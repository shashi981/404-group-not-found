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
    
    
    const query = 'INSERT INTO USERS (FirstName, LastName, Email, ProfileURL) VALUES (?, ?, ?, ?)';
    con.query(query, [FirstName, LastName, Email, ProfileURL], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        database_error(res, err.stack)
        return
      }

      //TODO return userID
      console.log('USER ADDED') 

      query_success(res, 'USER ADDED')
      //console.log(fields); 
      //con.end() 
    })
  })
})

app.get("/get/items", (req,res)=>{
  let ID=req.query.ID
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'UPDATE OWNS o JOIN (SELECT o1.UPC,o1.UID,o1.ExpireDate,o1.ItemCount,ROW_NUMBER() OVER (PARTITION BY o1.UID ORDER BY o1.ExpireDate ASC) AS NewItemID FROM OWNS o1 WHERE o1.UID =?) AS result ON o.UPC = result.UPC AND o.UID = result.UID SET o.ItemID = result.NewItemID WHERE o.UID=?;'
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

app.get("/add/items", (req,res)=>{

  let UPC=req.query.p1
  let UID=req.query.p2
  let ExpireDate=req.query.p3
  let ItemCount=req.query.p4
  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'INSERT INTO OWNS (UPC, UID, ExpireDate, ItemCount) VALUES (?, ?, ?, ?)';
    con.query(query, [UPC, UID, ExpireDate, ItemCount], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS ADDED') 
      query_success(res, 'SUCCESS ADDED')
    })
  })
})

//change this to be delete using itemID
app.get("/delete/items", (req,res)=>{
  let UID=req.query.p1
  let ItemID=req.query.p2

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'DELETE FROM OWNS WHERE UPC=? AND ItemID=?'
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
        query_success(res, 'DELETED')
      }
    })
  })
})

app.get("/get/items", (req,res)=>{
  let UID=req.query.p1

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'SELECT FROM OWNS WHERE UPC=?'
    con.query(query, [UID], (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      if (results.affectedRows === 0) {
        console.log('No rows were deleted. Check the values in your DELETE query.')
        res.send('No rows were deleted. Check the values in your DELETE query.')
      } else {
        //TODO restructure the result
        console.log('SUCCESS get inventory')
        query_success(res, 'SUCCESS Get inventory')
      }
    })
  })
})
