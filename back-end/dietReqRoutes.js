const express = require('express')
const router = express.Router()
const { UIDcheck} = require('./check')

function database_error(response, error) {
    response.status(500).send('Error querying the database'+error)
}
  
function query_success(response, message){
    response.status(200)
    response.json({Message: message})
  
}

//request for being a dietician
//done
router.get("/add", async (req,res)=>{

    try{
        const con = req.app.get('dbConnection')
        const UID=req.query.p1
    
        if(UID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        try {
            await UIDcheck(UID)
        } catch (error) {
            console.error('Error:', error)
            return database_error(res, error.message)
        }
    
        const query = 'INSERT INTO DIETICIAN_REQUEST (UID) VALUES (?)'
        await con.promise().query(query, [UID])
    
        console.log('Request for being dietician made!!' + con.threadId)
        query_success(res, 'Request for being dietician made!!')
      
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
//get all request for being a dietician
//done
router.get("/get", async (req,res)=>{
    try {
        const con = req.app.get('dbConnection')
        if(req.query.p1==="ForceError"){
            throw new Error("Forced Error")
        }
    
        const query = 'SELECT d.RID, u.UID, u.FirstName, u.LastName, u.Email, u.ProfileURL FROM DIETICIAN_REQUEST d, USERS u WHERE u.UID=d.UID'
        const [results] = await con.promise().query(query)
        
        if(results.length==0){
            return res.json({})
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
        })
    
        console.log('SUCCESS show list of being a dietician request')
        res.json(formattedResults)
      
    } catch (error) {
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
  
//approve request for being a dietician, add to dietician table and remove the request
//done
router.get("/approve", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.query.p1
        console.log(UID)
    
        if(UID==="ForceError"){
            throw new Error("Forced Error");
        }
        
        const query = 'INSERT INTO DIETICIAN (FirstName, LastName, Email, ProfileURL, MessageToken) SELECT u.FirstName, u.LastName, u.Email, u.ProfileURL, u.MessageToken FROM USERS u WHERE u.UID=?'
        const query2='DELETE FROM DIETICIAN_REQUEST WHERE UID=?'
        const query3='DELETE FROM USERS WHERE UID IN (?)'
    
        await con.promise().query(query, [UID])
        await con.promise().query(query2, [UID])
        await con.promise().query(query3, [UID])
    
        console.log('SUCCESS approve being dietician request') 
        query_success(res, 'SUCCESS approve being dietician request')
    
    }catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
//remove request for being a dietician 
//done
router.get("/remove", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.query.p1
        console.log(UID)
    
        if(UID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        const query2='DELETE FROM DIETICIAN_REQUEST WHERE UID=?'
        await con.promise().query(query2, [UID])
    
        console.log('SUCCESS delete being dietician request') 
        query_success(res, 'SUCCESS delete being dietician request')
    
    }catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

module.exports = router