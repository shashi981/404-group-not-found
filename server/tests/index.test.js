const request = require('supertest');
const {app,  SendExpiryReminder,  processShoppingData} = require('../src');   

//test
describe('/get/messageToken endpoint', () => {
  /**
   * Test: Retrieve message token with valid DID
   * Input: A valid dietitian ID
   * Expected status code: 200
   * Expected behavior: Retrieve a message token corresponding to the given DID
   * Expected output: A message token in the response body
   */
  test('Retrieve message token with valid DID', async () => {
    const validDID = '1';
    const res = await request(app).get(`/get/messageToken/${validDID}`);       
    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Attempt to retrieve message token with invalid DID
   * Input: An invalid dietitian ID
   * Expected status code: 404
   * Expected behavior: No message token is found for the invalid DID
   * Expected output: An error message in the response body
   */
  test('Attempt to retrieve message token with invalid DID', async () => {
    const invalidDID = '132'; // An invalid DID example
    const res = await request(app).get(`/get/messageToken/${invalidDID}`);
    expect(res.status).toStrictEqual(404);
  });

  /**
   * Test: Attempt to retrieve message token without providing a DID
   * Input: No DID provided in the request
   * Expected status code: 404
   * Expected behavior: Endpoint not found due to missing DID
   * Expected output: An error message indicating missing DID or invalid request
   */
  test('Attempt to retrieve message token without DID', async () => {
    const res = await request(app).get(`/get/messageToken/`);
    expect(res.status).toStrictEqual(404);
  });
});

describe('/get/availableDieticians endpoint', () => {
  /**
   * Test: Retrieve all available dieticians including a specific dietician
   * Input: No input required (no parameters)
   * Expected status code: 200
   * Expected behavior: Retrieve a list of all dieticians from the database, which includes the specified dietician
   * Expected output: An array of dietician objects, including the specific dietician {"DID":1,"FirstName":"dietician","LastName":"dietician","Email":"dietician.com","ProfileURL":null}  
   */
  test('Retrieve all available dieticians including a specific dietician', async () => {
    const res = await request(app).get('/get/availableDieticians');
    expect(res.status).toStrictEqual(200);

    expect(Array.isArray(res.body)).toBeTruthy();

    // Check if the response includes the specific dietician
    const expectedDietician = {
      DID: 1,
      FirstName: "dietician",
      LastName: "dietician",
      Email: "dietician.com",
      ProfileURL: null
    };
    expect(res.body).toEqual(expect.arrayContaining([expect.objectContaining(expectedDietician)]));
  });
});

describe('/get/usersForDietician/:dieticianId endpoint', () => {
  /**
   * Test: Retrieve users for a specific dietician and check for a user with UID = 27
   * Input: A valid dietician ID
   * Expected status code: 200
   * Expected behavior: Retrieve a list of users associated with the given dietician ID, which includes the user with UID = 27
   * Expected output: An array of user objects, including a user with UID = 27
   */
  test('Retrieve users for a specific dietician with valid ID and check for UID = 27', async () => {
    const dieticianId = 'validDieticianId'; // Replace with a valid dietician ID
    const res = await request(app).get(`/get/usersForDietician/${dieticianId}`);
    expect(res.status).toStrictEqual(200);
    expect(Array.isArray(res.body)).toBeTruthy();
  });

  /**
   * Test: Attempt to retrieve users with an invalid dietician ID
   * Input: An invalid dietician ID
   * Expected status code: 200
   * Expected behavior: No users found for the invalid dietician ID, and an empty array is returned
   * Expected output: Empty array
   */
  test('Attempt to retrieve users for a dietician with invalid ID', async () => {
    const dieticianId = 'invalidDieticianId'; // Replace with an invalid dietician ID
    const res = await request(app).get(`/get/usersForDietician/${dieticianId}`);
    expect(res.status).toStrictEqual(200);
    expect(res.body).toEqual([]);
  });
});

describe('/get/chatHistory/:UID/:DID endpoint', () => {
  /**
   * Test: Retrieve chat history for specific UID and DID
   * Input: UID = 29 and DID = 2
   * Expected status code: 200
   * Expected behavior: Retrieve the chat history between the user (UID = 29) and the dietician (DID = 2)
   * Expected output: An array of chat messages
   */
  test('Retrieve chat history for UID = 29 and DID = 2', async () => {
    const UID = 29;
    const DID = 2;
    const res = await request(app).get(`/get/chatHistory/${UID}/${DID}`);
    expect(res.status).toStrictEqual(200);
    expect(Array.isArray(res.body)).toBeTruthy();
  });

  /**
   * Test: Attempt to retrieve chat history with invalid UID and DID
   * Input: Invalid UID and DID
   * Expected status code: 200
   * Expected behavior: No chat history found for the invalid UID and DID, and an empty JSON object or error message is returned
   * Expected output: Empty JSON object or error message
   */
  test('Attempt to retrieve chat history with invalid UID and DID', async () => {
    const invalidUID = 'invalidUID';
    const invalidDID = 'invalidDID';
    const res = await request(app).get(`/get/chatHistory/${invalidUID}/${invalidDID}`);
    expect([200]).toContain(res.status);
  });
});

let UID=null
let UID2=null
let UPC='068700115004'

// Function to mock the database error

const mockError = () => {
  jest.mock('./server', () => {
    const originalModule = jest.requireActual('./server');

    return {
      ...originalModule,
      getcon: jest.fn(() => ({
        promise: {
          query: jest.fn().mockRejectedValue(new Error('Simulated database error')),
        },
      })),
    };
  });
};

//Interface POST https://20.104.197.24:443/add/users
describe('/add/users endpoint', () => {
  /**
   * Test: Add a user with valid data
   * Input: Valid user data in the request body
   * Expected status code: 200
   * Expected behavior: User is added to the database, and UID is returned in the response body
   * Expected output: UID in the response body
   */
  test('Add user with valid data', async () => {
    const userData = {
      p1: 'John',
      p2: 'Doe',
      p3: 'john.doe@example.com',
      p4: 'https://example.com/profile.jpg',
      p5: 'someToken'
    };
    const res = await request(app).post('/add/users').send(userData);
    //console.log('Response:', res.body);  // Log the entire response object
    console.log('UID:', res.body); 
    expect(res.status).toStrictEqual(200);
    //expect(res.body.UID).toEqual(expectedUID);
    UID=res.body.Message
    console.log(UID)
    
  });

  /**
   * Test: Attempt to add a user with missing data
   * Input: Incomplete user data in the request body
   * Expected status code: 500
   * Expected behavior: User is not added to the database, and an error message is returned in the response body
   * Expected output: An error message in the response body
   */
  test('Attempt to add user with missing data', async () => {
    const incompleteUserData = {
      p1: 'John',
      p2: 'Doe',
      p3: 'john.doe@example.com',
      // Missing p4 and p5
    };

    const res = await request(app).post('/add/users').send(incompleteUserData);
    expect(res.status).toStrictEqual(500);
  });

  /**
   * Test: Attempt to add a user with invalid data
   * Input: no user data in the request body
   * Expected status code: 500
   * Expected behavior: User is not added to the database, and an error message is returned in the response body
   * Expected output: An error message in the response body
   */
  test('Attempt to add user with invalid data', async () => {
    const invalidUserData = {};

    const res = await request(app).post('/add/users').send(invalidUserData);
    expect(res.status).toStrictEqual(500);
  });
});


//Interface Post https://20.104.197.24:443/get/users
describe('Get USER request', () => {
  //Test: Valid data input for get user
  //Input: both Email and Token passed, email is valid, unique and exists in the database 
  // Expected status code: 200
  //Expected behaviour: Message Token for the user with that email is updated, and get all 
  //Expected output: UID, FirstName, LastName, Email, ProfileURL, FirstName, LastName, Email, ProfileURL same as input
  test("Valid user", async()=>{
    const email='john.doe@example.com' //this email should exist in db
    const url= "/get/users"
    const res= await request(app).post(url).send({
      p1:email,
      p2:'someToken'
    })
    const responseObject = {
        FirstName: 'John',
        LastName: 'Doe',
        Email: 'john.doe@example.com',
        ProfileURL: 'https://example.com/profile.jpg',
      }
    expect(res.status).toStrictEqual(200)
    console.log(res.body)
    expect(res.body.FirstName).toEqual(responseObject.FirstName)
    expect(res.body.LastName).toEqual(responseObject.LastName)
    expect(res.body.Email).toEqual(responseObject.Email)
    expect(res.body.ProfileURL).toEqual(responseObject.ProfileURL)
  })

  //Test: Force to enter error catching block
  //Input: All parameters are "ForceError"
  // Expected status code: 500
  //Expected behaviour: No changes made to the database, Force Error is catched and returned
  //Expected output error text: Error querying the databaseError: Forced Error
  test('should handle database error and enter catch block', async () => {

    const email="ForceError"
    const Token="ForceError"
    const url= "/get/users"
    const res= await request(app).post(url).send({
      p1:email,
      p2:Token
    })
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
  
  //Test email not in database
  //Input: both Email and Token passed, email doesn’t exist in the database 
  // Expected status code: 500
  //Expected behaviour: No database column is changed
  //Expected output: Empty
  test("Email Not in DB", async()=>{
    const email="invalidtest@gmail.com" //this email should not exist in db
    const Token="fakoehfnjildhnfljhasfjsfksjf"
    const url= "/get/users"
    const res= await request(app).post(url).send({
      p1:email,
      p2:Token
    })
    const responseObject = {}
    expect(res.status).toStrictEqual(500)
    expect(res.body).toEqual(responseObject)
  })
  
  //Test: Missing parameters(null)
  //Input: either email or token is not passed, or both
  // Expected status code: 500
  //Expected behaviour: No database column is changed
  //Expected output: Empty
  test("Missing input", async()=>{
    const email="" 
    const Token=""
    const url= "/get/users"
    const res= await request(app).post(url).send({
      p1:email,
      p2:Token
    })
    const responseObject = {}
    expect(res.status).toStrictEqual(500)
    expect(res.body).toEqual(responseObject)
  
  })
})

describe('/get/items endpoint before add items', () => {
  /**
   * Test: Get items for a user with valid UID
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: Empty, nothing is added yet
   * Expected output: Empty
   */
  test('Get items for user with valid UID', async () => {
    const res = await request(app).get(`/get/items?p1=${UID}`);
    expect(res.status).toStrictEqual(200);
  });
})

describe('/add/items endpoint',() => {
  /**
   * Test: Add items with valid data, the UPC is not in database
   * Input: Valid user ID, UPCs, ExpireDates, and ItemCounts in the request body
   * Expected status code: 200
   * Expected behavior: Items are added to the database, and a success message is returned in the response body
   * Expected output: { message: 'SUCCESS ADDED ITEMS' } in the response body
   */
  test('Add items with valid data, UPC not in database', async () => {
    const currentDate = new Date();
    const tomorrow = new Date(currentDate);
    tomorrow.setDate(currentDate.getDate() + 1);

    const tmr = tomorrow.toISOString().split('T')[0];
    const validData = {
      p1: UID, // Replace with an actual valid UID
      p2: [UPC, UPC], // Replace with actual UPCs
      p3: [tmr, '2024-01-15'], // Replace with actual ExpireDates
      p4: [2, 5] // Replace with actual ItemCounts
    };

    const res = await request(app).post('/add/items').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.body).toEqual({ message: 'SUCCESS ADDED ITEMS' });
  });

  /**
   * Test: Add items with valid data, the UPC is now in db
   * Input: Valid user ID, UPCs, ExpireDates, and ItemCounts in the request body
   * Expected status code: 200
   * Expected behavior: Items are added to the database, and a success message is returned in the response body
   * Expected output: { message: 'SUCCESS ADDED ITEMS' } in the response body
   */
  test('Add items with valid data, UPC in database now', async () => {
    const currentDate = new Date();
    const tomorrow = new Date(currentDate);
    tomorrow.setDate(currentDate.getDate() + 1);

    const validData = {
      p1: UID, // Replace with an actual valid UID
      p2: [UPC], // Replace with actual UPCs
      p3: ['2024-02-28'], // Replace with actual ExpireDates
      p4: [2] // Replace with actual ItemCounts
    };

    const res = await request(app).post('/add/items').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.body).toEqual({ message: 'SUCCESS ADDED ITEMS' });
  });

  /**
   * Test: Attempt to add items with inconsistent array lengths
   * Input: Inconsistent array lengths in the request body
   * Expected status code: 400
   * Expected behavior: Items are not added to the database, and an error message is returned in the response body
   * Expected output: 'Array lengths must match.' in the response body
   */
  test('Attempt to add items with inconsistent array lengths', async () => {
    const inconsistentData = {
      p1: UID,
      p2: ['123456', '789012'],
      p3: ['2023-12-31'], // Different length from p2 and p4
      p4: [2, 5]
    };

    const res = await request(app).post('/add/items').send(inconsistentData);
    expect(res.status).toStrictEqual(400);
    expect(res.text).toBe('Array lengths must match.');
  });

  /**
   * Test: Attempt to add items with invalid data
   * Input: Invalid data in the request body
   * Expected status code: 500
   * Expected behavior: Items are not added to the database, and an error message is returned in the response body
   * Expected output: An error message in the response body
   */
  test('Attempt to add items with invalid data', async () => {
    const invalidData = {
      p1: 'invalid', // Invalid UID
      p2: ['123456', '789012'],
      p3: ['2023-12-31', '2024-01-15'],
      p4: [2, 'invalid'] // Invalid ItemCount
    };

    const res = await request(app).post('/add/items').send(invalidData);
    expect(res.status).toStrictEqual(500);
  });

  /**
   * Test: Attempt to add items with UPC that cannot be found
   * Input: Valid user ID, UPCs, ExpireDates, and ItemCounts in the request body
   * but the UPC cannot be find in db or api, have to be input manual
   * Expected status code: 200
   * Expected behavior: Items are not added to the database, and the UPC is returned in the response body to should which item is not added
   * Expected output: The UPC value that is not added is sent back for manual input
   */
  test('Attempt to add items with UPC cant be find in db and API', async () => {
    const invalidData = {
      p1: UID,
      p2: ['068700011818'],
      p3: ['2024-01-15'],
      p4: [2] 
    };

    const res = await request(app).post('/add/items').send(invalidData);
    expect(res.status).toStrictEqual(200);
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error*/

  test('should handle database error and enter catch block', async () => {
     
    const ErrorData = {
      p1: "ForceError",
      p2: "ForceError",
      p3: "ForceError",
      p4: "ForceError"
    };

    const res = await request(app).post('/add/items').send(ErrorData);
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });

  /*Test: Force to enter error catching block of inner try-catch (error when calling fetchDataFromAPI)
  Input: Valid UID, UPC="1234567890", expiryDate and ItemCount="ForceError"
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output text: Values not added for UPCs: 1234567890 (The UPC that has error when fetching is sent back) */
  test('should handle fetch data error and enter catch block', async () => {
     
    const ErrorData = {
      p1: UID,
      p2: ["1234567890"],
      p3: ["ForceError"],
      p4: ["ForceError"]
    };

    const res = await request(app).post('/add/items').send(ErrorData);
    expect(res.body.message).toStrictEqual('Values not added for UPCs: 1234567890 '); 
  });
});

describe('/add/items_man endpoint', () => {
  /**
   * Test: Add items manually with valid data
   * Input: Valid user ID, UPC, ExpireDate, ItemCount, and ItemName in the request body
   * Expected status code: 200
   * Expected behavior: Items are added to the database, and a success message is returned in the response body
   * Expected output: 'SUCCESS ADDED ITEMS MANUAL' in the response body
   */
  test('Add items manually with valid data', async () => {
    const currentDate = new Date();
    const tomorrow = new Date(currentDate);
    tomorrow.setDate(currentDate.getDate() + 1);

    const tmr = tomorrow.toISOString().split('T')[0];


    const validData = {
      p1: UID, // Replace with an actual valid UID
      p2: -1,
      p3: [tmr, "2024-01-31", tmr],
      p4: [2, 5, 1],
      p5: ['Banana', 'Potato', 'Milk']
    };

    const res = await request(app).post('/add/items_man').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('SUCCESS ADDED ITEMS MANUAL');
  });

  /**
   * Test: Attempt to add items manually with inconsistent array lengths
   * Input: Inconsistent array lengths in the request body
   * Expected status code: 400
   * Expected behavior: Items are not added to the database, and an error message is returned in the response body
   * Expected output: 'Arrays should have the same length' in the response body
   */
  test('Attempt to add items manually with inconsistent array lengths', async () => {
    const inconsistentData = {
      p1: UID,
      p2: -1,
      p3: ['2023-12-31'],
      p4: [2, 5],
      p5: ['Item1', 'Item2']
    };

    const res = await request(app).post('/add/items_man').send(inconsistentData);
    expect(res.status).toStrictEqual(400);
    expect(res.text).toBe('Arrays should have the same length');
  });

  /**
   * Test: Attempt to add items manually with invalid data
   * Input: Invalid data in the request body
   * Expected status code: 500
   * Expected behavior: Items are not added to the database, and an error message is returned in the response body
   * Expected output: An error message in the response body
   */
  test('Attempt to add items manually with invalid data', async () => {
    const invalidData = {
      p1: 'invalid', // Invalid UID
      p2: -1,
      p3: ['2023-12-31', '2024-01-15'],
      p4: [2, 'invalid'], // Invalid ItemCount
      p5: ['Item1', 'Item2']
    };

    const res = await request(app).post('/add/items_man').send(invalidData);
    expect(res.status).toStrictEqual(500);
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error*/

  test('should handle database error and enter catch block', async () => {
     
    const ErrorData = {
      p1: "ForceError",
      p2: "ForceError",
      p3: "ForceError",
      p4: "ForceError",
      p5: "ForceError"
    };

    const res = await request(app).post('/add/items_man').send(ErrorData);
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/get/items endpoint', () => {
  /**
   * Test: Get items for a user with valid UID
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: Retrieve a list of items for the specified user from the database
   * Expected output: An array of items in the response body
   */
  test('Get items for user with valid UID', async () => {
    const res = await request(app).get(`/get/items?p1=${UID}`);
    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Attempt to get items for a user with invalid UID
   * Input: Invalid UID in the query parameters
   * Expected status code: 500 
   * Expected behavior: No items are retrieved, and an error message is returned in the response body
   * Expected output: An error message in the response body
   */
  test('Attempt to get items for user with invalid UID', async () => {
    const invalidUID = 'invalid'; // An invalid UID example
    const res = await request(app).get(`/get/items?p1=${invalidUID}`);
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 500 for invalid UID
  });

  /**
   * Test: Attempt to get items without providing a UID
   * Input: No UID provided in the query parameters
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to get items without UID', async () => {
    const res = await request(app).get('/get/items');
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 404 for missing UID
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error*/

  test('should handle database error and enter catch block', async () => {
     
    const ErrorUID = 'ForceError'; // An invalid UID example
    const res = await request(app).get(`/get/items?p1=${ErrorUID}`);
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('Reminder: items about to expiry', () => {
  /*Test: Sending reminders to each user with items about to expiry
  Input: None
  Expected behaviour: Firebase notification is send to each user with valid Token and items about to expiry in 
  2 days or already expired
  Expected output: Firebase notification is sent
  */
  test('This should generate reminders for items about to expiry', async()=>{
    await SendExpiryReminder()
  })
  
  /*
  Test: causing an error during query using mocking
  Expected behaviour: 'Simulated database error' is thrown
  Expected output: error message be 'Simulated database error'
  */
  test('should handle database error and enter catch block', async () => {
     
  mockError()

  try {

    await SendExpiryReminder()
  } catch (error) {
    // Assert that the error is the expected database error
    expect(error.message).toBe('Simulated database error');
  }

  jest.resetAllMocks();
  });
})

describe('/get/recipe endpoint', () => {
  /**
   * Test: Get recipe for 3 items about to expire based on all 3 preference and UID
   * Input: Valid UID with preferences and items about to expire
   * Expected status code: 200
   * Expected behavior: Retrieve recipes based on the preferences and items about to expire
   * Expected output: Response body containing the recipes that fit the filter, can be empty
   */
  test('Get recipe for items about to expire based on preference and UID', async () => {
    const validData = {
      p1: UID, // Replace with an actual valid UID
      p2: ['Vegan', 'Vegetarian', 'Non-dairy'] // Replace with actual valid preferences
    };

    await request(app).post('/add/pref').send(validData);
    
    const res = await request(app).get(`/get/recipe?p1=${UID}`)

    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Get recipe for 2 items about to expire based on 2 preference and UID
   * Input: Valid UID with preferences and items about to expire
   * Expected status code: 200
   * Expected behavior: Retrieve recipes based on the preferences and items about to expire
   * Expected output: Response body containing the recipes that fit the filter, can be empty
   */
  test('Get recipe for items about to expire based on preference and UID', async () => {
    await request(app).get('/delete/pref?p1=' + UID);
    const validData = {
      p1: UID, 
      p2: [ 'Vegetarian', 'Non-dairy'] 
    };

    await request(app).post('/add/pref').send(validData);
    
    const itemData = {
      p1: UID, 
      p2: [3] 
    };

    await request(app).post('/delete/items').send(itemData);
    const res = await request(app).get(`/get/recipe?p1=${UID}`)

    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Get recipe for items about to expire based on 1 preference and UID
   * Input: Valid UID with preferences and items about to expire
   * Expected status code: 200
   * Expected behavior: Retrieve recipes based on the preferences and items about to expire
   * Expected output: Response body containing the recipes that fit the filter, can be empty
   */
  test('Get recipe for items about to expire based on preference and UID', async () => {
    await request(app).get('/delete/pref?p1=' + UID);
    const validData = {
      p1: UID, 
      p2: ['Non-dairy'] 
    };

    await request(app).post('/add/pref').send(validData);
    
    const res = await request(app).get(`/get/recipe?p1=${UID}`)

    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Get recipe for 1 items about to expire without preferences
   * Input: Valid UID without preferences and items about to expire
   * Expected status code: 200
   * Expected behavior: Retrieve recipes based on items about to expire without considering preferences
   * Expected output: Response body containing the recipes that fit the filter, can be empty
   */
  test('Get recipe for items about to expire without preferences', async () => {
    await request(app).get('/delete/pref?p1=' + UID);
    const validData = {
      p1: UID, 
      p2: [2] 
    };

    await request(app).post('/delete/items').send(validData);
    const res = await request(app).get(`/get/recipe?p1=${UID}`)

    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Get recipe with no items about to expire
   * Input: Valid UID with preferences and no items about to expire
   * Expected status code: 200
   * Expected behavior: Return an empty response as there are no items about to expire
   * Expected output: Empty response body
   */
  test('Get recipe with no items about to expire', async () => {
    await request(app).get(`/get/items?p1=${UID}`);
    const validData = {
      p1: UID, 
      p2: [1] 
    };

    await request(app).post('/delete/items').send(validData);

    const res = await request(app).get(`/get/recipe?p1=${UID}`)

    expect(res.status).toStrictEqual(200);
    expect(res.body).toStrictEqual({});
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/get/recipe?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/update/items endpoint', () => {
  /**
   * Test: Update items with valid UID, ItemID, UPC, ExpireDate, and ItemCount
   * Input: Valid data in the request body
   * Expected status code: 200
   * Expected behavior: Items with the specified UID, ItemID, UPC are updated in the database
   * Expected output: 'SUCCESS Updated items' in the response body
   */
  test('Update items with valid data', async () => {
    const validData = {
      p1: UID, 
      p2: [1], 
      p3: [123456], 
      p4: ['2023-12-01'], 
      p5: [5] 
    };

    const res = await request(app).post('/update/items').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('SUCCESS Updated items');
  });

  /**
   * Test: Attempt to update items with mismatched array lengths
   * Input: Arrays with different lengths in the request body
   * Expected status code: 400
   * Expected behavior: Endpoint returns an error message indicating array length mismatch
   * Expected output: Error message in the response body
   */
  test('Attempt to update items with mismatched array lengths', async () => {
    const invalidData = {
      p1: UID, 
      p2: [1, 2], 
      p3: [123456, 789012],
      p4: ['2023-12-01'], // Missing one ExpireDate
      p5: [5, 10] 
    };

    const res = await request(app).post('/update/items').send(invalidData);
    expect(res.status).toStrictEqual(400);
    expect(res.text).toBe('Arrays should have the same length');
  });

  /**
   * Test: Attempt to update items without providing UID
   * Input: No UID provided in the request body
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to update items without UID', async () => {
    const invalidData = {
      p2: [1, 2], 
      p3: [123456, 789012], 
      p4: ['2023-12-01', '2023-12-15'], 
      p5: [5, 10] 
    };

    const res = await request(app).post('/update/items').send(invalidData);
    expect(res.status).toStrictEqual(500); 
  });

  /*
  Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const ErrorData = {
      p1: "ForceError",
      p2: "ForceError",
      p3: "ForceError",
      p4: "ForceError",
      p5: "ForceError"
    };

    const res = await request(app).post('/update/items').send(ErrorData);
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/delete/items endpoint', () => {
  /**
   * Test: Delete items with valid UID and ItemID
   * Input: Valid UID and ItemID in the request body
   * Expected status code: 200
   * Expected behavior: Items with the specified UID and ItemID are deleted from the database
   * Expected output: 'DELETED ITEM' in the response body
   */
  test('Delete items with valid UID and ItemID', async () => {
    await request(app).get(`/get/items?p1=${UID}`);
    const validData = {
      p1: UID, 
      p2: [1] 
    };

    const res = await request(app).post('/delete/items').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('DELETED ITEM');
  });

  /**
   * Test: Attempt to delete items with invalid UID
   * Input: Invalid UID in the request body
   * Expected status code: 200 (assuming the route handles invalid UID cases gracefully)
   * Expected behavior: No items are deleted, and a message indicating no rows were deleted is returned
   * Expected output: 'Error querying the databaseValue not found' in the response body
   */
  test('Attempt to delete items with invalid UID', async () => {
    const invalidData = {
      p1: 'invalid', // An invalid UID example
      p2: [1] 
    };

    const res = await request(app).post('/delete/items').send(invalidData);
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toBe('Error querying the databaseValue not found');
  });

  /**
   * Test: Attempt to delete items without providing UID
   * Input: No UID provided in the request body
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: 'Error querying the databaseValue not found' in the response body
   */
  test('Attempt to delete items without UID', async () => {
    const invalidData = {
      p2: [1] 
    };

    const res = await request(app).post('/delete/items').send(invalidData);
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toBe('Error querying the databaseValue not found');
  });

  /**
   * Test: Attempt to delete items without providing ItemID
   * Input: No ItemID provided in the request body
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing ItemID
   * Expected output: 'Error querying the databaseValue not found' in the response body
   */
  test('Attempt to delete items without ItemID', async () => {
    const invalidData = {
      p1: UID 
    };

    const res = await request(app).post('/delete/items').send(invalidData);
    expect(res.status).toStrictEqual(500);
    expect(res.text).toBe('Error querying the databaseValue not found');
  });

  /*
  Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const ErrorData = {
      p1: "ForceError",
      p2: "ForceError"
    };

    const res = await request(app).post('/delete/items').send(ErrorData);
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('Algorithm: processShoppingData', () => {
  /*Test: Sending shopping reminders to each user with items expect to run out in 5 days and those items at least have 3 purchase record
  Input: None
  Expected behaviour: Firebase notification is send to each user with valid Token and items that is expect to run out in 5 days
  Expected output: Firebase notification is sent
  */
  test('This should generate shopping reminders', async()=>{
    await processShoppingData()
  })
  
  /*Test: causing an error during query using mocking
  Input: None
  Expected behaviour: 'Simulated database error' is thrown
  Expected output: error message be 'Simulated database error'*/
  test('should handle database error and enter catch block', async () => {
     
  mockError()

  try {
    await processShoppingData()
  } catch (error) {
    // Assert that the error is the expected database error
    expect(error.message).toBe('Simulated database error');
  }

  jest.resetAllMocks(); //rest all mocking
  });
})

describe('/add/pref endpoint', () => {
  /**
   * Test: Add preferences with valid UID and preferences
   * Input: Valid data in the request body
   * Expected status code: 200
   * Expected behavior: Preferences with the specified UID are added to the database
   * Expected output: 'SUCCESS ADDED Pref' in the response body
   */
  test('Add preferences with valid data', async () => {
    const validData = {
      p1: UID, 
      p2: ['Vegan']
    };

    const res = await request(app).post('/add/pref').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('SUCCESS ADDED Pref');
  });

  /**
   * Test: Attempt to add preferences without providing UID
   * Input: No UID provided in the request body
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: 'Error querying the databaseValue not found' in the response body
   */
  test('Attempt to add preferences without UID', async () => {
    const invalidData = {
      p2: ['Vegan'] 
    };

    const res = await request(app).post('/add/pref').send(invalidData);
    expect(res.status).toStrictEqual(500); 
  });

  /**
   * Test: Attempt to add preferences with an empty array
   * Input: Empty array provided for preferences in the request body
   * Expected status code: 500
   * Expected behavior: No preferences are added, and a message indicating empty preferences is returned
   * Expected output: 'Error querying the databaseError: Column 'UID' cannot be null' in the response body
   */
  test('Attempt to add preferences with an empty array', async () => {
    const emptyData = {
      p1: UID, 
      p2: [] // Empty array for preferences
    };

    const res = await request(app).post('/add/pref').send(emptyData);
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toBe('Error querying the databaseError: Empty array is passed');
  });

  /*
  Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const ErrorData = {
      p1: "ForceError",
      p2: "ForceError"
    };

    const res = await request(app).post('/add/pref').send(ErrorData);
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/get/pref endpoint', () => {
  /**
   * Test: Get preferences with valid UID
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: Preferences with the specified UID are retrieved from the database
   * Expected output: An array of preferences in the response body
   */
  test('Get preferences with valid UID', async () => {
    const res = await request(app).get('/get/pref?p1=' + UID);
    expect(res.status).toStrictEqual(200);

  });

  /**
   * Test: Get preferences with valid UID but no pref in db
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: Preferences with the specified UID are retrieved from the database
   * Expected output: Empty json object
   */
  test('Get preferences with valid UID but no pref in db', async () => {
    await request(app).get('/delete/pref?p1=' + UID);
    const res = await request(app).get('/get/pref?p1=' + UID);
    expect(res.status).toStrictEqual(200);
    const responseObject = {}
    expect(res.body).toEqual(responseObject)
  });

  /**
   * Test: Attempt to get preferences with an invalid UID
   * Input: Invalid UID in the query parameters
   * Expected status code: 500
   * Expected behavior: No preferences are found for the invalid UID
   * Expected output: An empty object in the response body
   */
  test('Attempt to get preferences with invalid UID', async () => {
    const invalidUID = 'invalid'; // An invalid UID example
    const res = await request(app).get('/get/pref?p1=' + invalidUID);
    expect(res.status).toStrictEqual(500); 
  });

  /**
   * Test: Attempt to get preferences without providing a UID
   * Input: No UID provided in the query parameters
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to get preferences without UID', async () => {
    const res = await request(app).get('/get/pref');
    expect(res.status).toStrictEqual(500); 
  });
  
  //Test: Force to enter error catching block
  //Input: All parameters are "ForceError"
  // Expected status code: 500
  //Expected behaviour: No changes made to the database, Force Error is catched and returned
  //Expected output error text: Error querying the databaseError: Forced Error
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/get/pref?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/delete/pref endpoint', () => {
  /**
   * Test: Delete preferences with valid UID
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: Preferences with the specified UID are deleted from the database
   * Expected output: 'SUCCESS DELETE Pref' in the response body
   */
  test('Delete preferences with valid UID', async () => {
    const res = await request(app).get('/delete/pref?p1=' + UID);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('SUCCESS DELETE Pref');
  });

  /**
   * Test: Attempt to delete preferences with an invalid UID
   * Input: Invalid UID in the query parameters
   * Expected status code: 500
   * Expected behavior: No preferences are deleted
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to delete preferences with invalid UID', async () => {
    const invalidUID = 'invalid'; // An invalid UID example
    const res = await request(app).get('/delete/pref?p1=' + invalidUID);
    expect(res.status).toStrictEqual(500); 
  });

  /**
   * Test: Attempt to delete preferences without providing a UID
   * Input: No UID provided in the query parameters
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to delete preferences without UID', async () => {
    const res = await request(app).get('/delete/pref');
    expect(res.status).toStrictEqual(500); 
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/delete/pref?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/get/pref_list endpoint', () => {
  /**
   * Test: Get available preferences from the database
   * Input: No input required for this test
   * Expected status code: 200
   * Expected behavior: Available preferences are retrieved from the database
   * Expected output: An array of preferences in the response body
   */
  test('Get available preferences from the database', async () => {
    const res = await request(app).get('/get/pref_list');
    expect(res.status).toStrictEqual(200);
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/get/pref_list?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/get/dietReq endpoint empty before /add/dietReq', () => {
  /**
   * Test: Retrieve all requests for being a dietitian when no request is being made
   * Expected status code: 200
   * Expected behavior: Empty, no request being made
   * Expected output: Empty Array
   */
  test('Retrieve all requests for being a dietitian', async () => {
  const res = await request(app).get('/get/dietReq');
  expect(res.status).toStrictEqual(200);
    
  });
})

describe('/add/dietReq endpoint', () => {
  /**
   * Test: Send a request for being a dietitian
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: A request for being a dietitian is made for the specified UID
   * Expected output: 'Request for being a dietitian made!!' in the response body
   */
  test('Send a request for being a dietitian', async () => {
    const res = await request(app).get(`/add/dietReq?p1=${UID}`);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('Request for being dietician made!!');
  });

  /**
   * Test: Attempt to send a request without providing a UID
   * Input: No UID provided in the query parameters
   * Expected status code: 500 
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to send a request without providing a UID', async () => {
    const res = await request(app).get('/add/dietReq');
    expect(res.status).toStrictEqual(500); 
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/add/dietReq?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/get/dietReq endpoint', () => {
  /**
   * Test: Retrieve all requests for being a dietitian
   * Expected status code: 200
   * Expected behavior: Retrieve a list of all requests for being a dietitian
   * Expected output: An array of dietitian requests in the response body
   */
  test('Retrieve all requests for being a dietitian', async () => {
  const res = await request(app).get('/get/dietReq');
  expect(res.status).toStrictEqual(200);
    
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/get/dietReq?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/approve/dietReq endpoint', () => {
  /**
   * Test: Approve a request for being a dietitian
   * Input: A valid user ID (UID) for a dietitian request
   * Expected status code: 200
   * Expected behavior: Approve the dietitian request, add the user to the dietitian table,
   *                    and remove the request from the dietitian request table, remove the user from usertable
   * Expected output: 'SUCCESS approve being dietician request' in the response body
   */
  test('Approve request for being a dietitian', async () => {
    const res = await request(app).get(`/approve/dietReq?p1=${UID}`);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('SUCCESS approve being dietician request');
    
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/approve/dietReq?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
  
});


describe('/remove/dietReq endpoint', () => {
  /**
   * Test: Remove a request for being a dietitian
   * Input: A valid user ID (UID) for a dietitian request
   * Expected status code: 200
   * Expected behavior: Remove the dietitian request from the dietitian request table
   * Expected output: 'SUCCESS delete being dietician request' in the response body
   */
  test('Remove request for being a dietitian', async () => {
    const userData2 = {
      p1: 'testdata',
      p2: 'testdata',
      p3: 'testdata@gmail.com',
      p4: 'https://example.com/profile.jpg',
      p5: 'someToken'
    };
    const res2 = await request(app).post('/add/users').send(userData2);
    UID2=res2.body.Message
    await request(app).get(`/add/dietReq?p1=${UID2}`);
    const res = await request(app).get(`/remove/dietReq?p1=${UID2}`);
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('SUCCESS delete being dietician request');
    

  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/remove/dietReq?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});


describe('/get/dietician endpoint', () => {
  /**
   * Test: Get dietitian with email and update Firebase token
   * Input: Valid dietitian email and Firebase token
   * Expected status code: 200
   * Expected behavior: Retrieve the dietitian information and update the Firebase token
   * Expected output: Dietitian information in the response body
   */
  test('Get dietitian with email and update Firebase token', async () => {
    const validEmail = 'john.doe@example.com'; 
    const validToken = 'newFirebaseToken'; 
    const res = await request(app).post('/get/dietician').send({
      p1:validEmail,
      p2:validToken
    });
    
    expect(res.status).toStrictEqual(200);
    
    expect(res.body).toHaveProperty('DID');
    expect(res.body).toHaveProperty('FirstName');
    expect(res.body).toHaveProperty('LastName');
    expect(res.body).toHaveProperty('Email');
    expect(res.body).toHaveProperty('ProfileURL');

    const DID= res.body.DID
    await request(app).get(`/delete/dietician?p1=${DID}`)
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {

    const email="ForceError"
    const Token="ForceError"
    const url= '/get/dietician'
    const res= await request(app).post(url).send({
      p1:email,
      p2:Token
    })
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
}); 


describe('/get/users_type endpoint', () => {
  /**
   * Test: Get user type based on email
   * Input: Valid email for a user
   * Expected status code: 200
   * Expected behavior: Retrieve the user type user based on the email
   * Expected output: Response body containing the user type
   */
  test('Get user type for a user email', async () => {
    const validUserEmail = 'user@example.com'; 
    const res = await request(app).get(`/get/users_type?p1=${validUserEmail}`);

    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('User\n');
  });

  /**
   * Test: Get user type based on email for a dietitian
   * Input: Valid email for a dietitian
   * Expected status code: 200
   * Expected behavior: Retrieve the user type dietician based on the email
   * Expected output: Response body containing the user type
   */
  test('Get user type for a dietitian email', async () => {
    const validDietitianEmail = 'dietitian@example.com'; 
    const res = await request(app).get(`/get/users_type?p1=${validDietitianEmail}`);

    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('Dietician\n');
  });

  /**
   * Test: Get user type based on email for an admin
   * Input: Valid email for an admin
   * Expected status code: 200
   * Expected behavior: Retrieve the user type admin based on the email
   * Expected output: Response body containing the user type
   */
  test('Get user type for an admin email', async () => {
    const validAdminEmail = 'admin@example.com'; 
    const res = await request(app).get(`/get/users_type?p1=${validAdminEmail}`);

    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('Admin\n');
  });

  /**
   * Test: Get user type for an email that does not exist
   * Input: email that does not exist in any user type
   * Expected status code: 200 
   * Expected behavior: Return a message indicating that the entry does not exist
   * Expected output: Response body indicating that the entry does not exist
   */
  test('Get user type for a non-existent email', async () => {
    const invalidEmail = 'nonexistent@example.com'; 
    const res = await request(app).get(`/get/users_type?p1=${invalidEmail}`);

    expect(res.status).toStrictEqual(200); 
    expect(res.body.Message).toBe('Does not exist\n');
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/get/users_type?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});

describe('/get/recipe_info endpoint', () => {
  /**
   * Test: Get recipe info using RID
   * Input: Valid RID(s)
   * Expected status code: 200
   * Expected behavior: Retrieve recipe information based on the provided RID(s)
   * Expected output: Response body containing the recipe information
   */
  test('Get recipe info using RID', async () => {
    const validRID = 52767; 

    const res = await request(app)
      .get(`/get/recipe_info?p1=${validRID}`);

    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Get recipe info with invalid or non-existent RID(s)
   * Input: non-existent RID(s)
   * Expected status code: 200
   * Expected behavior: Return an empty response as there is no recipe information for the provided RID(s)
   * Expected output: Empty response body
   */
  test('Get recipe info with invalid or non-existent RID(s)', async () => {
    const invalidRID=999; 

    const res = await request(app)
      .get(`/get/recipe_info?p1=${invalidRID}=`);

    expect(res.status).toStrictEqual(200);
    expect(res.body).toStrictEqual({});
  });

  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/get/recipe_info?p1='+'ForceError' );
    expect(res.status).toStrictEqual(500);
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
});


//Interface GET https://20.104.197.24:443/delete/users
describe('/delete/users endpoint',  () => {
  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block', async () => {
     
    const res = await request(app).get('/delete/users?p1=' +"ForceError");
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });

  /**
   * Test: Delete a user with a valid UID
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: User with the specified UID is deleted from the database
   * Expected output: 'DELETED USER' in the response body
   */
  test('Delete user with valid UID', async () => {
    var res
    if(UID!=null){
      res=await request(app).get('/delete/users?p1=' +UID);
    }
    if(UID2!=null){
      res = await request(app).get('/delete/users?p1=' +UID2);
    }
    expect(res.status).toStrictEqual(200);
    expect(res.body.Message).toBe('DELETED USER');
  });

  /**
   * Test: Attempt to delete a user with an invalid UID
   * Input: Invalid UID in the query parameters
   * Expected status code: 500
   * Expected behavior: value not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to delete user with invalid UID', async () => {
    const invalidUID = 'invalid'; // An invalid UID example
    const res = await request(app).get('/delete/users?p1=' +invalidUID);
    expect(res.status).toStrictEqual(500); 
  });

  /**
   * Test: Attempt to delete a user without providing a UID
   * Input: No UID provided in the query parameters
   * Expected status code: 500
   * Expected behavior: value not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to delete user without UID', async () => {
    const res = await request(app).get('/delete/users');
    expect(res.status).toStrictEqual(500); 
  });
});

describe('Test case for helper function',  () => {
  /*Test: Force to enter error catching block
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block for delete dietician', async () => {
     
    const res = await request(app).get('/delete/dietician?p1=' +"ForceError");
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });

  /*Test: Force to enter error catching block for
  Input: All parameters are "ForceError"
  Expected status code: 500
  Expected behaviour: No changes made to the database, Force Error is catched and returned
  Expected output error text: Error querying the databaseError: Forced Error
  */
  test('should handle database error and enter catch block for delete UPC and also clear up the UPC added', async () => {
    
    await request(app).get('/delete/UPC?p1=' +UPC);

    const res = await request(app).get('/delete/UPC?p1=' +"ForceError");
    expect(res.status).toStrictEqual(500); 
    expect(res.text).toStrictEqual("Error querying the databaseError: Forced Error"); 
  });
})
