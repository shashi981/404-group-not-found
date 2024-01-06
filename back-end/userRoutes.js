const express = require('express')
const router = express.Router()
const { Emailcheck, UIDcheck} = require('./check')

function database_error(response, error) {
    response.status(500).send('Error querying the database'+error)
}
  
function query_success(response, message){
    response.status(200)
    response.json({Message: message})
}

//get users
//done
router.post("/get", async (req, res) => {
    try{
        const con = req.app.get('dbConnection')
        const email = req.body.p1
        const token = req.body.p2
  
        if(email==="ForceError" && token==="ForceError"){
            throw new Error("Forced Error");
        }
  
        try {
            await Emailcheck(con, email)
        } catch (error) {
            console.error('Error:', error);
            return database_error(res, error.message);
        }
  
        const updateQuery = 'UPDATE USERS SET MessageToken = ? WHERE Email = ?;';
        await con.promise().query(updateQuery, [token, email])
    
        const selectQuery = 'SELECT * FROM USERS WHERE Email = ?;'
        const [results] = await con.promise().query(selectQuery, [email])
  
        const user = results[0]
        const responseObject = {
            UID: user.UID,
            FirstName: user.FirstName,
            LastName: user.LastName,
            Email: user.Email,
            ProfileURL: user.ProfileURL,
        }
  
        console.log("USER GET")
        res.json(responseObject)
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
  
//add users
//done
router.post("/add", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const FirstName=req.body.p1
        const LastName=req.body.p2
        const Email=req.body.p3
        const ProfileURL=req.body.p4
        const Token=req.body.p5
    
        const query = 'INSERT INTO USERS (FirstName, LastName, Email, ProfileURL, MessageToken) VALUES (?, ?, ?, ?, ?);'
        const query2='SELECT UID FROM USERS WHERE Email=?'
  
        await con.promise().query(query, [FirstName, LastName, Email, ProfileURL, Token])
        const [results2] = await con.promise().query(query2, [Email])
    
        console.log("USER ADDED") 
    
        const formattedResults = results2.map((r) => {
            return `${r.UID}`
        });
        query_success(res, formattedResults)
        
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
  })
  
//delete users
//done
router.get("/delete", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.query.p1
    
        if(UID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        try {
            await UIDcheck(con, UID);
        } catch (error) {
            console.error('Error:', error)
            return database_error(res, error.message)
        }
  
        const query = 'DELETE FROM USERS WHERE UID=?;'
        await con.promise().query(query, [UID])
        
        console.log('SUCCESS DELETED USER')
        query_success(res, 'DELETED USER')
        
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

module.exports = router