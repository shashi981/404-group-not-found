const express = require('express')
const router = express.Router()


function database_error(response, error) {
    response.status(500).send('Error querying the database'+error)
}
  
function query_success(response, message){
    response.status(200)
    response.json({Message: message})
}

//give the user type of the email if exist: admin, dietician or user else return does not exist
//done
router.get("/users_type", async (req,res)=>{
    try {
        const con = req.app.get('dbConnection')
        const Email = req.query.p1
    
        if(Email==="ForceError"){
            throw new Error("Forced Error")
        }
    
        const query1 = 'SELECT * FROM USERS WHERE Email=?'
        const [userResults] = await con.promise().query(query1, [Email])
    
        if (userResults.length > 0) {
            console.log('Entry exists as user')
            return query_success(res, 'User\n')
        }
    
        const query2 = 'SELECT * FROM DIETICIAN WHERE Email=?'
        const [dieticianResults] = await con.promise().query(query2, [Email]);
    
        if (dieticianResults.length > 0) {
            console.log('Entry exists as dietician')
            return query_success(res, 'Dietician\n')
        }
    
        const query3 = 'SELECT * FROM ADMIN WHERE Email=?'
        const [adminResults] = await con.promise().query(query3, [Email])
    
        if (adminResults.length > 0) {
            console.log('Entry exists as admin')
            return query_success(res, 'Admin\n')
        }
    
        console.log('This is an entry that does not exist')
    
        return query_success(res, 'Does not exist\n')
    } catch (error) {
        console.error('Error:', error.stack)
        database_error(res, error)
    }
})

module.exports = router