//TODO check connection with front end
const mysql = require('mysql2');
const express = require("express")
const https = require('https')
const fs = require('fs')

const app=express()
app.use(express.json())

const UPCAPIKey='?apikey=05E1D91D8E518F2F15B235B4E473F34F'
const UPCAPIURL= 'https://api.upcdatabase.org/product/'

//local testing use
/*
const con = mysql.createConnection({
  host: "",
  port: "3306",
  user: "root",
  password: "",
  database: 'grocerymanger'
});

const server=app.listen(8081,"0.0.0.0", (req,res)=>{
  const host=server.address().address
  const port=server.address().port

  console.log("%s %s", host, port)

})
*/

//server use
const con = mysql.createConnection({
  host: "localhost",
  port: "3306",
  user: "404GroupNotFound",
  password: "404Group",
  database: 'grocerymanger'
});


const certs = {
  key: fs.readFileSync('./key.pem'),
  cert: fs.readFileSync('./certificate.pem')
}

const server = https.createServer(certs, app)

server.listen(443, () => {
  console.log(`Server is running on port 443`)
})


function database_error(response, error) {
  response.status(500).send('Error querying the database'+error)
}

function query_success(response, message){
  response.status(200).send(message)
}

//get users
//done
app.get("/get/users", async (req,res)=>{
  try{
  let Email=req.query.p1
  const query = 'SELECT * FROM USERS WHERE Email=?;'

  const [results] = await con.promise().query(query, [Email])
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
  
    /*con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      /*const formattedResults = results.map((result) => {
        return `${result.UID}\t${result.FirstName}\t${result.LastName}\t${result.Email}\t${result.ProfileURL}`;
      })
      query_success(res, formattedResults)*/
   // })
  //})

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
  console.log("USER GET")

  res.json(responseObject);
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//done
app.post("/add/users", async (req,res)=>{
  try{

  let FirstName=req.body.p1
  let LastName=req.body.p2
  let Email=req.body.p3
  let ProfileURL=req.body.p4

  const query = 'INSERT INTO USERS (FirstName, LastName, Email, ProfileURL) VALUES (?, ?, ?, ?);'
  const query2='SELECT UID FROM USERS WHERE Email=?'

  const [results1] = await con.promise().query(query, [FirstName, LastName, Email, ProfileURL])
  const [results2] = await con.promise().query(query2, [Email])

  /*
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    
    /*con.query(query, [FirstName, LastName, Email, ProfileURL], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
    })*/

    /*con.query(query2, [Email], (err, results2, fields2) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }*/
      console.log('USER ADDED') 
      const formattedResults = results2.map((r) => {
        return `${r.UID}`;
      });
      query_success(res, formattedResults)
    //})
  //})
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//delete users
//done
app.get("/delete/users", async (req,res)=>{
  try{

    let UID=req.query.p1

    const query = 'DELETE FROM USERS WHERE UID=?;'

    const [results] = await con.promise().query(query, [UID])
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      database_error(res, err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    
    /*con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      if (results.affectedRows === 0) {
        console.log('No rows were deleted. Check the values in your DELETE query.')
        res.send('No rows were deleted. Check the values in your DELETE query.')
      } else {
        console.log('SUCCESS DELETED USER')
        query_success(res, 'DELETED USER')
      }
   // })
  //})
    } catch(error){
        console.error('Error:', error)
        database_error(res, error.stack)
    }
})

//update user
app.get("/update/users", async (req,res)=>{
  try{
    let UID=req.query.p1
    let FirstName=req.query.p2
    let LastName=req.query.p3
    let Email=req.query.p4
    let ProfileURL=req.query.p5

    const query = 'UPDATE USERS SET FirstName=?, LastName=?, Email=?, ProfileURL=? WHERE UID=?;'
    

    const [results] = await con.promise().query(query, [FirstName, LastName, Email, ProfileURL, UID])
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    
    
    
    /*con.query(query, [FirstName, LastName, Email, ProfileURL, UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }*/
      console.log('SUCCESS Update User') 
      query_success(res, 'Success update user')
    //})
  //})
  } catch(error){
      console.error('Error:', error)
      database_error(res, error.stack)
  }
})

//get items
//done
app.get("/get/items", async (req,res)=>{
  try{

    let UID=req.query.p1

    const query = 'UPDATE OWNS o JOIN (SELECT o1.UPC,o1.UID,o1.ExpireDate,o1.ItemCount,ROW_NUMBER() OVER (PARTITION BY o1.UID ORDER BY o1.ExpireDate, o1.UPC ASC) AS NewItemID FROM OWNS o1 WHERE o1.UID =?) AS result ON o.UPC = result.UPC AND o.UID = result.UID AND o.ExpireDate=result.ExpireDate And o.ItemCount=result.ItemCount SET o.ItemID = result.NewItemID WHERE o.UID=?;'

    //const query2 = 'SELECT g.Name, g.Brand, o.UPC, o.ExpireDate, o.ItemCount, o.ItemID  FROM OWNS o, GROCERIES g WHERE o.UID=? && g.UPC=o.UPC ORDER BY o.ItemID ASC;'

    // not sure if this fix the issue, todo test it
    const query2 = 'SELECT DISTINCT g.Name, g.Brand, o.UPC, o.ExpireDate, o.ItemCount, o.ItemID FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ? ORDER BY o.ItemID ASC;'

    const [result1] = await con.promise().query(query, [UID, UID])
    const [result2] = await con.promise().query(query2, [UID])
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    
    /*con.query(query, [ID,ID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
    })*/
    
    /*con.query(query2, ID, (err, selectResults, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
*/
      /*
      result of result2Array
      });*/

      if (result2.length === 0) {
        return res.json({});
      }
      //add get brand and name
      const items = result2.map((result) => {
        const formattedDate = new Date(result.ExpireDate).toLocaleDateString();
        return {
          UPC: result.UPC,
          //UID: result.UID,
          Name: result.Name, 
          Brand: result.Brand,
          ExpireDate: formattedDate,
          ItemCount: result.ItemCount,
          ItemID: result.ItemID
        }
      })

      console.log('SUCCESS Get items') 

      res.json(items)
      //query_success(res, 'SUCCESS Get items:\n' + formattedResults.join('\n'))
    //})
  //})
  } catch (error) {
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//add items
//done
app.get("/add/items", async (req,res)=>{
  try{
    let UID=req.query.p1
    let UPC = req.query.p2 ? req.query.p2.split(',') : []
    let ExpireDate = req.query.p3 ? req.query.p3.split(',') : []
    let ItemCount = req.query.p4 ? req.query.p4.split(',') : []
    //let count=0;
    let returnstatement=''
    if (UPC.length !== ExpireDate.length || UPC.length !== ItemCount.length) {
      return res.status(400).send('Arrays should have the same length')
    }
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    const values = [];
    for (let i = 0; i <UPC.length; i++) {

      const query1 = 'SELECT * FROM GROCERIES WHERE UPC=?'
      const [userResults] = await con.promise().query(query1, [UPC[i]])

      if (userResults.length ==0) {
        console.log('The upc is not in the db')
        const url = UPCAPIURL + UPC[i] + UPCAPIKey
        let title
        //let description
        let brand
        /*https.get(url, (response) => {
          let data = '';
        
          // Event handler for receiving data
          response.on('data', (chunk) => {
            data += chunk;
          });
        
          response.on('end', () => {*/
            try {
              const jsonData = await fetchDataFromAPI(url);
              if (jsonData.success) {
                brand=jsonData.brand
                title=jsonData.title
                //description=jsonData.description

                console.log(jsonData);
                if(i<UPC.length-1){
                  values.push(([UID, UPC[i], ExpireDate[i], ItemCount[i],]));
                }
                else{
                  values.push(([UID, UPC[i], ExpireDate[i], ItemCount[i]]));
                }
                console.log(brand)
                console.log(title)
                num=UPC[i]
                console.log(num)
                const query2 = 'INSERT INTO GROCERIES(UPC, Name, Brand, Category) VALUES (?,?,?,?)'
                const [addgrocery] = await con.promise().query(query2, [num,title, brand, title ])
              }
              else{
                returnstatement += UPC[i]
                console.error('Product not found. Error code:', jsonData.error.code);
                console.error('Error message:', jsonData.error.message);
                
              }
            } catch (error) {
              console.error('Error parsing JSON:', error);
            }
          //});
        
          // Event handler for any errors
         // response.on('error', (error) => {
          //console.error('Request error:', error);
         // });
        //});
    }  
    }
    console.log(values)

    if(values.length>0){
      const query3 = 'INSERT INTO OWNS (UID, UPC, ExpireDate, ItemCount) VALUES ?'
      const [results] = await con.promise().query(query3, [values])
    }
    /*con.query(query, [values], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
     // console.log('SUCCESS ADDED items') 
     if(returnstatement === ''){
      query_success(res, 'SUCCESS ADDED ITEMS')
     }
     else{
      query_success(res, 'Values not added ' + returnstatement)
     }
    //})
 // })
  }
catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

function fetchDataFromAPI(url) {
  return new Promise((resolve, reject) => {
    https.get(url, (response) => {
      let data = '';

      response.on('data', (chunk) => {
        data += chunk;
      });

      response.on('end', () => {
        try {
          const jsonData = JSON.parse(data);
          resolve(jsonData);
        } catch (error) {
          reject(error);
        }
      });

      response.on('error', (error) => {
        reject(error);
      });
    });
  });
}

//todo finish this
app.post("/add/items_man", async (req,res)=>{
  try{
    //change to auto get a negative upc???
    let UID=req.body.p1
    let UPC = req.body.p2 // should be -1
    let ExpireDate = req.body.p3 //? req.query.p3.split(',') : []
    let ItemCount = req.body.p4 //? req.query.p4.split(',') : []
    let ItemName = req.body.p5 //? req.query.p5.split(',') : []
    if ( ExpireDate.length !== ItemCount.length || ItemCount.length !== ItemName.length) {
      return res.status(400).send('Arrays should have the same length')
    }
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    const values = []
    const store=[]
    for (let i = 0; i <ItemName.length; i++) {
      if(i<UPC.length-1){
        store.push(([UPC, ItemName[i],]))
        values.push(([UID, UPC, ExpireDate[i], ItemCount[i],ItemName[i]]))
      }
      else{
        store.push(([UPC, ItemName[i]]))
        values.push(([UID, UPC, ExpireDate[i], ItemCount[i], ItemName[i]]))
      }
    }
    console.log(values)

    const query = 'INSERT IGNORE INTO GROCERIES (UPC, Name) VALUES ?'
    const query2 = 'INSERT INTO OWNS (UID, UPC, ExpireDate, ItemCount, Name) VALUES ?'

    const [results] = await con.promise().query(query, [store])
    const [results2] = await con.promise().query(query2, [values])
    /*con.query(query, [values], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      console.log('SUCCESS ADDED items') 
      query_success(res, 'SUCCESS ADDED ITEMS MANUAL')
    //})
 // })
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//delete items
//done
app.post("/delete/items", async (req,res)=>{
  try{
  let UID=req.body.p1
  let ItemID=req.body.p2 //? req.query.p2.split(',') : []

  const query = 'DELETE FROM OWNS WHERE UID= ? AND ItemID IN (?);'
  
/*
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    
    /*con.query(query, [UID, ItemID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/

      const [results] = await con.promise().query(query, [UID, ItemID])

      if (results.affectedRows === 0) {
        console.log('No rows were deleted. Check the values in your DELETE query.')
        res.send('No rows were deleted. Check the values in your DELETE query.')
      } else {
        console.log('SUCCESS DELETED items')
        query_success(res, 'DELETED ITEM')
      }
    //})
  //})
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//done
//update items
app.post("/update/items", (req,res)=>{
  try{
  let UID=req.body.p1
  let ItemID=req.body.p2 //? req.query.p2.split(',') : []
  let UPC = req.body.p3 //? req.query.p3.split(',') : []
  let ExpireDate = req.body.p4 //? req.query.p4.split(',') : []
  let ItemCount = req.body.p5 //? req.query.p5.split(',') : []

  if (UPC.length !== ExpireDate.length || UPC.length !== ItemCount.length || ItemID.length !== UPC.length) {
    return res.status(400).send('Arrays should have the same length')
  }
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    const values = [];
    for (let i = 0; i <UPC.length; i++) {
      values.push([ExpireDate[i], ItemCount[i], UID, UPC[i], ItemID[i]])
    }
    console.log(values)
    const query = 'UPDATE OWNS SET ExpireDate=?, ItemCount=? WHERE UID=? AND UPC=? AND ItemID=?;'
    values.forEach(async (values) => {
      const executedQuery = mysql.format(query, values);
      console.log('Executed Query:', executedQuery);
      const [results1] = await con.promise().query(executedQuery)
      /*con.query(executedQuery, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack);
      return
      }*/
      console.log('SUCCESS Updated items');
      })
    //})
    query_success(res, 'SUCCESS Updated items')
  //})
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//add pref
//done
app.post("/add/pref", async (req,res)=>{

  try{
  let UID=req.body.p1
  let Pref = req.body.p2 //? req.query.p2.split(',') : []
 /* con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
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
    /*con.query(query, [values], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      const [results] = await con.promise().query(query, [values])
      console.log('SUCCESS ADDED Pref') 
      query_success(res, 'SUCCESS ADDED Pref')
    //})
  //})
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//delete pref
//done
app.post("/delete/pref", async (req,res)=>{
  try{
  let UID=req.query.p1
  let Pref = req.body.p2 //? req.query.p2.split(',') : []

  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    const query = 'DELETE FROM PREFERENCE WHERE UID= ? AND Pref IN (?)'

    const [results] = await con.promise().query(query, [UID, Pref])
    /*con.query(query, [UID, Pref], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      console.log('SUCCESS DELETE Pref') 
      query_success(res, 'SUCCESS DELETE Pref')
    //})
  //})
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//get preference
//done
app.get("/get/pref", async (req,res)=>{
  try{
    
  let UID=req.query.p1
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    const query = 'SELECT Pref FROM PREFERENCE WHERE UID= ?'
    const [results] = await con.promise().query(query, [UID])
    /*con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
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
      //query_success(res, 'SUCCESS select Pref ' + formattedResults.join('\n'))
    //})
  //})
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//get available preference from database
//done
app.get("/get/pref_list", async (req,res)=>{
  try{
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    const query = 'SELECT * FROM PREF_LIST'
    const [results] = await con.promise().query(query)
    /*con.query(query, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      console.log('SUCCESS show Pref_list') 
      const formattedResults = results.map((result) => {
        return {Pref:result.Pref};
      });
      //query_success(res, 'SUCCESS show Pref_list ' + formattedResults.join('\n'))
    //})
  //})
  res.json(formattedResults)
  } catch(error){
      console.error('Error:', error);
      database_error(res, error.stack);
  }
})

//request for being a dietician
//done
app.get("/add/dietReq", async (req,res)=>{

  try{
  let UID=req.query.p1
  const query = 'INSERT INTO DIETICIAN_REQUEST (UID) VALUES (?)'
  const [result1] = await con.promise().query(query, [UID])
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    
    
    
    /*con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }*/
      console.log('Request for being dietician made!!' + con.threadId)
      query_success(res, 'Request for being dietician made!!')
    //})
  //})
} catch(error){
    console.error('Error:', error);
    database_error(res, error.stack);
  }
})

//get all request for being a dietician
//done
app.get("/get/dietReq", async (req,res)=>{
  /*
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    const query = 'SELECT d.RID, u.UID, u.FirstName, u.LastName, u.Email, u.ProfileURL FROM DIETICIAN_REQUEST d, USERS u WHERE u.UID=d.UID'
    con.query(query, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      const formattedResults = results.map((result) => {
        return `${result.RID}\t${result.UID}\t${result.FirstName}\t${result.LastName}\t${result.Email}\t${result.ProfileURL}`;
      });
      console.log('SUCCESS show list of being dietician request') 
      query_success(res, 'SUCCESS show list of being dietician request: ' + formattedResults.join('\n'))
    })
  })*/
  try {
    const query = 'SELECT d.RID, u.UID, u.FirstName, u.LastName, u.Email, u.ProfileURL FROM DIETICIAN_REQUEST d, USERS u WHERE u.UID=d.UID'
    const [results] = await con.promise().query(query)

    const formattedResults = results.map((result) => {
      return `${result.RID}\t${result.UID}\t${result.FirstName}\t${result.LastName}\t${result.Email}\t${result.ProfileURL}`;
    });

    console.log('SUCCESS show list of being a dietician request');
    query_success(res, 'SUCCESS show list of being a dietician request: ' + formattedResults.join('\n'));
  } catch (error) {
    console.error('Error:', error);
    database_error(res, error.stack);
  }
})


//approve request for being a dietician, add to dietician table and remove the request
//done
app.post("/approve/dietReq", async (req,res)=>{
  try{
  UID=req.body.p1// ? req.query.p1.split(',') : []
  console.log(UID)
  const query = 'INSERT INTO DIETICIAN (FirstName, LastName, Email, ProfileURL) SELECT u.FirstName, u.LastName, u.Email, u.ProfileURL FROM USERS u WHERE u.UID IN (?)'
  const query2='DELETE FROM DIETICIAN_REQUEST WHERE UID IN (?)'
  //const query3='DELETE FROM USERS WHERE UID IN (?)'
  const [result1] = await con.promise().query(query, [UID])
  const [result2] = await con.promise().query(query2, [UID])
  //const [result3] = await con.promise().query(query3, [UID])
  
  //if(result2.length>0){
    console.log('SUCCESS approve being dietician request') 
    query_success(res, 'SUCCESS approve being dietician request')
  //}
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    /*con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
    })*/
    /*con.query(query2, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      
  //})
  //})
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})


app.get("/get/users_type", async (req,res)=>{

  try {
    const Email = req.query.p1;

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

//todo test this
app.post("/get/recipe", async (req,res)=>{
  try {
    //let UID=req.query.p1
    let expiryitems=req.body.p1 //? req.query.p2.split(',') : []
    let Pref=req.body.p2 //? req.query.p3.split(',') : []
    /*let expiryitems=['Tomato', 'Salt']
    let Pref=['Vegetarian','Non-dairy', 'Vegan' ]*/
    let storequery=''
    let query=''
    const store=[]
    if(Pref.length != 0){
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
        query+="INTERSECT SELECT * FROM " +excludeTable + ') AS store WHERE store.Ingredient LIKE ? '
        store.push(excludeTable)
      }
    }
    console.log(query)
  }
  else{
    query = 'SELECT store.RID FROM recipe r WHERE r.Ingredient LIKE ? '
  }
  storequery=query
  for(let j=0; j< expiryitems.length; j++){
    if(j==0){
      temp=query.replace('?', '\''+`%${expiryitems[j]}%`+'\'')
      query=temp
    }
    else if(j< expiryitems.length-1){
      query += "INTERSECT " + storequery
      temp=query.replace('?', '\''+`%${expiryitems[j]}%`+'\'')
      query=temp
    }
    else{
      query += "INTERSECT " + storequery
      temp=query.replace('?', '\''+`%${expiryitems[j]}%`+'\'')
      query=temp
      tempquery= "SELECT s.RID FROM (" + query + ") AS s ORDER BY RAND() LIMIT 5"
      query=tempquery
    }
  }
    const [results] = await con.promise().query(query)
    //console.log(results.sql);
    //todo fix the formatting and get the complete recipe
    /*const responseObject = {
      recipes: results,
    };*/
    /*
    const formattedResults = results.map((result) => {
      return `${result.RID}\t`;
    });
    console.print(formattedResults)*/
    const formattedArray = results.map((result) => result.RID);
    console.log(formattedArray);
    const query2 = 'SELECT * FROM Recipe WHERE RID IN (?)'
    const [result2] = await con.promise().query(query2, [formattedArray])

    const formattedResults = result2.map((result) => {
      return {
        RID: result.RID,
        Ingredient: result.Ingredient,
        Amount: result.Amount,
      };
    });
    
    console.log(formattedResults)
    //query_success(res, formattedResults)
    
    const structuredData = {};

    for (const result of formattedResults) {
      if (!structuredData[result.RID]) {
        structuredData[result.RID] = {
          RID: result.RID,
          ingredients: [],
        };
      }
    
      structuredData[result.RID].ingredients.push({
        Ingredient: result.Ingredient,
        Amount: result.Amount,
      });
    }
    
    const jsonData = Object.values(structuredData);
    
    console.log(jsonData);
    res.json(jsonData);
    
    //query_success(res, formattedArray)
    //res.json(responseObject);
    //const query = 'SELECT d.RID, u.UID, u.FirstName, u.LastName, u.Email, u.ProfileURL FROM DIETICIAN_REQUEST d, USERS u WHERE u.UID=d.UID'
    /*const [results] = await con.promise().query(query)

    const formattedResults = results.map((result) => {
      return `${result.RID}\t${result.UID}\t${result.FirstName}\t${result.LastName}\t${result.Email}\t${result.ProfileURL}`;
    });*/

    //console.log('SUCCESS show list of being a dietician request');
    //query_success(res, 'SUCCESS show list of being a dietician request: ' + formattedResults.join('\n'));

  } catch (error) {
    console.error('Error:', error);
    database_error(res, error.stack);
  }
})

//TODO add endpoints to get recipe info using rid
app.get("/get/recipe_info", async (req,res)=>{
  try{
    
  let RID=req.query.p1 ? req.query.p1.split(',') : []
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)*/
    const query = 'SELECT * FROM RECIPE_INFO WHERE RID = IN (?)'
    const [results] = await con.promise().query(query, [RID])
    /*con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      if (results.length === 0) {
        return res.json({});
      }

      const formattedResults = results.map((result) => {
        return {
          Name: result.Rname, 
          Instruction: result.Instruction,
          YTLink: result.YoutubeLInk
        }
      })
      console.log('SUCCESS return recipe_info') 
      res.json(formattedResults)
      //query_success(res, 'SUCCESS select Pref ' + formattedResults.join('\n'))
    //})
  //})
  } catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

const moment = require('moment');

// Define a function to process shopping data and generate reminders
function processShoppingData() {
  // User-defined settings
  const reminderPeriodDays = 2;
  const numberOfVisits = 10; // This can be changed

  // Store item purchase history
  const purchaseHistory = {};

  // Initialize current date
  const currentDate = moment();

  // Initialize the expected run-out date for each item
  const expectedRunOutDates = {};


  // Connect to the MySQL database
  con.connect((err) => {
    if (err) {
      console.error('MySQL Connection Error:', err);
      return;
    }
    
    // NEED TO UPDATE
    // Query to retrieve shopping data from the database
    const query = 'SELECT item, quantity, purchase_date FROM shopping_data_table';

    // Execute the query
    con.query(query, (queryErr, shoppingData) => {
      if (queryErr) {
        console.error('MySQL Query Error:', queryErr);
        con.end(); // Close the connection
        return;
      }

      // Close the MySQL connection
      con.end();

      
      // Populate purchase history, also detecting anomalies
      shoppingData.forEach((shoppingEntry) => {
        const item = shoppingEntry.item;
        const quantity = shoppingEntry.quantity;
        const anomalyThreshold = quantity * 3; // Anomaly detection threshold (e.g., a quantity higher than this threshold is considered an anomaly)
        const purchaseDate = moment(shoppingEntry.purchase_date, 'YYYY-MM-DD').toDate();

        // Check for anomalies based on the threshold
        if (quantity > anomalyThreshold) {
          console.log(`Anomaly detected for ${item} on ${purchaseDate}: Quantity ${quantity} exceeds threshold.`);
          return;
        }

        // Updating purchase history
        if (item in purchaseHistory) {
          purchaseHistory[item].push(purchaseDate);
        } else {
          purchaseHistory[item] = [purchaseDate];
        }

        // Calculate expected run-out date based on time frame
        if (!(item in expectedRunOutDates)) {
          expectedRunOutDates[item] = moment(purchaseDate).add(numberOfVisits, 'weeks').toDate();
        }
      });

      // Generate reminders
      for (const item in purchaseHistory) {
        const purchaseDates = purchaseHistory[item];

        // Calculate the average purchase frequency
        const purchaseFrequency = purchaseDates.length / numberOfVisits; // Number of times purchased per week

        // Calculate the expected run-out date
        const expectedRunOutDate = expectedRunOutDates[item];

        // Calculate the reminder date
        const reminderDate = moment(expectedRunOutDate).subtract(reminderPeriodDays, 'days');

        // TO BE IMPLEMENTED IN FIREBASE
        // Check if it's time to send a reminder 
        if (currentDate.isSameOrAfter(reminderDate)) {
          console.log(`Reminder: Buy ${item} in the next ${reminderPeriodDays} days.`);
        }
      }
    });
  });
}

// Call the function to process shopping data and generate reminders
//processShoppingData();