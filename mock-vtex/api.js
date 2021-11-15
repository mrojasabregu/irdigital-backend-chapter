const express = require('express');

const app = express();

app.use('/api', require('./vtex'));
app.use('/irma', require('./irma'));

app.listen(3000, () => {
    console.log('Mock vtex listening');
});