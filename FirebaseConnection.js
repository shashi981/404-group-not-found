const admin = require('firebase-admin');

// Initialize the app with appropriate configurations
const serviceAccount = require('- ../../../../../../something.json -');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

// Define the message payload
const message = {
  notification: {
    title: 'New Message',
    body: 'Hello, this is a test notification.',
  },
  token: '-token-', // Specify the target device's FCM registration token
};

// Send the message
admin.messaging().send(message)
  .then((response) => {
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });
