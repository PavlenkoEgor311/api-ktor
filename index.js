const functions = require('firebase-functions');
const { createServer } = require('http');
const { default: app } = require('./build/server/server');

exports.api = functions.https.onRequest((req, res) => {
    createServer(app).listen(8080, () => {
        console.log('Server listening on port 8080');
    });
});