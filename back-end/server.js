//figure out if can do array payload
var mysql = require('mysql2');
var express = require("express")
var app=express()

var con = mysql.createConnection({
  host: "",
  port: "3306",
  user: "root",
  password: "",
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
      return;
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
      return;
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
      return;
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

//modify user
app.get("/modify/users", (req,res)=>{
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
        res.status(500).send('Error querying the database: ');
        return
      }
    })
    
    const query2 = 'SELECT * FROM OWNS o WHERE o.UID=? ORDER BY o.ItemID ASC'
    con.query(query2, ID, (err, selectResults, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        res.status(500).send('Error querying the database: ');
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
      return;
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
      return;
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


