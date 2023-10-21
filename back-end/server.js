//TODO add asyc to all requests and maybe add locks to each table
var mysql = require('mysql2');
var express = require("express")
var app=express()

var con = mysql.createConnection({
  host: "127.0.0.1",
  port: "3306",
  user: "root",
  password: "Zachary",
  database: 'grocerymanger'
});

var server=app.listen(8081,"0.0.0.0", (req,res)=>{
  var host=server.address().address
  var port=server.address().port

  console.log("%s %s", host, port)

})

function database_error(response, error) {
  response.status(500).send('Error querying the database'+error)
}

function query_success(response, message){
  response.status(200).send(message)
}

//get users
//done
app.get("/get/users", (req,res)=>{
  let UID=req.query.p1

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'SELECT FirstName, LastName, Email, ProfileURL FROM USERS WHERE UID=?'
    con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      const formattedResults = results.map((result) => {
        return `${result.FirstName}\t${result.LastName}\t${result.Email}\t${result.ProfileURL}`;
      });
      query_success(res, 'SUCCESS Get User:\n' + formattedResults.join('\n'))
    })
  })
})

//done
app.get("/add/users", (req,res)=>{
  let FirstName=req.query.p1
  let LastName=req.query.p2
  let Email=req.query.p3
  let ProfileURL=req.query.p4
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    
    const query = 'INSERT INTO USERS (FirstName, LastName, Email, ProfileURL) VALUES (?, ?, ?, ?)'
    con.query(query, [FirstName, LastName, Email, ProfileURL], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
    })

    const query2='SELECT UID FROM USERS WHERE Email=?'
    con.query(query2, [Email], (err, results2, fields2) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
      console.log('USER ADDED') 
      const formattedResults = results2.map((r) => {
        return `${r.UID}`;
      });
      query_success(res, 'Useradded ' + formattedResults.join('\n'))
    })
  })
})

//delete users
//done
app.get("/delete/users", (req,res)=>{
  let UID=req.query.p1

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      database_error(res, err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'DELETE FROM USERS WHERE UID=?'
    con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      if (results.affectedRows === 0) {
        console.log('No rows were deleted. Check the values in your DELETE query.')
        res.send('No rows were deleted. Check the values in your DELETE query.')
      } else {
        console.log('SUCCESS DELETED USER')
        query_success(res, 'DELETED USER')
      }
    })
  })
})

//update user
app.get("/update/users", (req,res)=>{
  let UID=req.query.p1
  let FirstName=req.query.p2
  let LastName=req.query.p3
  let Email=req.query.p4
  let ProfileURL=req.query.p5
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    
    const query = 'UPDATE USERS SET FirstName=?, LastName=?, Email=?, ProfileURL=? WHERE UID=?'
    con.query(query, [FirstName, LastName, Email, ProfileURL, UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
      console.log('SUCCESS Update User') 
      query_success(res, 'Success update user')
    })
  })
})

//get items
//done
app.get("/get/items", (req,res)=>{
  let ID=req.query.ID
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'UPDATE OWNS o JOIN (SELECT o1.UPC,o1.UID,o1.ExpireDate,o1.ItemCount,ROW_NUMBER() OVER (PARTITION BY o1.UID ORDER BY o1.ExpireDate, o1.UPC ASC) AS NewItemID FROM OWNS o1 WHERE o1.UID =?) AS result ON o.UPC = result.UPC AND o.UID = result.UID AND o.ExpireDate=result.ExpireDate And o.ItemCount=result.ItemCount SET o.ItemID = result.NewItemID WHERE o.UID=?;'
    con.query(query, [ID,ID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
    })
    
    const query2 = 'SELECT * FROM OWNS o WHERE o.UID=? ORDER BY o.ItemID ASC'
    con.query(query2, ID, (err, selectResults, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }

      console.log('SUCCESS Get items') 
      
      const formattedResults = selectResults.map((result) => {
        const formattedDate = new Date(result.ExpireDate).toLocaleDateString();
        return `${result.UPC}\t${result.UID}\t${formattedDate}\t${result.ItemCount}\t${result.ItemID}`;
      });
      query_success(res, 'SUCCESS Get items:\n' + formattedResults.join('\n'))
    })
  })
})

//add items
//done
app.get("/add/items", (req,res)=>{

  let UID=req.query.p1
  let UPC = req.query.p2 ? req.query.p2.split(',') : []
  let ExpireDate = req.query.p3 ? req.query.p3.split(',') : []
  let ItemCount = req.query.p4 ? req.query.p4.split(',') : []
  if (UPC.length !== ExpireDate.length || UPC.length !== ItemCount.length) {
    return res.status(400).send('Arrays should have the same length')
  }
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
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
    con.query(query, [values], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS ADDED') 
      query_success(res, 'SUCCESS ADDED')
    })
  })
})

//delete items
//done
app.get("/delete/items", (req,res)=>{
  let UID=req.query.p1
  let ItemID=req.query.p2 ? req.query.p2.split(',') : []

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'DELETE FROM OWNS WHERE UID= ? AND ItemID IN (?)'
    con.query(query, [UID, ItemID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      if (results.affectedRows === 0) {
        console.log('No rows were deleted. Check the values in your DELETE query.')
        res.send('No rows were deleted. Check the values in your DELETE query.')
      } else {
        console.log('SUCCESS DELETED')
        query_success(res, 'DELETED ITEM')
      }
    })
  })
})

//done
//update items
app.get("/update/items", (req,res)=>{

  let UID=req.query.p1
  let ItemID=req.query.p2 ? req.query.p2.split(',') : []
  let UPC = req.query.p3 ? req.query.p3.split(',') : []
  let ExpireDate = req.query.p4 ? req.query.p4.split(',') : []
  let ItemCount = req.query.p5 ? req.query.p5.split(',') : []
  if (UPC.length !== ExpireDate.length || UPC.length !== ItemCount.length || ItemID.length !== UPC.length) {
    return res.status(400).send('Arrays should have the same length')
  }
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    const values = [];
    for (let i = 0; i <UPC.length; i++) {
      values.push([ExpireDate[i], ItemCount[i], UID, UPC[i], ItemID[i]])
    }
    console.log(values)
    const query = 'UPDATE OWNS SET ExpireDate=?, ItemCount=? WHERE UID=? AND UPC=? AND ItemID=?;'
    values.forEach((values) => {
      const executedQuery = mysql.format(query, values);
      console.log('Executed Query:', executedQuery);
      con.query(executedQuery, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack);
      return
      }
      console.log('SUCCESS Updated items');
      })
    })
    query_success(res, 'SUCCESS Updated items')
  })
})

//add pref
//done
app.get("/add/pref", (req,res)=>{

  let UID=req.query.p1
  let Pref = req.query.p2 ? req.query.p2.split(',') : []
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
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
    con.query(query, [values], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS ADDED Pref') 
      query_success(res, 'SUCCESS ADDED Pref')
    })
  })
})

//delete pref
//done
app.get("/delete/pref", (req,res)=>{

  let UID=req.query.p1
  let Pref = req.query.p2 ? req.query.p2.split(',') : []
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    const query = 'DELETE FROM PREFERENCE WHERE UID= ? AND Pref IN (?)'
    con.query(query, [UID, Pref], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS DELETE Pref') 
      query_success(res, 'SUCCESS DELETE Pref')
    })
  })
})

//get preference
//done
app.get("/get/pref", (req,res)=>{

  let UID=req.query.p1
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    const query = 'SELECT Pref FROM PREFERENCE WHERE UID= ?'
    con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS select Pref') 
      const formattedResults = results.map((result) => {
        return `${result.Pref}`;
      });
      query_success(res, 'SUCCESS select Pref ' + formattedResults.join('\n'))
    })
  })
})

//get available preference from database
//done
app.get("/get/pref_list", (req,res)=>{

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    const query = 'SELECT * FROM PREF_LIST'
    con.query(query, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS show Pref_list') 
      const formattedResults = results.map((result) => {
        return `${result.Pref}`;
      });
      query_success(res, 'SUCCESS show Pref_list ' + formattedResults.join('\n'))
    })
  })
})

//request for being a dietician
//done
app.get("/add/dietReq", (req,res)=>{

  let UID=req.query.p1
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    
    const query = 'INSERT INTO DIETICIAN_REQUEST (UID) VALUES (?)'
    con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }
      console.log('Request for being dietician made!!' + con.threadId)
      query_success(res, 'Request for being dietician made!!')
    })
  })
})

//get all request for being a dietician
//done
app.get("/get/dietReq", (req,res)=>{
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
  })
})


//approve request for being a dietician, add to dietician table and remove the request
//done
app.get("/approve/dietReq", (req,res)=>{

  UID=req.query.p1 ? req.query.p1.split(',') : []
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    const query = 'INSERT INTO DIETICIAN (FirstName, LastName, Email, ProfileURL) SELECT u.FirstName, u.LastName, u.Email, u.ProfileURL FROM USERS u WHERE u.UID IN (?)'
    con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
    })
    const query2='DELETE FROM DIETICIAN_REQUEST WHERE UID IN (?)'
    con.query(query2, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS approve being dietician request') 
      query_success(res, 'SUCCESS approve being dietician request')
  })
  })
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
    database_error(res, err.stack)
  }

})
