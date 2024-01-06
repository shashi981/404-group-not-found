const express = require('express')
const router = express.Router()

function database_error(response, error) {
    response.status(500).send('Error querying the database'+error)
}
  
// todo use the api to get extra recipes when the no recipe matches on db
//get recipe for items about to expiry based on preference and use uid
//done
router.get("/get/recipe", async (req,res)=>{
    try {
        const con = req.app.get('dbConnection')
        const UID=req.query.p1
        
        if(UID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        let storequery=''
        let query=''
        const store=[]
    
        const Prefquery = 'SELECT * FROM PREFERENCE WHERE UID=?'
        const [Preftemp] = await con.promise().query(Prefquery, [UID])
        const Pref = Preftemp.map((Prefresult) => Prefresult.Pref)
        
        console.log(Pref)
        const Itemsquery = 'SELECT DISTINCT g.Name FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ? AND o.AboutExpire=1'
        const [Itemstemp] = await con.promise().query(Itemsquery, [UID])
        const Expiryitems = Itemstemp.map((Items) => Items.Name)
    
        console.log(Expiryitems)
        if(Expiryitems.length === 0){
            return res.json({})
        }
    
        if(Pref.length !== 0){
            query = 'SELECT store.RID FROM ('
    
            for(let i=0; i< Pref.length; i++){
            let preference = Pref[i]
            let excludeTable = ''
    
            if (preference === 'Vegetarian') {
                excludeTable = 'vegetarian'
            } else if (preference === 'Non-dairy') {
                excludeTable = 'nondairy'
            } else if (preference === 'Vegan') {
                excludeTable = 'vegan'
            }
    
            if(Pref.length === 1){
                query ='SELECT store.RID FROM (SELECT * FROM ' +excludeTable + ') AS store WHERE LOWER(store.Ingredient) LIKE ? '
                break
            }
                
            if(i===0){
                query+="SELECT * FROM " +excludeTable + " "
            }
            else if(i<Pref.length-1){
                query+="INTERSECT SELECT * FROM " + excludeTable + " "
                
            }
            else{
                query+="INTERSECT SELECT * FROM " +excludeTable + ') AS store WHERE LOWER(store.Ingredient) LIKE ? '
                store.push(excludeTable)
            }
            }
            console.log(query)
        }
        else{
            query = 'SELECT r.RID FROM RECIPE r WHERE LOWER(r.Ingredient) LIKE ? '
        }
    
        storequery=query
        if(Expiryitems.length === 1){
        const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[0]}%`+'\')')
        query=temp
        const tempquery= "SELECT s.RID FROM (" + query + ") AS s ORDER BY RAND() LIMIT 5"
        query=tempquery
        }
        else{
        for(let j=0; j< Expiryitems.length; j++){
            if(j === 0){
            const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[j]}%`+'\')')
            query=temp
            }
            else if(j< Expiryitems.length-1){
            query += "INTERSECT " + storequery
            const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[j]}%`+'\')')
            query=temp
            }
            else{
            query += "INTERSECT " + storequery
            const temp=query.replace('?', 'LOWER(\''+`%${Expiryitems[j]}%`+'\')')
            query=temp
            const tempquery= "SELECT s.RID FROM (" + query + ") AS s ORDER BY RAND() LIMIT 5"
            query=tempquery
            }
        }
        }
        console.log(query)
    
        const [results] = await con.promise().query(query)
        let formattedResults
    
        const formattedArray = results.map((result) => result.RID)
        console.log(formattedArray)
    
        if(formattedArray.length===0){
            return res.json({})
        }
        else{
            const query2 = 'SELECT * FROM RECIPE WHERE RID IN (?)'
            const [result2] = await con.promise().query(query2, [formattedArray])
    
            formattedResults = result2.map((result) => ({
                RID: result.RID,
                Ingredient: result.Ingredient,
                Amount: result.Amount,
            }))
        }
    
        console.log(formattedResults)
        const structuredData = {}
    
        for (const result of formattedResults) {
            if (!structuredData[result.RID]) {
                structuredData[result.RID] = {
                    RID: result.RID,
                    ingredients: [],
                }
            }
        
            structuredData[result.RID].ingredients.push({
            Ingredient: result.Ingredient,
            Amount: result.Amount,
            })
        }
        
        const jsonData = Object.values(structuredData)
        
        console.log(jsonData)
        res.json(jsonData)
  
    } catch (error) {
        console.error('Error:', error.stack)
        database_error(res, error)
    }
  })
  
  // get recipe info using rid
  //done
  router.get("/get/recipe_info", async (req,res)=>{
    try{
      
        const RID=req.query.p1 ? req.query.p1.split(',') : []
    
        if(RID[0]==="ForceError"){
            throw new Error("Forced Error")
        }
    
        const query = 'SELECT * FROM RECIPE_INFO WHERE RID IN (?)'
        const [results] = await con.promise().query(query, [RID])
    
        if (results.length === 0) {
            return res.json({})
        }
    
        const formattedResults = results.map((result) => {
            return {
                RID: result.RID,
                Name: result.Rname, 
                Instruction: result.Instruction,
                YTLink: result.YoutubeLInk
            }
        })
  
        console.log('SUCCESS return recipe_info') 
        console.log(formattedResults)
        res.json(formattedResults)
        
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
  })

  module.exports = router