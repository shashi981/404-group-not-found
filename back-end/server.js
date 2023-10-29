//TODO add asyc to all requests and maybe add locks to each table
//TODO change or redirect the server to use https once the cert is done for server
const mysql = require('mysql2');
const express = require("express")
const https = require('https')
const fs = require('fs')

const app=express()

/*
//local testing use
const con = mysql.createConnection({
  host: "",
  port: "3306",
  user: "root",
  password: "",
  database: 'grocerymanger'
});*/

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

const server = https.createServer(certs, app);

server.listen(443, () => {
  console.log(`Server is running on port 443`);
})

/*
const server=app.listen(8081,"0.0.0.0", (req,res)=>{
  const host=server.address().address
  const port=server.address().port

  console.log("%s %s", host, port)

})*/


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
  let UID=req.query.p1
  const query = 'SELECT FirstName, LastName, Email, ProfileURL FROM USERS WHERE UID=?;'

  const [results] = await con.promise().query(query, [UID])
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
      const formattedResults = results.map((result) => {
        return `${result.FirstName}\t${result.LastName}\t${result.Email}\t${result.ProfileURL}`;
      })
      query_success(res, 'SUCCESS Get User:\n' + formattedResults.join('\n'))
   // })
  //})
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//done
app.get("/add/users", async (req,res)=>{
  try{

  let FirstName=req.query.p1
  let LastName=req.query.p2
  let Email=req.query.p3
  let ProfileURL=req.query.p4

  const query = 'INSERT INTO USERS (FirstName, LastName, Email, ProfileURL) VALUES (?, ?, ?, ?);'
  const query2='SELECT UID FROM USERS WHERE Email=?'

  const [results1] = await con.promise().query(query, [FirstName, LastName, Email, ProfileURL])
  const [results2] = await con.promise().query(query2, [FirstName, LastName, Email, ProfileURL])

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
      query_success(res, 'Useradded ' + formattedResults.join('\n'))
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
    

    const [results] = await con.promise().query(query, [FirstName, LastName, Email, ProfileURL])
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

    let ID=req.query.ID

    const query = 'UPDATE OWNS o JOIN (SELECT o1.UPC,o1.UID,o1.ExpireDate,o1.ItemCount,ROW_NUMBER() OVER (PARTITION BY o1.UID ORDER BY o1.ExpireDate, o1.UPC ASC) AS NewItemID FROM OWNS o1 WHERE o1.UID =?) AS result ON o.UPC = result.UPC AND o.UID = result.UID AND o.ExpireDate=result.ExpireDate And o.ItemCount=result.ItemCount SET o.ItemID = result.NewItemID WHERE o.UID=?;'

    const query2 = 'SELECT * FROM OWNS o WHERE o.UID=? ORDER BY o.ItemID ASC;'

    const [result1] = await con.promise().query(query, [ID])
    const [result2] = await con.promise().query(query2, [ID])
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

      console.log('SUCCESS Get items') */
      
      const formattedResults = result2.map((result) => {
        const formattedDate = new Date(result.ExpireDate).toLocaleDateString();
        return `${result.UPC}\t${result.UID}\t${formattedDate}\t${result.ItemCount}\t${result.ItemID}`;
      });
      query_success(res, 'SUCCESS Get items:\n' + formattedResults.join('\n'))
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
      if(i<UPC.length-1){
        values.push(([UID, UPC[i], ExpireDate[i], ItemCount[i],]));
      }
      else{
        values.push(([UID, UPC[i], ExpireDate[i], ItemCount[i]]));
      }
    }
    console.log(values)

    const query = 'INSERT INTO OWNS (UID, UPC, ExpireDate, ItemCount) VALUES ?'

    const [results] = await con.promise().query(query, [values])
    /*con.query(query, [values], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }*/
      console.log('SUCCESS ADDED') 
      query_success(res, 'SUCCESS ADDED')
    //})
 // })
  }catch(error){
    console.error('Error:', error)
    database_error(res, error.stack)
  }
})

//delete items
//done
app.get("/delete/items", async (req,res)=>{
  try{
  let UID=req.query.p1
  let ItemID=req.query.p2 ? req.query.p2.split(',') : []

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
        console.log('SUCCESS DELETED')
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
app.get("/update/items", (req,res)=>{
  try{
  let UID=req.query.p1
  let ItemID=req.query.p2 ? req.query.p2.split(',') : []
  let UPC = req.query.p3 ? req.query.p3.split(',') : []
  let ExpireDate = req.query.p4 ? req.query.p4.split(',') : []
  let ItemCount = req.query.p5 ? req.query.p5.split(',') : []

  if (UPC.length !== ExpireDate.length || UPC.length !== ItemCount.length || ItemID.length !== UPC.length) {
    return res.status(400).send('Arrays should have the same length')
  }
  /*con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    const values = [];*/
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
app.get("/add/pref", async (req,res)=>{

  try{
  let UID=req.query.p1
  let Pref = req.query.p2 ? req.query.p2.split(',') : []
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
app.get("/delete/pref", async (req,res)=>{
  try{
  let UID=req.query.p1
  let Pref = req.query.p2 ? req.query.p2.split(',') : []

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
      console.log('SUCCESS select Pref') 
      const formattedResults = results.map((result) => {
        return `${result.Pref}`;
      });
      query_success(res, 'SUCCESS select Pref ' + formattedResults.join('\n'))
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
        return `${result.Pref}`;
      });
      query_success(res, 'SUCCESS show Pref_list ' + formattedResults.join('\n'))
    //})
  //})
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
app.get("/approve/dietReq", async (req,res)=>{
  try{
  UID=req.query.p1 ? req.query.p1.split(',') : []
  console.log(UID +"FUCK YOU")
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
      return query_success(res, 'This is a user\n')
    }

    const query2 = 'SELECT * FROM DIETICIAN WHERE Email=?'
    const [dieticianResults] = await con.promise().query(query2, [Email]);

    if (dieticianResults.length > 0) {
      console.log('Entry exists as dietician')
      return query_success(res, 'This is a dietician\n')
    }

    const query3 = 'SELECT * FROM ADMIN WHERE Email=?'
    const [adminResults] = await con.promise().query(query3, [Email])

    if (adminResults.length > 0) {
      console.log('Entry exists as admin');
      return query_success(res, 'This is an admin\n')
    }

    console.log('This is an entry that does not exist')
    return query_success(res, 'This is an entry that does not exist\n')
  } catch (error) {
    console.error('Error:', error)
    database_error(res, error.stack)
  }

})
