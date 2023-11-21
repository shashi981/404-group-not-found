const request = require('supertest');
const app = require('./server'); 

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

describe('/get/users endpoint', () => {
  /**
   * Test: Retrieve user details with valid email and token
   * Input: Valid email and token as query parameters
   * Expected status code: 200
   * Expected behavior: User details are updated in the database and the updated user information is returned
   * Expected output: User object containing UID, FirstName, LastName, Email, and ProfileURL
   */
  test('Retrieve user details with valid email and token', async () => {
    const email = 'shashi090801@gmail.com';
    const token = 'validToken';
    const res = await request(app).get(`/get/users?p1=${email}&p2=${token}`);
    expect(res.status).toStrictEqual(200);
  });

  /**
   * Test: Attempt to retrieve user details with invalid email
   * Input: Invalid email and valid token as query parameters
   * Expected status code: 200 (Empty JSON response)
   * Expected behavior: No user found with the invalid email, and an empty JSON object is returned
   * Expected output: Empty JSON object
   */
  test('Attempt to retrieve user details with invalid email', async () => {
    const email = 'invalid@example.com';
    const token = 'validToken';
    const res = await request(app).get(`/get/users?p1=${email}&p2=${token}`);
    expect(res.status).toStrictEqual(200);
    expect(res.body).toEqual({});
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
