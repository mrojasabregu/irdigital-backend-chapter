const express = require('express');
const sleep = require('./sleep');
const router = express.Router();
const latencies = [100,150,200,300,400,1100,2000,4100,1000,200,500,300,400,700,5000,5100];

async function responseWithRandomLatency() {
    const randomLatency = latencies[Math.floor(Math.random() * latencies.length)];
    const status = 200;
    const body = { message: 'OK'};
    await sleep(randomLatency);
    return { status, body };
}

router.route('/messaging').post(async (req, res, next) => {
    const { status, body} = await responseWithRandomLatency();
    return res.status(status).send(body);
});

module.exports  = router;