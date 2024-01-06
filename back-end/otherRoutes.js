const express = require('express')
const router = express.Router()
const { UIDcheck, ItemIDcheck} = require('./check')

router.get("/delete/UPC", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UPC=req.query.p1
        
        if(UPC==="ForceError"){
            throw new Error("Forced Error")
        }
    
        const query = 'DELETE FROM OWNS WHERE UPC= ?'
        const query2 = 'DELETE FROM GROCERIES WHERE UPC= ?'
        await con.promise().query(query, [UPC])
        await con.promise().query(query2, [UPC])
        
        console.log('SUCCESS DELETE Groceries') 
        query_success(res, 'SUCCESS DELETE Groceries')
      
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})