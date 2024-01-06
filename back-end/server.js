//TODO check connection with front end
const mysql = require("mysql2")
const express = require("express")
const https = require("https")
const fs = require("fs")
const moment = require("moment")
const cron = require("node-cron")
const admin = require("firebase-admin")
const WebSocket = require("ws")

const userRoutes = require('./userRoutes')
const itemRoutes = require('./itemRoutes')
const prefRoutes = require('./prefRoutes')
const dietReqRoutes=require('./dietReqRoutes')
const recipeRoutes=require('./recipeRoutes')
const dieticianRoutes=require('./dieticianRoutes')
const chatRoutes=require('./chatRoutes')
const otherRoutes=require('./otherRoutes')
const usertypeRoutes=require('./usertypeRoutes')

let userConnections = {}
let dieticianConnections = {}

//database connection
const con = require('./dbConfig')

//app
const app = express()
app.use(express.json())
app.set('dbConnection', con)
app.use('/users', userRoutes)
app.use('/items',itemRoutes)
app.use('/pref', prefRoutes)
app.use('/dietReq', dietReqRoutes)
app.use('/recipe', recipeRoutes)
app.use('/dietician', dieticianRoutes)
app.use(usertypeRoutes)
app.use(chatRoutes)
app.use(otherRoutes)

//use this to get more recipes when have the time to do so
/*
const RecipeKeypath='./RecipeKey.txt'
let RecipeAPIKey='&apiKey='
const RecipeAPIURL= 'https://api.spoonacular.com/recipes/findByIngredients?number=5&ranking=1&ingredients='

fs.readFile(RecipeKeypath, 'utf8', (err, data) => {
  if (err) {
    console.error('Error reading the file:', err)
  } else {
    RecipeAPIKey += data
  }
})*/

//change this to maybe minute or hourly for testing
//const schedule='*/20 * * * *' // M4 submission use
const schedule = '0 0 * * *' //per daily

const serviceAccount = require('./grocerymanager_firebase.json');

// Initialize the app with appropriate configurations
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
})


const certs = {
  key: fs.readFileSync('./key.pem'),
  cert: fs.readFileSync('./certificate.pem')
}

const server = https.createServer(certs, app)

const port = process.env.NODE_ENV === 'test' ? 3001 : 443;

if (process.env.NODE_ENV !== 'test') {
  server.listen(port, () => {
    console.log(`Server is running on port ${port}`);
  });
}

const wss = new WebSocket.Server({server})

function database_error(response, error) {
  response.status(500).send('Error querying the database'+error)
}

function query_success(response, message){
  response.status(200)
  response.json({Message: message})

}

function getcon(){
  return con
}

wss.on('connection', async (ws,req) => {
  console.log('Client connected')
  const actorId = req.headers['actor-id']
  const actorType = req.headers['actor-type'] // This could be 'user' or 'dietician'

  if (actorType === 'user') {
      userConnections[actorId] = ws;
  } else if (actorType === 'dietician') {
      dieticianConnections[actorId] = ws
  }

  // On receiving a message from the client
  ws.on('message', async (message) => {
    try{
   
      console.log('Received:', message)
      
      // Parse the message (assuming it's in JSON format)
      let parsedMessage = JSON.parse(message)
      
      // Store in the database
      let query = 'INSERT INTO CHAT (UID, DID, Text, Time, FROM_USER) VALUES (?, ?, ?, ?, ?)'
      const [result]=await con.promise().query(query, [parsedMessage.UID, parsedMessage.DID, parsedMessage.Text, new Date(), parsedMessage.FROM_USER])

      console.log('Message saved', result)

      forwardMessage(parsedMessage.FROM_USER, parsedMessage.DID, parsedMessage.UID, parsedMessage.Text)

      // Prepare notification
      let targetTable = parsedMessage.FROM_USER === 1 ? 'DIETICIAN' : 'USERS'
      let targetID = parsedMessage.FROM_USER === 1 ? parsedMessage.DID : parsedMessage.UID
      let oppositeID=parsedMessage.FROM_USER === 1 ? parsedMessage.UID : parsedMessage.DID
      let store= targetTable === 'DIETICIAN' ? 'DID' : 'UID'
      let receiveID= targetTable === 'DIETICIAN' ? 'UID' : 'DID'
      let queryToken = 'SELECT MessageToken FROM ' + targetTable +' WHERE ' + store+'=?'
      let [tokensResult] = await con.promise().query(queryToken, [targetID])
      let token = tokensResult[0]?.MessageToken

      if (token) {
        let notificationText = `You have a new message from ${parsedMessage.FROM_USER === 1 ? 'user' : 'dietitian'} with ${receiveID} ${oppositeID}`
        let messagePayload = {
          notification: {
            title: 'Chat notification',
            body: notificationText,
          },
          token
        }

        console.log(messagePayload)
        Messaging(messagePayload)
      }
    } catch(error){
        console.error('Database error:', error)
        ws.send(JSON.stringify({ type: 'error', message: 'Error saving the message' }))
    }
  })

  // When the client closes the connection
  ws.on('close', () => {
      console.log('Client disconnected');
  })
})

function forwardMessage(fromBit, DID, UID, Text){
  let actorWS = dieticianConnections[DID]
  if (fromBit === 1) {
      actorWS = dieticianConnections[DID]
  } else {
       actorWS = userConnections[UID]
  }

  if(actorWS){
      actorWS.send(Text)
  }
  else{
      console.log(`No active connection found for user with Reciever`)
  }
}

//the schedule for shopping reminder algorithm 
cron.schedule(schedule, () => {
  console.log('Cron job triggered for shoppingdata')
  processShoppingData()
})

// Define a function to process shopping data and generate reminders
//done 
async function processShoppingData() {
  try{
    console.log("algorithm tiggered")
    // User-defined settings
    const reminderPeriodDays = 5
    const numberOfVisits = 3// This can be changed

    // Store item purchase history

    // Initialize current date
    const currentDate = moment()

    // Initialize the expected run-out date for each item

    //Get all users UID and repeat for all UID
    const queryUsers='SELECT UID, MessageToken FROM USERS;'
    const [U] = await getcon().promise().query(queryUsers)
    const UIDTokenArray = U.map((temp) => {
      return {
        UID:temp.UID,
        Token:temp.MessageToken
      }
    });

    UIDTokenArray.forEach(async (entry)=>{
    // NEED TO UPDATE
    const purchaseHistory = {}
    const expectedRunOutDates = {}
    // Query to retrieve shopping data from the database
      const UID=entry.UID
      const query = 'SELECT g.Name, o.ItemCount, o.PurchaseDate FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ?'
      const [result] = await getcon().promise().query(query, [UID])

      const shoppingData=result.map((store)=>{
        return {
          item:store.Name,
          quantity:store.ItemCount,
          purchaseDate:store.PurchaseDate
        }
      })

      console.log(shoppingData)

      // Populate purchase history, also detecting anomalies
      shoppingData.forEach((shoppingEntry) => {
        const item = shoppingEntry.item
        const quantity = shoppingEntry.quantity
        const anomalyThreshold = quantity * 3 // Anomaly detection threshold (e.g., a quantity higher than this threshold is considered an anomaly)
        const purchaseDate=shoppingEntry.purchaseDate

        // Check for anomalies based on the threshold
        if (quantity > anomalyThreshold) {
          console.log(`Anomaly detected for ${item} on ${purchaseDate}: Quantity ${quantity} exceeds threshold.`)
          return
        }

        // Updating purchase history
        if (item in purchaseHistory) {
          purchaseHistory[item].push(purchaseDate)
        } else {
          purchaseHistory[item] = [purchaseDate]
        }

        // Calculate expected run-out date based on time frame
        if (!(item in expectedRunOutDates)) {
          expectedRunOutDates[item] = moment(purchaseDate).add(numberOfVisits, 'weeks').toDate()
        }
      })
    
      // Generate reminders
      for (const item in purchaseHistory) {

        // Calculate the average purchase frequency

        // Calculate the expected run-out date
        const expectedRunOutDate = expectedRunOutDates[item]

        // Calculate the reminder date
        const reminderDate = moment(expectedRunOutDate).subtract(reminderPeriodDays, 'days')

        // Check if it's time to send a reminder 
        if (currentDate.isSameOrAfter(reminderDate)) {
          console.log(`Reminder: Buy ${item} in the next ${reminderPeriodDays} days.`)
          const text=`Reminder: Buy ${item} in the next ${reminderPeriodDays} days.`

          const jsonContent = {
            notification: {
              title: 'Reminder for buying items',
              body: text,
            },
            token: entry.Token,
          }
          console.log("\n")
          console.log(jsonContent)
          Messaging(jsonContent)
        }
      }
    })
  }catch(error){
    console.error('Error:', error)
  }
}

// Call the function to process shopping data and generate reminders
//expiry date reminders schedule
cron.schedule(schedule, () => {
  console.log('Cron job triggered expiry date reminder')
  SendExpiryReminder()
})

//send notification to uses for items that is about to expire in 2 days or already expired for x days
//done
async function SendExpiryReminder(){
  //get all users that have items about to expiry
  try{
    const currentDate = new Date()

    const DisableSafeMode='SET SQL_SAFE_UPDATES = 0;'
    const queryupdate='UPDATE OWNS SET AboutExpire = CASE WHEN DATEDIFF(ExpireDate, CURDATE()) <= 2 THEN 1 ELSE 0 END;'
    const EnableSafeMode='SET SQL_SAFE_UPDATES = 1;'

    await getcon().promise().query(DisableSafeMode)
    await getcon().promise().query(queryupdate)
    await getcon().promise().query(EnableSafeMode)

    const queryselect='SELECT DISTINCT UID FROM OWNS WHERE AboutExpire=1'
    const [store] = await getcon().promise().query(queryselect)
    const UIDArray = store.map((temp) => temp.UID)
    console.log(UIDArray)
    
    //for each users
    for(let i=0; i<UIDArray.length; i++){
      const UID=UIDArray[i]
      const queryToken='SELECT u.MessageToken FROM USERS u WHERE UID=?'
      const queryItems='SELECT DISTINCT g.Name, g.Brand, o.ExpireDate, o.ItemCount FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = \'whatever\' OR g.Name = o.Name) WHERE o.UID = ? AND o.AboutExpire=1;'

      //send notification for all items and the date left
      //get the token using uid and call firebase using that
      // Define the message payload

      const t=await getcon().promise().query(queryToken, [UID])
      const [Items]=await getcon().promise().query(queryItems, [UID])
      const token=t[0].map((storetoken) => storetoken.MessageToken)
      console.log(t)
      console.log(token)

      const ItemArray = Items.map((item) => {
        const itemData = {
          Name: item.Name,
          DatesToExpire: Math.ceil((item.ExpireDate - currentDate) / (1000 * 60 * 60 * 24)), // Calculate days until expiration
          Quantity: item.ItemCount
        }
      
        if (item.Brand !== null) {
          itemData.Brand = item.Brand
        }
      
        return itemData
      })

      const itemsText = ItemArray.reduce((accumulator, item, index) => {
        const brandText = item.Brand ? `Brand: ${item.Brand}\n` : ''
        const daysToExpire = item.DatesToExpire
      
        let daysToExpireText = ''
      
        if (daysToExpire >= 0) {
          daysToExpireText = `Days to Expire: ${daysToExpire}`
        } else {
          daysToExpireText = `Item Already Expired for ${-daysToExpire} days`
        }
      
        const itemText = `Name: ${item.Name}\n${brandText}${daysToExpireText}\nQuantity: ${item.Quantity}\n`

        if (index % 2 === 0) {
          accumulator.push(itemText)
        } else {
          const combinedText = accumulator.pop() + itemText
          accumulator.push(combinedText)
        }

        return accumulator
      }, [])

      const messages = itemsText.map(text => {
        return{
          notification: {
            title: 'Reminder for items about to expire',
            body: 'Items About to Expire or already expired:\n'+ text,
          },
          token: token[0], 
        }
    })
      console.log(messages)
      messages.forEach(message => {
        Messaging(message)
      })
    }
  }catch(error){
  }
}

//the firebase notification function 
//done
//ChatGPT usage: No
async function Messaging(message){
// Send the message
  admin.messaging().send(message)
    .then((response) => {
      console.log('Successfully sent message:', response)
    })
    .catch((error) => {
      console.log('Error sending message:', error)
    })
}

module.exports = {
  app,
  SendExpiryReminder,
  processShoppingData,
  getcon
}
