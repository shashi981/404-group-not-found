const express = require('express')
const router = express.Router()

function database_error(response, error) {
    response.status(500).send('Error querying the database'+error)
}

router.get('/get/availableDieticians', async (req, res) => {
    try{
        const con = req.app.get('dbConnection')
        const query = 'SELECT DID, FirstName, LastName, Email, ProfileURL FROM DIETICIAN'
        const [results]=await con.promise().query(query)
        res.json(results)
        
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

router.get('/get/usersForDietician/:dieticianId', async (req, res) => {
    try{
        const con = req.app.get('dbConnection')
        const dieticianId = req.params.dieticianId

        const query = `
            SELECT DISTINCT U.*
            FROM USERS U
            JOIN CHAT C ON U.UID = C.UID
            WHERE C.DID = ?
        `
        const [results]=await con.promise().query(query, [dieticianId])
        res.json(results)

    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

router.get('/get/messageToken/:DID', async (req, res) => {
    try{
        const con = req.app.get('dbConnection')
        const DID = req.params.DID 

        const query = 'SELECT MessageToken FROM DIETICIAN WHERE DID = ?'
        const [results]=await con.promise().query(query, [DID])

        if (results.length === 0) {
            res.status(404).send('Dietitian not found')
        } else {
            res.json({ MessageToken: results[0].MessageToken })
        }

    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

// Endpoint to retrieve chat history between a user and a dietician
router.get('/get/chatHistory/:UID/:DID', async (req, res) => {
    try{
        const con = req.app.get('dbConnection')
        const UID = req.params.UID
        const DID = req.params.DID
        const limit = 100  // max number of messages to return

        const query = 'SELECT * FROM CHAT WHERE UID = ? AND DID = ? ORDER BY Time DESC LIMIT ?'
        const [results]=await con.promise().query(query, [UID, DID, limit])
        res.json(results)
    
    } catch(error){
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

module.exports = router