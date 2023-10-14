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

app.get("/add/user", (req,res)=>{

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const userData = {
      FirstName: 'Test4',
      LastName: 'test4',
      Email: 'testing4@gmail.com',
    };
    
    const query = 'INSERT INTO USERS (FirstName, LastName, Email) VALUES (?, ?, ?)';
    const values = [userData.FirstName, userData.LastName, userData.Email];
    con.query(query, values, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('USER ADDED') 

      res.send('USER ADDED')
      //console.log(fields); 
      //con.end() 
    })
  })
})

app.get("/add/items", (req,res)=>{

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'INSERT INTO OWNS (UPC, UID, ExpireDate, ItemCount) VALUES (?, ?, ?, ?)';
    const values = [123456789012, 1, '2023-10-26', 8];
    con.query(query, values, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      console.log('SUCCESS ADDED') 

      res.send('SUCCESS ADDED')
      //console.log(fields); 
      //con.end() 
    })
  })
})

app.get("/delete/items", (req,res)=>{

  con.connect(function(err) {
    if (err) {
      console.error('Error connecting to the database: ' + err.stack)
      return;
    }
  
    console.log('Connected to the database as id ' + con.threadId)
    
    const query = 'DELETE FROM OWNS WHERE UPC=? AND UID=? AND ExpireDate=? AND ItemCount=?';
    const values = [123456789012, 1, '2023-10-26', 8];
    con.query(query, values, (err, results, fields) => {
      if (err) {
        console.error('Error querying the database: ' + err.stack)
        return
      }
      if (results.affectedRows === 0) {
        console.log('No rows were deleted. Check the values in your DELETE query.');
        res.send('No rows were deleted. Check the values in your DELETE query.');
      } else {
        console.log('SUCCESS DELETED');
        res.send('SUCCESS DELETED');
      }
      //console.log(fields); 
      //con.end() 
    })
  })
})
