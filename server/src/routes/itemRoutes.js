const express = require('express')
const router = express.Router()
const { UIDcheck, ItemIDcheck} = require('./check')
const UPCKeypath='./UPCKey.txt'
let UPCAPIKey= "?apikey="
const UPCAPIURL= "https://api.upcdatabase.org/product/"

fs.readFile(UPCKeypath, 'utf8', (err, data) => {
  if (err) {
    console.error('Error reading the file:', err)
  } else {
    UPCAPIKey += data
  }
})

function database_error(response, error) {
    response.status(500).send('Error querying the database'+error)
}
  
function query_success(response, message){
    response.status(200)
    response.json({Message: message})
}

//get items
//done
router.get("/get", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.query.p1
    
        if(UID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        try {
            await UIDcheck(con, UID)
        } catch (error) {
            console.error('Error:', error)
            return database_error(res, error.message)
        }
    
    
        const query = 'UPDATE OWNS o JOIN (SELECT o1.UPC,o1.UID,o1.ExpireDate,o1.ItemCount,ROW_NUMBER() OVER (PARTITION BY o1.UID ORDER BY o1.ExpireDate, o1.UPC ASC) AS NewItemID FROM OWNS o1 WHERE o1.UID =?) AS result ON o.UPC = result.UPC AND o.UID = result.UID AND o.ExpireDate=result.ExpireDate And o.ItemCount=result.ItemCount SET o.ItemID = result.NewItemID WHERE o.UID=?;'
        const query2 = 'SELECT DISTINCT g.Name, g.Brand, o.UPC, o.ExpireDate, o.ItemCount, o.ItemID FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ? ORDER BY o.ItemID ASC;'
    
        await con.promise().query(query, [UID, UID])
        const [result2] = await con.promise().query(query2, [UID])        
    
        if (result2.length === 0) {
            return res.json({})
        }
    
        const items = result2.map((result) => {
        const formattedDate = new Date(result.ExpireDate).toLocaleDateString()
            return {
            UPC: result.UPC,
            Name: result.Name, 
            Brand: result.Brand,
            ExpireDate: formattedDate,
            ItemCount: result.ItemCount,
            ItemID: result.ItemID
            }
        })
    
        console.log('SUCCESS Get items') 
    
        res.json(items)
        
    } catch (error) {
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
//add items
//done
router.post("/add", async (req, res) => {
    try {
        const con = req.app.get('dbConnection')
        const UID = req.body.p1
        const UPCs = req.body.p2
        const ExpireDates = req.body.p3
        const ItemCounts = req.body.p4
    
        const currentDate = new Date()
        const currentDateString = currentDate.toISOString().split('T')[0]
        console.log(currentDateString)
    
        let returnStatement = ''
        const values = []
        
        if(UID==="ForceError" && UPCs==="ForceError"&& ExpireDates==="ForceError" &&ItemCounts==="ForceError"){
            throw new Error("Forced Error")
        }
    
        try {
            await UIDcheck(con, UID)
        } catch (error) {
            console.error('Error:', error)
            return database_error(res, error.message)
        }
    
    
        if (UPCs.length !== ExpireDates.length || UPCs.length !== ItemCounts.length) {
            return res.status(400).send('Array lengths must match.')
        }
    
        for (let i = 0; i < UPCs.length; i++) {
            const UPC = UPCs[i]
            const [groceryResults] = await con.promise().query('SELECT * FROM GROCERIES WHERE UPC = ?', [UPC])
    
            if (groceryResults.length === 0) {

                try {
                    if(UPC==="1234567890"){
                    throw new Error("Forced Error")
                    }
        
                    const productData = await fetchDataFromAPI(UPCAPIURL + UPC + UPCAPIKey)

                    if (productData.success) {
                    console.log(productData)

                    const insertGroceryQuery = 'INSERT INTO GROCERIES (UPC, Name, Brand, Category) VALUES (?, ?, ?, ?)'
                    await con.promise().query(insertGroceryQuery, [UPC, productData.title, productData.brand, productData.title])
                    values.push([UID, UPC, ExpireDates[i], ItemCounts[i], currentDate])

                    } else {
                    returnStatement += UPC + " "
                    console.error(`Product not found for UPC: ${UPC}`)
                    }
                
                } catch (error) {
                    returnStatement += UPC + " "
                    console.error('Error fetching product data:', error)
                }
            } else {
                values.push([UID, UPC, ExpireDates[i], ItemCounts[i], currentDate])
            }
        }
    
        if (values.length > 0) {
            const insertOwnsQuery = 'INSERT INTO OWNS (UID, UPC, ExpireDate, ItemCount, PurchaseDate) VALUES ?'
            await con.promise().query(insertOwnsQuery, [values])
        }
    
        if (returnStatement === '') {
            console.log('SUCCESS ADDED ITEMS')
            res.json({ message: 'SUCCESS ADDED ITEMS' })
        } else {
            console.log('Values not added for UPCs: ' + returnStatement)
            res.json({ message: 'Values not added for UPCs: ' + returnStatement })
        }
    } catch (error) {
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
  
//get data from the UPC API
function fetchDataFromAPI(url) {
    return new Promise((resolve, reject) => {
        https.get(url, (response) => {
            let data = ''
    
            response.on('data', (chunk) => {
            data += chunk
            })
    
            response.on('end', () => {
            try {
                const jsonData = JSON.parse(data)
                resolve(jsonData)
            } catch (error) {
                reject(error)
            }
            })
    
            response.on('error', (error) => {
            reject(error)
            })
        })
    })
}
  
//add items manually
//done
router.post("/add_man", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.body.p1
        const UPC = req.body.p2 // should be -1
        const ExpireDate = req.body.p3 
        const ItemCount = req.body.p4
        const ItemName = req.body.p5 
    
        if(UID==="ForceError" && UPC==="ForceError"&& ExpireDate==="ForceError" &&ItemCount==="ForceError"&& ItemName==="ForceError"){
            throw new Error("Forced Error")
        }
    
        if ( ExpireDate.length !== ItemCount.length || ItemCount.length !== ItemName.length) {
            return res.status(400).send('Arrays should have the same length')
        }
  
        const currentDate = new Date()
        const currentDateString = currentDate.toISOString().split('T')[0]
        console.log(currentDateString)
        
        const values = []
        const store=[]
        for (let i = 0; i <ItemName.length; i++) {
            if(i<ItemName.length-1){
            store.push(([UPC, ItemName[i],]))
            values.push(([UID, UPC, ExpireDate[i], ItemCount[i],ItemName[i], currentDate]))
            }
            else{
            store.push(([UPC, ItemName[i]]))
            values.push(([UID, UPC, ExpireDate[i], ItemCount[i], ItemName[i], currentDate]))
            }
        }
        console.log(values)
    
        const query = 'INSERT IGNORE INTO GROCERIES (UPC, Name) VALUES ?'
        const query2 = 'INSERT INTO OWNS (UID, UPC, ExpireDate, ItemCount, Name, PurchaseDate) VALUES ?'
    
        await con.promise().query(query, [store])
        await con.promise().query(query2, [values])
    
        console.log('SUCCESS ADDED items') 
        query_success(res, 'SUCCESS ADDED ITEMS MANUAL')
     
    }catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
//delete items
//done
router.post("/delete", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.body.p1
        const ItemID=req.body.p2 
        
        if(UID==="ForceError"&& ItemID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        try {
            await UIDcheck(con, UID)
            await ItemIDcheck(con, UID, ItemID)
        } catch (error) {
            console.error('Error:', error)
            return database_error(res, error.message)
        }
    
  
        const query = 'DELETE FROM OWNS WHERE UID= ? AND ItemID IN (?);'
    
        await con.promise().query(query, [UID, ItemID])
    
        console.log('SUCCESS DELETED items')
        query_success(res, 'DELETED ITEM')

    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})
  
//done
//update items
router.post("/update", async (req,res)=>{
    try{
        const con = req.app.get('dbConnection')
        const UID=req.body.p1
        const ItemID=req.body.p2 
        const UPC = req.body.p3 
        const ExpireDate = req.body.p4 
        const ItemCount = req.body.p5 
    
        if(UID==="ForceError" && UPC==="ForceError"&& ExpireDate==="ForceError" &&ItemCount==="ForceError"&& ItemID==="ForceError"){
            throw new Error("Forced Error")
        }
    
        try {
            await UIDcheck(con, UID)
        } catch (error) {
            console.error('Error:', error)
            return database_error(res, error.message)
        }
    
        if (UPC.length !== ExpireDate.length || UPC.length !== ItemCount.length || ItemID.length !== UPC.length) {
            return res.status(400).send('Arrays should have the same length')
        }
      
        const values = []
        for (let i = 0; i <UPC.length; i++) {
            values.push([ExpireDate[i], ItemCount[i], UID, UPC[i], ItemID[i]])
        }

        console.log(values)
        const query = 'UPDATE OWNS SET ExpireDate=?, ItemCount=? WHERE UID=? AND UPC=? AND ItemID=?;'
        
        values.forEach(async (values) => {
            const executedQuery = mysql.format(query, values)
    
            console.log('Executed Query:', executedQuery)
                
            await con.promise().query(executedQuery)
            console.log('SUCCESS Updated items')
        })
    
        query_success(res, 'SUCCESS Updated items')
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

module.exports = router