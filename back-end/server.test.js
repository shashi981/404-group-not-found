const request = require('supertest');
const app = require('./server'); // Adjust the path as necessary

describe('/get/messageToken endpoint', () => {
  /**
   * Test: Retrieve message token with valid DID
   * Input: A valid dietitian ID
   * Expected status code: 200
   * Expected behavior: Retrieve a message token corresponding to the given DID
   * Expected output: A message token in the response body
   */
  test('Retrieve message token with valid DID', async () => {
    const validDID = '1'; // Replace with an actual valid DID
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
