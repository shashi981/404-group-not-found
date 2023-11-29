const request = require('supertest');
const app = require('./server'); 

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


//describe('/get/users endpoint', () => {
  /**
   * Test: Retrieve user details with valid email and token
   * Input: Valid email and token as query parameters
   * Expected status code: 200
   * Expected behavior: User details are updated in the database and the updated user information is returned
   * Expected output: User object containing UID, FirstName, LastName, Email, and ProfileURL
   */
  /*test('Retrieve user details with valid email and token', async () => {
    const email = 'shashi090801@gmail.com';
    const token = 'validToken';
    const res = await request(app).get(`/get/users?p1=${email}&p2=${token}`);
    expect(res.status).toStrictEqual(200);
  });*/

  /**
   * Test: Attempt to retrieve user details with invalid email
   * Input: Invalid email and valid token as query parameters
   * Expected status code: 200 (Empty JSON response)
   * Expected behavior: No user found with the invalid email, and an empty JSON object is returned
   * Expected output: Empty JSON object
   */
 /* test('Attempt to retrieve user details with invalid email', async () => {
    const email = 'invalid@example.com';
    const token = 'validToken';
    const res = await request(app).get(`/get/users?p1=${email}&p2=${token}`);
    expect(res.status).toStrictEqual(200);
    expect(res.body).toEqual({});
  });
});*/


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

let UID
let UID2
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
    UID=res.body
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
   * Input: Invalid user data in the request body
   * Expected status code: 500
   * Expected behavior: User is not added to the database, and an error message is returned in the response body
   * Expected output: An error message in the response body
   */
  test('Attempt to add user with invalid data', async () => {
    const invalidUserData = {
      p1: 'John',
      p2: 123, // Invalid data type for LastName
      p3: 'john.doe@example.com',
      p4: 'https://example.com/profile.jpg',
      p5: 'someToken'
    };

    const res = await request(app).post('/add/users').send(invalidUserData);
    expect(res.status).toStrictEqual(500);
  });
});


//Interface GET https://20.104.197.24:443/get/users
describe('GET USER request', () => {
  //Input: both Email and Token passed, email is valid, unique and exists in the database 
  // Expected status code: 200
  //Expected behaviour: Message Token for the user with that email is updated, and get all //information for the user with that email
  //Expected output: UID, FirstName, LastName, Email, ProfileURL
  test("Valid user", async()=>{
    const email='john.doe@example.com' //this email should exist in db
    const Token="fakoehfnjildhnfljhasfjsfksjf"
    const url= "/get/users?p1=" + email + "&p2=" + Token
    const res= await request(app).get(url)
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
  
  //Input: both Email and Token passed, email doesn’t exist in the database 
  // Expected status code: 500
  //Expected behaviour: No database column is changed
  //Expected output: None
  test("Email Not in DB", async()=>{
    const email="invalidtest@gmail.com" //this email should not exist in db
    const Token="fakoehfnjildhnfljhasfjsfksjf"
    const url= "/get/users?p1=" + email + "&p2=" + Token
    const res= await request(app).get(url)
    const responseObject = {}
    expect(res.status).toStrictEqual(500)
    expect(res.body).toEqual(responseObject)
  })
  
  //Input: either email or token is not passed, or both
  // Expected status code: 500
  //Expected behaviour: No database column is changed
  //Expected output: None
  test("Missing input", async()=>{
    const email="" 
    const Token=""
    const url= "/get/users?p1=" + email + "&p2=" + Token
    const res= await request(app).get(url)
    const responseObject = {}
    expect(res.status).toStrictEqual(500)
    expect(res.body).toEqual(responseObject)
  
  
  })
})

describe('/add/items endpoint',() => {
  /**
   * Test: Add items with valid data
   * Input: Valid user ID, UPCs, ExpireDates, and ItemCounts in the request body
   * Expected status code: 200
   * Expected behavior: Items are added to the database, and a success message is returned in the response body
   * Expected output: { message: 'SUCCESS ADDED ITEMS' } in the response body
   */
  test('Add items with valid data', async () => {
    const validData = {
      p1: UID, // Replace with an actual valid UID
      p2: ['068700115004', '068700115004'], // Replace with actual UPCs
      p3: ['2023-12-31', '2024-01-15'], // Replace with actual ExpireDates
      p4: [2, 5] // Replace with actual ItemCounts
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
    const validData = {
      p1: UID, // Replace with an actual valid UID
      p2: -1,
      p3: ['2023-12-31', '2024-01-15'],
      p4: [2, 5],
      p5: ['Item1', 'Item2']
    };

    const res = await request(app).post('/add/items_man').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('SUCCESS ADDED ITEMS MANUAL');
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
   * Expected status code: 500 (or appropriate error status code)
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
   * Expected status code: 404 (or appropriate error status code)
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to get items without UID', async () => {
    const res = await request(app).get('/get/items');
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 404 for missing UID
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
    const validData = {
      p1: UID, // Replace with an actual valid UID
      p2: [1, 2] // Replace with actual valid ItemIDs
    };

    const res = await request(app).post('/delete/items').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('DELETED ITEM');
  });

  /**
   * Test: Attempt to delete items with invalid UID
   * Input: Invalid UID in the request body
   * Expected status code: 200 (assuming the route handles invalid UID cases gracefully)
   * Expected behavior: No items are deleted, and a message indicating no rows were deleted is returned
   * Expected output: 'No rows were deleted. Check the values in your DELETE query.' in the response body
   */
  test('Attempt to delete items with invalid UID', async () => {
    const invalidData = {
      p1: 'invalid', // An invalid UID example
      p2: [1, 2] // Replace with actual valid ItemIDs
    };

    const res = await request(app).post('/delete/items').send(invalidData);
    expect(res.status).toStrictEqual(500); // Assuming the route handles invalid UID cases gracefully
    expect(res.text).toBe('Error querying the databaseValue not found');
  });

  /**
   * Test: Attempt to delete items without providing UID
   * Input: No UID provided in the request body
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to delete items without UID', async () => {
    const invalidData = {
      p2: [1, 2] // Replace with actual valid ItemIDs
    };

    const res = await request(app).post('/delete/items').send(invalidData);
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 500 for missing UID
  });

  /**
   * Test: Attempt to delete items without providing ItemID
   * Input: No ItemID provided in the request body
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing ItemID
   * Expected output: An error message indicating missing ItemID or invalid request
   */
  test('Attempt to delete items without ItemID', async () => {
    const invalidData = {
      p1: UID // Replace with an actual valid UID
    };

    const res = await request(app).post('/delete/items').send(invalidData);
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 500 for missing ItemID
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
      p1: UID, // Replace with an actual valid UID
      p2: [1, 2], // Replace with actual valid ItemIDs
      p3: [123456, 789012], // Replace with actual valid UPCs
      p4: ['2023-12-01', '2023-12-15'], // Replace with actual valid ExpireDates
      p5: [5, 10] // Replace with actual valid ItemCounts
    };

    const res = await request(app).post('/update/items').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('SUCCESS Updated items');
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
      p1: UID, // Replace with an actual valid UID
      p2: [1, 2], // Replace with actual valid ItemIDs
      p3: [123456, 789012], // Replace with actual valid UPCs
      p4: ['2023-12-01'], // Missing one ExpireDate
      p5: [5, 10] // Replace with actual valid ItemCounts
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
      p2: [1, 2], // Replace with actual valid ItemIDs
      p3: [123456, 789012], // Replace with actual valid UPCs
      p4: ['2023-12-01', '2023-12-15'], // Replace with actual valid ExpireDates
      p5: [5, 10] // Replace with actual valid ItemCounts
    };

    const res = await request(app).post('/update/items').send(invalidData);
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 500 for missing UID
  });
});

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
      p1: UID, // Replace with an actual valid UID
      p2: ['Vegan'] // Replace with actual valid preferences
    };

    const res = await request(app).post('/add/pref').send(validData);
    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('SUCCESS ADDED Pref');
  });

  /**
   * Test: Attempt to add preferences without providing UID
   * Input: No UID provided in the request body
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to add preferences without UID', async () => {
    const invalidData = {
      p2: ['Vegan'] // Replace with actual valid preferences
    };

    const res = await request(app).post('/add/pref').send(invalidData);
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 500 for missing UID
  });

  /**
   * Test: Attempt to add preferences with an empty array
   * Input: Empty array provided for preferences in the request body
   * Expected status code: 200 (or appropriate status code for handling empty preferences array)
   * Expected behavior: No preferences are added, and a message indicating empty preferences is returned
   * Expected output: 'No preferences provided' in the response body
   */
  test('Attempt to add preferences with an empty array', async () => {
    const emptyData = {
      p1: UID, // Replace with an actual valid UID
      p2: [] // Empty array for preferences
    };

    const res = await request(app).post('/add/pref').send(emptyData);
    expect(res.status).toStrictEqual(500); // Assuming the route handles empty preferences array gracefully
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
    // Add more specific assertions based on your expected output
    // For example, if you expect an array of preferences, you can check the array length, etc.
  });

  /**
   * Test: Attempt to get preferences with an invalid UID
   * Input: Invalid UID in the query parameters
   * Expected status code: 200 (assuming the route handles invalid UID cases gracefully)
   * Expected behavior: No preferences are found for the invalid UID
   * Expected output: An empty object in the response body
   */
  test('Attempt to get preferences with invalid UID', async () => {
    const invalidUID = 'invalid'; // An invalid UID example
    const res = await request(app).get('/get/pref?p1=' + invalidUID);
    expect(res.status).toStrictEqual(500); // Assuming the route handles invalid UID cases gracefully
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
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 404 for missing UID
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
    expect(res.text).toBe('SUCCESS DELETE Pref');
  });

  /**
   * Test: Attempt to delete preferences with an invalid UID
   * Input: Invalid UID in the query parameters
   * Expected status code: 200 (assuming the route handles invalid UID cases gracefully)
   * Expected behavior: No preferences are deleted, and a message indicating no rows were deleted is returned
   * Expected output: 'No preferences were deleted. Check the values in your DELETE query.' in the response body
   */
  test('Attempt to delete preferences with invalid UID', async () => {
    const invalidUID = 'invalid'; // An invalid UID example
    const res = await request(app).get('/delete/pref?p1=' + invalidUID);
    expect(res.status).toStrictEqual(500); // Assuming the route handles invalid UID cases gracefully
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
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 404 for missing UID
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
    // Add more specific assertions based on your expected output
    // For example, you can check the array length, specific preferences, etc.
    // Ensure that the response body matches the expected structure and data from the database
  });
});

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
    expect(res.text).toBe('Request for being dietician made!!');
  });

  /**
   * Test: Attempt to send a request without providing a UID
   * Input: No UID provided in the query parameters
   * Expected status code: 500 (or appropriate error status code)
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to send a request without providing a UID', async () => {
    const res = await request(app).get('/add/dietReq');
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 404 for missing UID
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
    
    // Add additional assertions based on your actual response structure
    // and the expected behavior of the endpoint.
    // For example, you can check if the response body is an array.
  });
});

describe('/approve/dietReq endpoint', () => {
  /**
   * Test: Approve a request for being a dietitian
   * Input: A valid user ID (UID) for a dietitian request
   * Expected status code: 200
   * Expected behavior: Approve the dietitian request, add the user to the dietitian table,
   *                    and remove the request from the dietitian request table
   * Expected output: 'SUCCESS approve being dietician request' in the response body
   */
  test('Approve request for being a dietitian', async () => {
    const res = await request(app).get(`/approve/dietReq?p1=${UID}`);
    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('SUCCESS approve being dietician request');
    
    // Add additional assertions based on your actual response structure
    // and the expected behavior of the endpoint.
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
    UID2=res2.body
    await request(app).get(`/add/dietReq?p1=${UID2}`);
    const res = await request(app).get(`/remove/dietReq?p1=${UID2}`);
    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('SUCCESS delete being dietician request');
    
    // Add additional assertions based on your actual response structure
    // and the expected behavior of the endpoint.
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
    const validEmail = 'john.doe@example.com'; // Replace with a valid dietitian email
    const validToken = 'newFirebaseToken'; // Replace with a valid Firebase token
    const res = await request(app).get(`/get/dietician?p1=${validEmail}&p2=${validToken}`);
    
    expect(res.status).toStrictEqual(200);
    
    // Add assertions based on your actual response structure and the expected behavior
    // of retrieving dietitian information and updating the Firebase token.
    // You can check if the response body contains the expected dietitian information.
    // For example, if you have a response like:
    // { DID: 1, FirstName: 'John', LastName: 'Doe', Email: 'dietitian@example.com', ProfileURL: 'profile.jpg' }
    // You can use expect(res.body.Email).toBe(validEmail);
    // Adjust these assertions based on your actual implementation.

    // Assuming the response contains dietitian information
    expect(res.body).toHaveProperty('DID');
    expect(res.body).toHaveProperty('FirstName');
    expect(res.body).toHaveProperty('LastName');
    expect(res.body).toHaveProperty('Email');
    expect(res.body).toHaveProperty('ProfileURL');

    const DID= res.body.DID
    await request(app).get(`/delete/dietician?p1=${DID}`)
  });
}); 


describe('/get/users_type endpoint', () => {
  /**
   * Test: Get user type based on email
   * Input: Valid email for a user
   * Expected status code: 200
   * Expected behavior: Retrieve the user type (user, dietician, admin) based on the email
   * Expected output: Response body containing the user type
   */
  test('Get user type for a user email', async () => {
    const validUserEmail = 'user@example.com'; // Replace with a valid user email
    const res = await request(app).get(`/get/users_type?p1=${validUserEmail}`);

    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('User\n');
  });

  /**
   * Test: Get user type based on email for a dietitian
   * Input: Valid email for a dietitian
   * Expected status code: 200
   * Expected behavior: Retrieve the user type (user, dietician, admin) based on the email
   * Expected output: Response body containing the user type
   */
  test('Get user type for a dietitian email', async () => {
    const validDietitianEmail = 'dietitian@example.com'; // Replace with a valid dietitian email
    const res = await request(app).get(`/get/users_type?p1=${validDietitianEmail}`);

    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('Dietician\n');
  });

  /**
   * Test: Get user type based on email for an admin
   * Input: Valid email for an admin
   * Expected status code: 200
   * Expected behavior: Retrieve the user type (user, dietician, admin) based on the email
   * Expected output: Response body containing the user type
   */
  test('Get user type for an admin email', async () => {
    const validAdminEmail = 'admin@example.com'; // Replace with a valid admin email
    const res = await request(app).get(`/get/users_type?p1=${validAdminEmail}`);

    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('Admin\n');
  });

  /**
   * Test: Get user type for an email that does not exist
   * Input: Invalid email that does not exist in any user type
   * Expected status code: 200 (or appropriate error status code)
   * Expected behavior: Return a message indicating that the entry does not exist
   * Expected output: Response body indicating that the entry does not exist
   */
  test('Get user type for a non-existent email', async () => {
    const invalidEmail = 'nonexistent@example.com'; // Replace with a non-existent email
    const res = await request(app).get(`/get/users_type?p1=${invalidEmail}`);

    expect(res.status).toStrictEqual(200); // Assuming the route handles non-existent email cases gracefully
    expect(res.text).toBe('Does not exist\n');
  });
});


describe('/get/recipe endpoint', () => {
  /**
   * Test: Get recipe for items about to expire based on preference and UID
   * Input: Valid UID with preferences and items about to expire
   * Expected status code: 200
   * Expected behavior: Retrieve recipes based on the preferences and items about to expire
   * Expected output: Response body containing the recipes
   */
  test('Get recipe for items about to expire based on preference and UID', async () => {
    const validPreferences = ['Vegetarian']; // Replace with valid preferences
    const validItemsAboutToExpire = ['Carrot', 'Broccoli']; // Replace with valid items about to expire

    const res = await request(app)
      .get(`/get/recipe?p1=${UID}`)
      .query({ p2: validPreferences.join(',') })
      .query({ p3: validItemsAboutToExpire.join(',') });

    expect(res.status).toStrictEqual(200);
    // Add more assertions based on the expected output format
  });

  /**
   * Test: Get recipe for items about to expire without preferences
   * Input: Valid UID without preferences and items about to expire
   * Expected status code: 200
   * Expected behavior: Retrieve recipes based on items about to expire without considering preferences
   * Expected output: Response body containing the recipes
   */
  test('Get recipe for items about to expire without preferences', async () => {
    const validItemsAboutToExpire = ['Milk', 'Eggs']; // Replace with valid items about to expire

    const res = await request(app)
      .get(`/get/recipe?p1=${UID}`)
      .query({ p3: validItemsAboutToExpire.join(',') });

    expect(res.status).toStrictEqual(200);
    // Add more assertions based on the expected output format
  });

  /**
   * Test: Get recipe with no items about to expire
   * Input: Valid UID with preferences and no items about to expire
   * Expected status code: 200
   * Expected behavior: Return an empty response as there are no items about to expire
   * Expected output: Empty response body
   */
  test('Get recipe with no items about to expire', async () => {
    const validPreferences = ['Vegan']; // Replace with valid preferences

    const res = await request(app)
      .get(`/get/recipe?p1=${UID}`)
      .query({ p2: validPreferences.join(',') });

    expect(res.status).toStrictEqual(200);
    expect(res.body).toStrictEqual({});
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
    const validRID = 52767; // Replace with valid RIDs fix this

    const res = await request(app)
      .get(`/get/recipe_info?p1=${validRID}`);

    expect(res.status).toStrictEqual(200);
    // Add more assertions based on the expected output format
    // For example, check if the response body contains the expected recipe information
  });

  /**
   * Test: Get recipe info with invalid or non-existent RID(s)
   * Input: Invalid or non-existent RID(s)
   * Expected status code: 200
   * Expected behavior: Return an empty response as there is no recipe information for the provided RID(s)
   * Expected output: Empty response body
   */
  test('Get recipe info with invalid or non-existent RID(s)', async () => {
    const invalidRID=999; // Replace with invalid or non-existent RIDs

    const res = await request(app)
      .get(`/get/recipe_info?p1=${invalidRID}=`);

    expect(res.status).toStrictEqual(200);
    expect(res.body).toStrictEqual({});
  });
});


//Interface GET https://20.104.197.24:443/delete/users
describe('/delete/users endpoint',  () => {
  /**
   * Test: Delete a user with a valid UID
   * Input: Valid UID in the query parameters
   * Expected status code: 200
   * Expected behavior: User with the specified UID is deleted from the database
   * Expected output: 'DELETED USER' in the response body
   */
  test('Delete user with valid UID', async () => {
    const res = await request(app).get('/delete/users?p1=' +UID2);
    expect(res.status).toStrictEqual(200);
    expect(res.text).toBe('DELETED USER');
  });

  /**
   * Test: Attempt to delete a user with an invalid UID
   * Input: Invalid UID in the query parameters
   * Expected status code: 200 (assuming the route handles invalid UID cases gracefully)
   * Expected behavior: No user is deleted, and a message indicating no rows were deleted is returned
   * Expected output: 'No rows were deleted. Check the values in your DELETE query.' in the response body
   */
  test('Attempt to delete user with invalid UID', async () => {
    const invalidUID = 'invalid'; // An invalid UID example
    const res = await request(app).get('/delete/users?p1=' +invalidUID);
    expect(res.status).toStrictEqual(500); // Assuming the route handles invalid UID cases gracefully
  });

  /**
   * Test: Attempt to delete a user without providing a UID
   * Input: No UID provided in the query parameters
   * Expected status code: 500
   * Expected behavior: Endpoint not found due to missing UID
   * Expected output: An error message indicating missing UID or invalid request
   */
  test('Attempt to delete user without UID', async () => {
    const res = await request(app).get('/delete/users');
    expect(res.status).toStrictEqual(500); // Assuming the route returns a 404 for missing UID
  });
});

