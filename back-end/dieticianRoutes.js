const express = require('express')
const router = express.Router()


function database_error(response, error) {
    response.status(500).send('Error querying the database'+error)
}
  
function query_success(response, message){
    response.status(200)
    response.json({Message: message})
}

//get dieitician with the email and also update the firebase token as well
//done
app.post("/get", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const Email=req.body.p1
        const Token=req.body.p2
        
        if(Email==="ForceError" && Token==="ForceError"){
        throw new Error("Forced Error")
        }
    
        const query = 'SELECT * FROM DIETICIAN WHERE Email=?;'
        const updatequery = 'UPDATE DIETICIAN SET MessageToken= ? WHERE Email=? ;'
    
        await con.promise().query(updatequery, [Token, Email]) 
        const [results] = await con.promise().query(query, [Email])
    
        const dietician = results[0]
    
        const responseObject = {
            DID: dietician.DID,
            FirstName: dietician.FirstName,
            LastName: dietician.LastName,
            Email: dietician.Email,
            ProfileURL: dietician.ProfileURL,
        }
        console.log("DIETICIAN GET")
    
        res.json(responseObject)
    }catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
  })
  
app.get("/delete", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const DID=req.query.p1
    
        if(DID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        const query = 'DELETE FROM DIETICIAN WHERE DID=?;'
        await con.promise().query(query, [DID])
        
        console.log('SUCCESS DELETED Dietician')
        query_success(res, 'DELETED Dietician')
  
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

module.exports = router