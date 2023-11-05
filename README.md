# 404-group-not-found
CPEN321_APP

## Description: 
Grocery Manager is an all in one resource manager that keeps track of your groceries. In addition you can receive professional advice from certified dieticians on our platform, acquire new recipes based on your current inventory, and have reminders for when it is time to restock on items you purchase regularly. With our application, uploading is quick, simple, and you will never have to worry about food going bad again.

## Language Used:
- Frontend: Java, XML
- Backend: NodeJS
- Database: MySQL

## Minor issues and limitations:
- Recipe: Due to the limited amount of recipes in the database, there is a high tendency that there will be no recipe shown if there are a lot of items about to expire.

- Chat: Only 10 messages are shown, and both the user and dietician have to exit the chat box and reenter it to see the update of the sent message from the other side

## Front End:
### Viewable activities:
- Sign In With Google
- Main Landing Page
- Inventory
- Add Items Manually
- Add Items Using UPC Barcode
- Connector Page For Adding Items
- Chat Interface
- Recipe Suggestions
- Profile View
- Settings
- Dietitian Dashboard (Must have dietitian permissions)
- Admin Dashboard (Must have admin permissions)
### Libraries Used:
- OKHTTP3
- JSON
- JourneyApps Barcode Scanner
### APIs Used
- Google Sign In API
- Socket.io

## Backend-NodeJS: 

### Libraries used:
- mysql2
- express
- https
- fs
- moment
- node-cron
- firebase-admin
- ws

### External API used:
- Recipe API:  https://www.themealdb.com/api.php
- UPC code API:  https://upcdatabase.org/api

### APIs: 
#### GET: 
- /delete/users
- /get/users
- /get/items
- /delete/pref
- /get/pref
- /get/pref_list
- /get/recipe
- /get/recipe_info
- /get/dietician
- /add/dietReq
- /get/dietReq
- /approve/dietReq
- /remove/dietReq
- /get/availableDieticians
- /get/users_type
- /get/usersForDietician/:dieticianId
- /get/messageToken/:DID
- /get/chatHistory/:UID/:DID

#### POST:
- /add/users
- /add/items
- /add/items_man
- /delete/items
- /update/items
- /add/pref

## Backend-MySQL:

### Tables:
- USERS
- GROCERIES
- OWNS
- RECIPE
- RECIPE_INFO
- DIETICIAN
- DIETICIAN_REQUEST
- CHAT
- PREFERENCE
- PREF_LIST
- ADMIN
- Vegetarian_exclude
- Nondairy_exclude
- Vegan_exclude

### Views:
- nondairy
- vegetarian
- vegan


## Build Specification:
- Android SDK version 33.
- Google services version 4.4.0
- Node version 20.9.0

## Acknowledgements:
- We would like to thank the following individuals and organizations for their support, inspiration and guidance over the course of this project. This project would not be possible without them:
    - CPEN 321 Course Staff
    - The University of British Columbia


404 Group Not Found - 2023
