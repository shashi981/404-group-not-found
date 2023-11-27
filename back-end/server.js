//TODO check connection with front end
const mysql = require("mysql2")
const express = require("express");
const https = require("https")
const fs = require("fs");
const moment = require("moment")
const cron = require("node-cron")
const admin = require("firebase-admin")
const WebSocket = require("ws")

let onlineUsers = {}; // { userId: socketId, ... }
let onlineDieticians = {}; // { dieticianId: socketId, ... }

let userConnections = {};
let dieticianConnections = {};

//app
const app = express()
app.use(express.json());

const UPCKeypath='./UPCKey.txt'
let UPCAPIKey= "?apikey="
const UPCAPIURL= "https://api.upcdatabase.org/product/"

fs.readFile(UPCKeypath, 'utf8', (err, data) => {
  if (err) {
    console.error('Error reading the file:', err)
  } else {
    UPCAPIKey += data
  }
})

//use this to get more recipes when have the time to do so
/*
const RecipeKeypath='./RecipeKey.txt'
let RecipeAPIKey='&apiKey='
const RecipeAPIURL= 'https://api.spoonacular.com/recipes/findByIngredients?number=5&ranking=1&ingredients='

fs.readFile(RecipeKeypath, 'utf8', (err, data) => {
  if (err) {
    console.error('Error reading the file:', err)
  } else {
    RecipeAPIKey += data
  }
})*/

//change this to maybe minute or hourly for testing
//const schedule='*/20 * * * *' // M4 submission use
const schedule = '0 0 * * *' //per daily

const serviceAccount = require('./grocerymanager_firebase.json')

// Initialize the app with appropriate configurations
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
})

//server use
const con = mysql.createConnection({
  host: "localhost",
  port: "3306",
  user: "404GroupNotFound",
  password: "404Group",
  database: "grocerymanger"
});


const certs = {
  key: fs.readFileSync('./key.pem'),
  cert: fs.readFileSync('./certificate.pem')
}

const server = https.createServer(certs, app)

const port = process.env.NODE_ENV === 'test' ? 3001 : 443;

if (process.env.NODE_ENV !== 'test') {
  server.listen(port, () => {
    console.log(`Server is running on port ${port}`);
  });
}

const ws = new WebSocket.Server({server})

//ChatGPT usage: No
function database_error(response, error) {
  response.status(500).send('Error querying the database'+error)
}

//ChatGPT usage: No
function query_success(response, message){
  response.status(200).send(message)
}

//ChatGPT usage: Partial
ws.on('message', async (message) => {
  console.log('Received:', message);

  try {
    let parsedMessage = JSON.parse(message);
    // Store in the database
    let query = 'INSERT INTO CHAT (UID, DID, Text, Time, FROM_USER) VALUES (?, ?, ?, NOW(), ?)';
    await con.promise().query(query, [parsedMessage.UID, parsedMessage.DID, parsedMessage.Text, parsedMessage.FROM_USER]);

    console.log('Message saved');
    ws.send(JSON.stringify({ type: 'success', message: 'Message saved successfully' }));

    // Prepare notification
    let targetTable = parsedMessage.FROM_USER === 1 ? 'DIETICIAN' : 'USERS';
    let targetID = parsedMessage.FROM_USER === 1 ? parsedMessage.DID : parsedMessage.UID;
    let store= targetTable === 'DIETICIAN' ? 'DID' : 'UID'
    let queryToken = 'SELECT MessageToken FROM ' + targetTable +' WHERE ' + store+'=?'
    let [tokensResult] = await con.promise().query(queryToken, [targetID]);
    let token = tokensResult[0]?.MessageToken;

    if (token) {
      let notificationText = `You have a new message from ${parsedMessage.FROM_USER === 1 ? 'user' : 'dietitian'} with ID ${targetID}`;
      let messagePayload = {
        notification: {
          title: 'Chat notification',
          body: notificationText,
        },
        token
      };

      console.log(messagePayload);
      Messaging(messagePayload);
    }

    // Forward message
    if (parsedMessage.FROM_USER === 1) {
      forwardMessageToDietician(parsedMessage.DID, message);
    } else {
      forwardMessageToUser(parsedMessage.UID, message);
    }
  } catch (error) {
    console.error('Database error:', error);
    ws.send(JSON.stringify({ type: 'error', message: 'Error saving the message' }));
  }
});

// When the client closes the connection
ws.on('close', () => {
  console.log('Client disconnected');
});


//Helper functions for ws
//ChatGPT usage: Partial
function forwardMessageToDietician(DID, message) {
    const dieticianWs = dieticianConnections[DID];
    
    if (dieticianWs) {
        dieticianWs.send(message)
    } else {
        console.log(`No active connection found for dietician with ID ${DID}`)
    }
}

//ChatGPT usage: Partial
function forwardMessageToUser(UID, message) {
    const userWs = userConnections[UID]
    
    if (userWs) {
        userWs.send(message)
    } else {
        console.log(`No active connection found for user with ID ${UID}`)
    }
}
//End Helper functions for ws

//END POINTS FOR WS
//ChatGPT usage: Partial
app.get('/get/availableDieticians', (req, res) => {
    const query = 'SELECT DID, FirstName, LastName, Email, ProfileURL FROM DIETICIAN';

    con.query(query, (error, results) => {
        if (error) {
            database_error(res, error);
        } else {
            res.json(results);
        }
    });
});

//ChatGPT usage: Partial
app.get('/get/usersForDietician/:dieticianId', (req, res) => {
  const dieticianId = req.params.dieticianId;

  // Adjusted the query to join with the USERS table and fetch user details
  const query = `
      SELECT DISTINCT U.*
      FROM USERS U
      JOIN CHAT C ON U.UID = C.UID
      WHERE C.DID = ?
  `;

  con.query(query, [dieticianId], (error, results) => {
      if (error) {
          database_error(res, error); // Assuming database_error is a function that handles database errors
      } else {
          res.json(results);
      }
  });
});

//ChatGPT usage: Partial
app.get('/get/messageToken/:DID', (req, res) => {
  const DID = req.params.DID; // Extract the DID from the URL

  // Construct and execute a MySQL query to retrieve the MessageToken
  const query = 'SELECT MessageToken FROM DIETICIAN WHERE DID = ?';
  con.query(query, [DID], (err, results) => {
      if (err) {
          console.error('Error executing MySQL query: ' + err);
          res.status(500).send('Internal Server Error');
      } else if (results.length === 0) {
          res.status(404).send('Dietitian not found');
      } else {
          res.json({ MessageToken: results[0].MessageToken });
      }
  });
});

// Endpoint to retrieve chat history between a user and a dietician
//ChatGPT usage: Partial
app.get('/get/chatHistory/:UID/:DID', (req, res) => {
    const UID = req.params.UID;
    const DID = req.params.DID;
    const limit = 10;  // max number of messages to return

    const query = 'SELECT * FROM CHAT WHERE UID = ? AND DID = ? ORDER BY Time DESC LIMIT ?';

    con.query(query, [UID, DID, limit], (error, results) => {
        if (error) {
            database_error(res, error);
        } else {
            res.json(results);
        }
    });
});

//END POINTS WS END

// END POINTS FOR SOCKET - BEGIN

//New endpoint to get the actiev users fro the chat function
//ChatGPT usage: Partial
// app.get('/get/activeUsers', (req, res) => {
//   const activeUsersList = Object.keys(onlineUsers);
//   const activeDieticiansList = Object.keys(onlineDieticians);
//   res.json({ users: activeUsersList, dieticians: activeDieticiansList });
// });

//ChatGPT usage: Partial
// app.get('/get/chatHistory', async (req, res) => {
//   const userId = req.query.userId;
//   const dieticianId = req.query.dieticianId;

//   if (!userId || !dieticianId) {
//       return res.status(400).send("User ID and Dietician ID are required");
//   }

//   try {
//       const query = 'SELECT * FROM CHAT WHERE UID = ? AND DID = ?';
//       const [rows] = await con.promise().query(query, [userId, dieticianId]);
//       res.json(rows);
//   } catch (error) {
//       console.error("Error fetching chat history:", error);
//       res.status(500).send("Internal server error");
//   }
// });

// END POINTS FOR SOCKET ENDS 


//get users
//done
//ChatGPT usage: Partial
app.post("/get/users", async (req, res) => {
    const email = req.body.p1;
    const token = req.body.p2;

    const updateQuery = 'UPDATE USERS SET MessageToken = ? WHERE Email = ?;';
    await con.promise().query(updateQuery, [token, email]);

    const selectQuery = 'SELECT * FROM USERS WHERE Email = ?;';
    const [results] = await con.promise().query(selectQuery, [email]);

    if (results.length === 0) {
      return res.json({});
    }

    const user = results[0];
    const responseObject = {
      UID: user.UID,
      FirstName: user.FirstName,
      LastName: user.LastName,
      Email: user.Email,
      ProfileURL: user.ProfileURL,
    };

    console.log("USER GET");
    res.json(responseObject);
});


//add users
//done
////ChatGPT usage: Partial
app.post("/add/users", async (req,res)=>{
  try{
    const FirstName=req.body.p1
    const LastName=req.body.p2
    const Email=req.body.p3
    const ProfileURL=req.body.p4
    const Token=req.body.p5

    const query = 'INSERT INTO USERS (FirstName, LastName, Email, ProfileURL, MessageToken) VALUES (?, ?, ?, ?, ?);'
    const query2='SELECT UID FROM USERS WHERE Email=?'

    //const [results1] = await con.promise().query(query, [FirstName, LastName, Email, ProfileURL, Token])
    await con.promise().query(query, [FirstName, LastName, Email, ProfileURL, Token])
    const [results2] = await con.promise().query(query2, [Email])

    console.log("USER ADDED") 

    const formattedResults = results2.map((r) => {
        return `${r.UID}`
    });
    query_success(res, formattedResults)
      
  } catch(error){
      console.error('Error:', error)
      database_error(res, error.stack)
  }
})

//delete users
//done
//ChatGPT usage: No
app.get("/delete/users", async (req,res)=>{
  try{
    const UID=req.query.p1

    const query = 'DELETE FROM USERS WHERE UID=?;'
    const [results] = await con.promise().query(query, [UID])
  
      if (results.affectedRows === 0) {
        console.log('No rows were deleted. Check the values in your DELETE query.')
        res.send('No rows were deleted. Check the values in your DELETE query.')
      } else {
        console.log('SUCCESS DELETED USER')
        query_success(res, 'DELETED USER')
      }
    } catch(error){
        console.error('Error:', error)
        database_error(res, error.stack)
    }
})

//update user
//done
//ChatGPT usage: No
app.get("/update/users", async (req,res)=>{
  try{
    const UID=req.query.p1
    const FirstName=req.query.p2
    const LastName=req.query.p3
    const Email=req.query.p4
    const ProfileURL=req.query.p5

    const query = 'UPDATE USERS SET FirstName=?, LastName=?, Email=?, ProfileURL=? WHERE UID=?;'
    await con.promise().query(query, [FirstName, LastName, Email, ProfileURL, UID])
     //const [results] = await con.promise().query(query, [FirstName, LastName, Email, ProfileURL, UID])

    console.log('SUCCESS Update User') 
    query_success(res, 'Success update user')

  } catch(error){
      console.error('Error:', error)
      database_error(res, error.stack)
  }
})

//get items
//done
//ChatGPT usage: Partial
app.get("/get/items", async (req,res)=>{
  try{
    const UID=req.query.p1

    const query = 'UPDATE OWNS o JOIN (SELECT o1.UPC,o1.UID,o1.ExpireDate,o1.ItemCount,ROW_NUMBER() OVER (PARTITION BY o1.UID ORDER BY o1.ExpireDate, o1.UPC ASC) AS NewItemID FROM OWNS o1 WHERE o1.UID =?) AS result ON o.UPC = result.UPC AND o.UID = result.UID AND o.ExpireDate=result.ExpireDate And o.ItemCount=result.ItemCount SET o.ItemID = result.NewItemID WHERE o.UID=?;'
    const query2 = 'SELECT DISTINCT g.Name, g.Brand, o.UPC, o.ExpireDate, o.ItemCount, o.ItemID FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ? ORDER BY o.ItemID ASC;'

    //const [result1] = await con.promise().query(query, [UID, UID])
    await con.promise().query(query, [UID, UID])
    const [result2] = await con.promise().query(query2, [UID])        

    if (result2.length === 0) {
      return res.json({})
    }

    const items = result2.map((result) => {
    const formattedDate = new Date(result.ExpireDate).toLocaleDateString();
      return {
        UPC: result.UPC,
        Name: result.Name, 
        Brand: result.Brand,
        ExpireDate: formattedDate,
        ItemCount: result.ItemCount,
        ItemID: result.ItemID
      }
    })

    console.log('SUCCESS Get items') 

    res.json(items)
      
  } catch (error) {
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//add items
//done
//ChatGPT usage: Partial
app.post("/add/items", async (req, res) => {
  try {
    const UID = req.body.p1;
    const UPCs = req.body.p2;
    const ExpireDates = req.body.p3;
    const ItemCounts = req.body.p4;

    const currentDate = new Date();
    const currentDateString = currentDate.toISOString().split('T')[0];
    console.log(currentDateString);

    let returnStatement = '';
    const values = [];

    if (UPCs.length !== ExpireDates.length || UPCs.length !== ItemCounts.length) {
      return res.status(400).send('Array lengths must match.');
    }

    for (let i = 0; i < UPCs.length; i++) {
      const UPC = UPCs[i];
      const [groceryResults] = await con.promise().query('SELECT * FROM GROCERIES WHERE UPC = ?', [UPC]);

      if (groceryResults.length === 0) {
        try {
          const productData = await fetchDataFromAPI(UPCAPIURL + UPC + UPCAPIKey);
          if (productData.success) {
            console.log(productData);
            const insertGroceryQuery = 'INSERT INTO GROCERIES (UPC, Name, Brand, Category) VALUES (?, ?, ?, ?)';
            await con.promise().query(insertGroceryQuery, [UPC, productData.title, productData.brand, productData.title]);
            values.push([UID, UPC, ExpireDates[i], ItemCounts[i], currentDate]);
          } else {
            returnStatement += UPC + " ";
            console.error(`Product not found for UPC: ${UPC}`);
          }
        } catch (error) {
          console.error('Error fetching product data:', error);
        }
      } else {
        values.push([UID, UPC, ExpireDates[i], ItemCounts[i], currentDate]);
      }
    }

    if (values.length > 0) {
      const insertOwnsQuery = 'INSERT INTO OWNS (UID, UPC, ExpireDate, ItemCount, PurchaseDate) VALUES ?';
      await con.promise().query(insertOwnsQuery, [values]);
    }

    if (returnStatement === '') {
      console.log('SUCCESS ADDED ITEMS');
      res.json({ message: 'SUCCESS ADDED ITEMS' });
    } else {
      console.log('Values not added for UPCs: ' + returnStatement);
      res.json({ message: 'Values not added for UPCs: ' + returnStatement });
    }
  } catch (error) {
    console.error('Error in /add/items:', error);
    res.status(500).send('Internal Server Error');
  }
});


//get data from the UPC API
//ChatGPT usage: Yes
function fetchDataFromAPI(url) {
  return new Promise((resolve, reject) => {
    https.get(url, (response) => {
      let data = ''

      response.on('data', (chunk) => {
        data += chunk
      });

      response.on('end', () => {
        try {
          const jsonData = JSON.parse(data);
          resolve(jsonData)
        } catch (error) {
          reject(error)
        }
      })

      response.on('error', (error) => {
        reject(error)
      })
    })
  })
}

//add items manually
//done
//ChatGPT usage: No
app.post("/add/items_man", async (req,res)=>{
  try{
    const UID=req.body.p1
    const UPC = req.body.p2 // should be -1
    const ExpireDate = req.body.p3 
    const ItemCount = req.body.p4
    const ItemName = req.body.p5 

    if ( ExpireDate.length !== ItemCount.length || ItemCount.length !== ItemName.length) {
      return res.status(400).send('Arrays should have the same length')
    }

    const currentDate = new Date()
    const currentDateString = currentDate.toISOString().split('T')[0]
    console.log(currentDateString)
    
    const values = []
    const store=[]
    for (let i = 0; i <ItemName.length; i++) {
      if(i<UPC.length-1){
        store.push(([UPC, ItemName[i],]))
        values.push(([UID, UPC, ExpireDate[i], ItemCount[i],ItemName[i], currentDate]))
      }
      else{
        store.push(([UPC, ItemName[i]]))
        values.push(([UID, UPC, ExpireDate[i], ItemCount[i], ItemName[i], currentDate]))
      }
    }
    console.log(values)

    const query = 'INSERT IGNORE INTO GROCERIES (UPC, Name) VALUES ?'
    const query2 = 'INSERT INTO OWNS (UID, UPC, ExpireDate, ItemCount, Name, PurchaseDate) VALUES ?'

    //const [results] = await con.promise().query(query, [store])
    await con.promise().query(query, [store])
    await con.promise().query(query2, [values])
    //const [results2] = await con.promise().query(query2, [values])

    console.log('SUCCESS ADDED items') 
    query_success(res, 'SUCCESS ADDED ITEMS MANUAL')
   
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//delete items
//done
//ChatGPT usage: Partial
app.post("/delete/items", async (req,res)=>{
  try{
    const UID=req.body.p1
    const ItemID=req.body.p2 

    const query = 'DELETE FROM OWNS WHERE UID= ? AND ItemID IN (?);'

    const [results] = await con.promise().query(query, [UID, ItemID])

    if (results.affectedRows === 0) {
      console.log('No rows were deleted. Check the values in your DELETE query.')
      res.send('No rows were deleted. Check the values in your DELETE query.')
    } else {
      console.log('SUCCESS DELETED items')
      query_success(res, 'DELETED ITEM')
    }
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//done
//update items
//ChatGPT usage: Partial
app.post("/update/items", (req,res)=>{
  try{
    const UID=req.body.p1
    const ItemID=req.body.p2 
    const UPC = req.body.p3 
    const ExpireDate = req.body.p4 
    const ItemCount = req.body.p5 

    if (UPC.length !== ExpireDate.length || UPC.length !== ItemCount.length || ItemID.length !== UPC.length) {
      return res.status(400).send('Arrays should have the same length')
    }
    
    const values = [];
    for (let i = 0; i <UPC.length; i++) {
      values.push([ExpireDate[i], ItemCount[i], UID, UPC[i], ItemID[i]])
    }
    console.log(values)
    const query = 'UPDATE OWNS SET ExpireDate=?, ItemCount=? WHERE UID=? AND UPC=? AND ItemID=?;'
    
    values.forEach(async (values) => {
      const executedQuery = mysql.format(query, values)

      console.log('Executed Query:', executedQuery)
      //const [results1] = await con.promise().query(executedQuery)
          
      await con.promise().query(executedQuery)
      console.log('SUCCESS Updated items')
    })

  query_success(res, 'SUCCESS Updated items')
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//add pref
//done
//ChatGPT usage: No
app.post("/add/pref", async (req,res)=>{

  try{
    const UID=req.body.p1
    const Pref = req.body.p2 
    const values = [];

    for (let i = 0; i <Pref.length; i++) {
      if(i<Pref.length-1){
        values.push(([UID, Pref[i],]));
      }
      else{
        values.push(([UID, Pref[i]]));
      }
    }
    console.log(values)
    const query = 'INSERT INTO PREFERENCE (UID, Pref) VALUES ?'
    await con.promise().query(query, [values])

    //const [results] = await con.promise().query(query, [values])

    console.log('SUCCESS ADDED Pref') 
    query_success(res, 'SUCCESS ADDED Pref')
  
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//delete pref
//done
//ChatGPT usage: No
app.get("/delete/pref", async (req,res)=>{
  try{
    const UID=req.query.p1

    const query = 'DELETE FROM PREFERENCE WHERE UID= ?'
    await con.promise().query(query, [UID])

    //const [results] = await con.promise().query(query, [UID])
    
    console.log('SUCCESS DELETE Pref') 
    query_success(res, 'SUCCESS DELETE Pref')
    
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//get preference
//done
//ChatGPT usage: No
app.get("/get/pref", async (req,res)=>{
  try{
    const UID=req.query.p1

    const query = 'SELECT Pref FROM PREFERENCE WHERE UID= ?'
    const [results] = await con.promise().query(query, [UID])

    if (results.length === 0) {
      return res.json({});
    }

      const formattedResults = results.map((result) => {
        return {
          Pref: result.Pref
        }
      })
      console.log('SUCCESS select Pref') 
      res.json(formattedResults)
      
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//get available preference from database
//done
//ChatGPT usage: Partial
app.get("/get/pref_list", async (req,res)=>{
  try{
    const query = 'SELECT * FROM PREF_LIST'
    const [results] = await con.promise().query(query)

    console.log('SUCCESS show Pref_list') 
    const formattedResults = results.map((result) => {
      return {Pref:result.Pref};
    });
    res.json(formattedResults)
  } catch(error){
      console.error('Error:', error);
      database_error(res, error.stack);
  }
})

//request for being a dietician
//done
//ChatGPT usage: No
app.get("/add/dietReq", async (req,res)=>{

  try{
    const UID=req.query.p1
    const query = 'INSERT INTO DIETICIAN_REQUEST (UID) VALUES (?)'
    await con.promise().query(query, [UID])

    //const [result1] = await con.promise().query(query, [UID])

    console.log('Request for being dietician made!!' + con.threadId)
    query_success(res, 'Request for being dietician made!!')
    
  } catch(error){
      console.error('Error:', error);
      database_error(res, error.stack);
  }
})

//get all request for being a dietician
//done
//ChatGPT usage: Partial
app.get("/get/dietReq", async (req,res)=>{
  try {
    const query = 'SELECT d.RID, u.UID, u.FirstName, u.LastName, u.Email, u.ProfileURL FROM DIETICIAN_REQUEST d, USERS u WHERE u.UID=d.UID'
    const [results] = await con.promise().query(query)
    
    if(results.length==0){
      return res.json({});
    }

    const formattedResults = results.map((result) => {
      return {
        RID:result.RID,
        UID:result.UID,
        FirstName:result.FirstName,
        LastName:result.LastName,
        Email:result.Email,
        ProfileURL:result.ProfileURL,
      }
    });

    console.log('SUCCESS show list of being a dietician request');
    res.json(formattedResults)
    
  } catch (error) {
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})


//approve request for being a dietician, add to dietician table and remove the request
//done
//ChatGPT usage: Partial
app.get("/approve/dietReq", async (req,res)=>{
  try{
    const UID=req.query.p1
    console.log(UID)
    const query = 'INSERT INTO DIETICIAN (FirstName, LastName, Email, ProfileURL, MessageToken) SELECT u.FirstName, u.LastName, u.Email, u.ProfileURL, u.MessageToken FROM USERS u WHERE u.UID=?'
    const query2='DELETE FROM DIETICIAN_REQUEST WHERE UID=?'
    const query3='DELETE FROM USERS WHERE UID IN (?)'
    /*
    const [result1] = await con.promise().query(query, [UID])
    const [result2] = await con.promise().query(query2, [UID])
    const [result3] = await con.promise().query(query3, [UID])*/

    await con.promise().query(query, [UID])
    await con.promise().query(query2, [UID])
    await con.promise().query(query3, [UID])

    console.log('SUCCESS approve being dietician request') 
    query_success(res, 'SUCCESS approve being dietician request')
  
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//remove request for being a dietician 
//done
//ChatGPT usage: No
app.get("/remove/dietReq", async (req,res)=>{
  try{
    const UID=req.query.p1
    console.log(UID)

    const query2='DELETE FROM DIETICIAN_REQUEST WHERE UID=?'
    await con.promise().query(query2, [UID])

    //const [result2] = await con.promise().query(query2, [UID])

    console.log('SUCCESS delete being dietician request') 
    query_success(res, 'SUCCESS delete being dietician request')
  
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})


//get dieitician with the email and also update the firebase token as well
//done
//ChatGPT usage: No
app.post("/get/dietician", async (req,res)=>{
  try{
  const Email=req.body.p1
  const Token=req.body.p2

  const query = 'SELECT * FROM DIETICIAN WHERE Email=?;'
  const updatequery = 'UPDATE DIETICIAN SET MessageToken= ? WHERE Email=? ;'

  //const [update] = await con.promise().query(updatequery, [Token, Email])

  await con.promise().query(updatequery, [Token, Email]) 
  const [results] = await con.promise().query(query, [Email])

  if (results.length === 0) {
    return res.json({});
  }

  const dietician = results[0];

  const responseObject = {
    DID: dietician.DID,
    FirstName: dietician.FirstName,
    LastName: dietician.LastName,
    Email: dietician.Email,
    ProfileURL: dietician.ProfileURL,
  };
  console.log("DIETICIAN GET")

  res.json(responseObject);
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//give the user type of the email if exist: admin, dietician or user else return does not exist
//done
//ChatGPT usage: Partial
app.post("/get/users_type", async (req,res)=>{

  try {
    const Email = req.body.p1;

    const query1 = 'SELECT * FROM USERS WHERE Email=?'
    const [userResults] = await con.promise().query(query1, [Email])

    if (userResults.length > 0) {
      console.log('Entry exists as user')
      return query_success(res, 'User\n')
    }

    const query2 = 'SELECT * FROM DIETICIAN WHERE Email=?'
    const [dieticianResults] = await con.promise().query(query2, [Email]);

    if (dieticianResults.length > 0) {
      console.log('Entry exists as dietician')
      return query_success(res, 'Dietician\n')
    }

    const query3 = 'SELECT * FROM ADMIN WHERE Email=?'
    const [adminResults] = await con.promise().query(query3, [Email])

    if (adminResults.length > 0) {
      console.log('Entry exists as admin');
      return query_success(res, 'Admin\n')
    }

    console.log('This is an entry that does not exist')

    return query_success(res, 'Does not exist\n')
  } catch (error) {
    console.error('Error:', error)
    database_error(res, error.stack)
  }

})

//get recipe for items about to expiry based on preference and use uid
//done
//ChatGPT usage: Partial
app.get("/get/recipe", async (req,res)=>{
  try {
    // todo use the api to get extra recipes when the no recipe matches on db

    const UID=req.query.p1
    let storequery=''
    let query=''
    const store=[]

    const Prefquery = 'SELECT * FROM PREFERENCE WHERE UID=?'
    const [Preftemp] = await con.promise().query(Prefquery, [UID])
    const Pref = Preftemp.map((Prefresult) => Prefresult.Pref);
    
    console.log(Pref)
    const Itemsquery = 'SELECT DISTINCT g.Name FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ? AND o.AboutExpire=1'
    const [Itemstemp] = await con.promise().query(Itemsquery, [UID])
    const Expiryitems = Itemstemp.map((Items) => Items.Name);
    let excludeTable

    console.log(Expiryitems)
    if(Expiryitems.length === 0){
      return res.json({});
    }

    if(Pref.length !== 0){
      if(Pref.length === 1){
        if (Pref[0] === 'Vegetarian') {
          excludeTable = 'vegetarian';
        } else if (Pref[0] === 'Non-dairy') {
          excludeTable = 'nondairy';
        } else if (Pref[0] === 'Vegan') {
          excludeTable = 'vegan';
        }
        query ='SELECT store.RID FROM (SELECT * FROM ' +excludeTable + ') AS store WHERE LOWER(store.Ingredient) LIKE ? '
      }
      else{
        query = 'SELECT store.RID FROM ('

        for(let i=0; i< Pref.length; i++){
          let preference = Pref[i];
          let excludeTable = ''

          if (preference === 'Vegetarian') {
            excludeTable = 'vegetarian';
          } else if (preference === 'Non-dairy') {
            excludeTable = 'nondairy';
          } else if (preference === 'Vegan') {
            excludeTable = 'vegan';
          }

          if(i===0){
            query+="SELECT * FROM " +excludeTable + " "
          }
          else if(i<Pref.length-1){
            query+="INTERSECT SELECT * FROM " + excludeTable + " "
            
          }
          else{
            query+="INTERSECT SELECT * FROM " +excludeTable + ') AS store WHERE LOWER(store.Ingredient) LIKE ? '
            store.push(excludeTable)
          }
        }
        console.log(query)
      }
    }
    else{
      query = 'SELECT r.RID FROM RECIPE r WHERE LOWER(r.Ingredient) LIKE ? '
    }

  storequery=query
  if(Expiryitems.length === 1){
    const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[0]}%`+'\')')
    query=temp
    const tempquery= "SELECT s.RID FROM (" + query + ") AS s ORDER BY RAND() LIMIT 5"
    query=tempquery
  }
  else{
    for(let j=0; j< Expiryitems.length; j++){
      if(j === 0){
        const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[j]}%`+'\')')
        query=temp
      }
      else if(j< Expiryitems.length-1){
        query += "INTERSECT " + storequery
        const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[j]}%`+'\')')
        query=temp
      }
      else{
        query += "INTERSECT " + storequery
        const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[j]}%`+'\')')
        query=temp
        const tempquery= "SELECT s.RID FROM (" + query + ") AS s ORDER BY RAND() LIMIT 5"
        query=tempquery
      }
    }
  }
  console.log(query)

    const [results] = await con.promise().query(query)
    let formattedResults

    const formattedArray = results.map((result) => result.RID);
    console.log(formattedArray)

    if(formattedArray.length===0){
      return res.json({})
    }
    else{
      const query2 = 'SELECT * FROM RECIPE WHERE RID IN (?)'
      const [result2] = await con.promise().query(query2, [formattedArray])

      formattedResults = result2.map((result) => ({
        RID: result.RID,
        Ingredient: result.Ingredient,
        Amount: result.Amount,
      }))
    }

    console.log(formattedResults)
    const structuredData = {};

    for (const result of formattedResults) {
      if (!structuredData[result.RID]) {
        structuredData[result.RID] = {
          RID: result.RID,
          ingredients: [],
        }
      }
    
      structuredData[result.RID].ingredients.push({
        Ingredient: result.Ingredient,
        Amount: result.Amount,
      })
    }
    
    const jsonData = Object.values(structuredData);
    
    console.log(jsonData)
    res.json(jsonData)

  } catch (error) {
    console.error('Error:', error);
    database_error(res, error.stack);
  }
})

// get recipe info using rid
//done
//ChatGPT usage: No
app.get("/get/recipe_info", async (req,res)=>{
  try{
    
    const RID=req.query.p1 ? req.query.p1.split(',') : []

    const query = 'SELECT * FROM RECIPE_INFO WHERE RID IN (?)'
    const [results] = await con.promise().query(query, [RID])

    if (results.length === 0) {
      return res.json({});
    }

    const formattedResults = results.map((result) => {
      return {
        RID: result.RID,
        Name: result.Rname, 
        Instruction: result.Instruction,
        YTLink: result.YoutubeLInk
      }
    })

      console.log('SUCCESS return recipe_info') 
      console.log(formattedResults)
      res.json(formattedResults)
      
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//the schedule for shopping reminder algorithm 
cron.schedule(schedule, () => {
  console.log('Cron job triggered for shoppingdata')
  processShoppingData()
})

// Define a function to process shopping data and generate reminders
//done 
//ChatGPT usage: Partial
async function processShoppingData() {
  try{
    console.log("algorithm tiggered")
    // User-defined settings
    const reminderPeriodDays = 5
    const numberOfVisits = 2 // This can be changed

    // Store item purchase history

    // Initialize current date
    const currentDate = moment()

    // Initialize the expected run-out date for each item

    //Get all users UID and repeat for all UID
    const queryUsers='SELECT UID, MessageToken FROM USERS;'
    const [U] = await con.promise().query(queryUsers)
    const UIDTokenArray = U.map((temp) => {
      return {
        UID:temp.UID,
        Token:temp.MessageToken
      }
    });

    UIDTokenArray.forEach(async (entry)=>{
    // NEED TO UPDATE
    const purchaseHistory = {}
    const expectedRunOutDates = {}
    // Query to retrieve shopping data from the database
      const UID=entry.UID
      console.log(UID)
      console.log(entry.Token)
      const query = 'SELECT g.Name, o.ItemCount, o.PurchaseDate FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ?'
      const [result] = await con.promise().query(query, [UID])

      const shoppingData=result.map((store)=>{
        return {
          item:store.Name,
          quantity:store.ItemCount,
          purchaseDate:store.PurchaseDate
        }
      })

      console.log(shoppingData)

      // Populate purchase history, also detecting anomalies
      shoppingData.forEach((shoppingEntry) => {
        const item = shoppingEntry.item
        const quantity = shoppingEntry.quantity
        const anomalyThreshold = quantity * 3 // Anomaly detection threshold (e.g., a quantity higher than this threshold is considered an anomaly)
       // const purchaseDate = moment(shoppingEntry.purchase_date, 'YYYY-MM-DD').toDate();
        const purchaseDate=shoppingEntry.purchaseDate

        // Check for anomalies based on the threshold
        if (quantity > anomalyThreshold) {
          console.log(`Anomaly detected for ${item} on ${purchaseDate}: Quantity ${quantity} exceeds threshold.`);
          return;
        }

        // Updating purchase history
        if (item in purchaseHistory) {
          purchaseHistory[item].push(purchaseDate)
        } else {
          purchaseHistory[item] = [purchaseDate]
        }

        // Calculate expected run-out date based on time frame
        if (!(item in expectedRunOutDates)) {
          expectedRunOutDates[item] = moment(purchaseDate).add(numberOfVisits, 'weeks').toDate()
        }
      })
    
      // Generate reminders
      for (const item in purchaseHistory) {
        //const purchaseDates = purchaseHistory[item]

        // Calculate the average purchase frequency
        //const purchaseFrequency = purchaseDates.length / numberOfVisits // Number of times purchased per week

        // Calculate the expected run-out date
        const expectedRunOutDate = expectedRunOutDates[item]

        // Calculate the reminder date
        const reminderDate = moment(expectedRunOutDate).subtract(reminderPeriodDays, 'days')

        // Check if it's time to send a reminder 
        if (currentDate.isSameOrAfter(reminderDate)) {
          console.log(`Reminder: Buy ${item} in the next ${reminderPeriodDays} days.`)
          //todo maybe change this to send message for evrey two??
          const text=`Reminder: Buy ${item} in the next ${reminderPeriodDays} days.`

          const jsonContent = {
            notification: {
              title: 'Reminder for buying items',
              body: text,
            },
            token: entry.Token,
          }
          console.log("\n")
          console.log(jsonContent)
          Messaging(jsonContent)
        }
      }
    })
  }catch(error){
    console.error('Error:', error)
    //database_error(res, error.stack)
  }
}

// Call the function to process shopping data and generate reminders
//expiry date reminders schedule
cron.schedule(schedule, () => {
  console.log('Cron job triggered expiry date reminder')
  SendExpiryReminder()
});

//send notification to uses for items that is about to expire in 2 days or already expired for x days
//done
//ChatGPT usage: Partial
async function SendExpiryReminder(){
  //get all users that have items about to expiry
  try{
    const currentDate = new Date()

    const DisableSafeMode='SET SQL_SAFE_UPDATES = 0;'
    const queryupdate='UPDATE OWNS SET AboutExpire = CASE WHEN DATEDIFF(ExpireDate, CURDATE()) <= 2 THEN 1 ELSE 0 END;'
    const EnableSafeMode='SET SQL_SAFE_UPDATES = 1;'
    /*
    const [disable] = await con.promise().query(DisableSafeMode)
    const [results] = await con.promise().query(queryupdate)
    const [enable] = await con.promise().query(EnableSafeMode)*/

    await con.promise().query(DisableSafeMode)
    await con.promise().query(queryupdate)
    await con.promise().query(EnableSafeMode)

    const queryselect='SELECT DISTINCT UID FROM OWNS WHERE AboutExpire=1'
    const [store] = await con.promise().query(queryselect)
    const UIDArray = store.map((temp) => temp.UID);
    console.log(UIDArray)
    
    //for each users
    for(let i=0; i<UIDArray.length; i++){
      const UID=UIDArray[i]
      const queryToken='SELECT u.MessageToken FROM USERS u WHERE UID=?'
      const queryItems='SELECT DISTINCT g.Name, g.Brand, o.ExpireDate, o.ItemCount FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ? AND o.AboutExpire=1;'

      //send notification for all items and the date left
      //get the token using uid and call firebase using that
      // Define the message payload

      const t=await con.promise().query(queryToken, [UID])
      const [Items]=await con.promise().query(queryItems, [UID])
      const token=t[0].map((storetoken) => storetoken.MessageToken)
      console.log(t)
      console.log(token)

      const ItemArray = Items.map((item) => {
        const itemData = {
          Name: item.Name,
          DatesToExpire: Math.ceil((item.ExpireDate - currentDate) / (1000 * 60 * 60 * 24)), // Calculate days until expiration
          Quantity: item.ItemCount
        }
      
        if (item.Brand !== null) {
          itemData.Brand = item.Brand
        }
      
        return itemData
      })

      const itemsText = ItemArray.reduce((accumulator, item, index) => {
        const brandText = item.Brand ? `Brand: ${item.Brand}\n` : ''
        const daysToExpire = item.DatesToExpire
      
        let daysToExpireText = ''
      
        if (daysToExpire >= 0) {
          daysToExpireText = `Days to Expire: ${daysToExpire}`
        } else {
          daysToExpireText = `Item Already Expired for ${-daysToExpire} days`
        }
      
        const itemText = `Name: ${item.Name}\n${brandText}${daysToExpireText}\nQuantity: ${item.Quantity}\n`

        if (index % 2 === 0) {
          accumulator.push(itemText)
        } else {
          const combinedText = accumulator.pop() + itemText
          accumulator.push(combinedText)
        }

        return accumulator
      }, [])

      const messages = itemsText.map(text => {
        return{
          notification: {
            title: 'Reminder for items about to expire',
            body: 'Items About to Expire or already expired:\n'+ text,
          },
          token: token[0], 
        }
    })
      console.log(messages)
      messages.forEach(message => {
        Messaging(message);
      })
    }
  }catch(error){
    console.error('Error:', error)
    //database_error(res, error.stack)
  }
}

//the firebase notification function 
//done
//ChatGPT usage: No
async function Messaging(message){
// Send the message
  admin.messaging().send(message)
    .then((response) => {
      console.log('Successfully sent message:', response);
    })
    .catch((error) => {
      console.log('Error sending message:', error);
    });
}

module.exports = app; 
