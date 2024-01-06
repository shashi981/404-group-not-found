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

//add pref
//done
router.post("/add", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.body.p1
        const Pref = req.body.p2 
        const values = [];
    
        if(UID==="ForceError"&& Pref==="ForceError"){
            throw new Error("Forced Error")
        }
    
        if(Pref.length === 0){
            throw new Error('Empty array is passed')
        }
        for (let i = 0; i <Pref.length; i++) {
            if(i<Pref.length-1){
            values.push(([UID, Pref[i],]))
            }
            else{
            values.push(([UID, Pref[i]]))
            }
        }
        console.log(values)
        const query = 'INSERT INTO PREFERENCE (UID, Pref) VALUES ?'
        await con.promise().query(query, [values])
    
        console.log('SUCCESS ADDED Pref') 
        query_success(res, 'SUCCESS ADDED Pref')
    
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    } 
})
  
//delete pref
//done
router.get("/delete", async (req,res)=>{
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
    
        const query = 'DELETE FROM PREFERENCE WHERE UID= ?'
        await con.promise().query(query, [UID])
      
        console.log('SUCCESS DELETE Pref') 
        query_success(res, 'SUCCESS DELETE Pref')
      
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
//get preference
//done
router.get("/get", async (req,res)=>{
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
    
        const query = 'SELECT Pref FROM PREFERENCE WHERE UID= ?'
        const [results] = await con.promise().query(query, [UID])
    
        if (results.length === 0) {
            return res.json({})
        }
  
        const formattedResults = results.map((result) => {
          return {
            Pref: result.Pref
          }
        })
        console.log('SUCCESS select Pref') 
        res.json(formattedResults)
        
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
//get available preference from database
//done
router.get("/list", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        if(req.query.p1==="ForceError"){
            throw new Error("Forced Error")
        }
    
        const query = 'SELECT * FROM PREF_LIST'
        const [results] = await con.promise().query(query)
    
        console.log('SUCCESS show Pref_list') 
        const formattedResults = results.map((result) => {
            return {Pref:result.Pref}
        });
        res.json(formattedResults)
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

module.exports = router