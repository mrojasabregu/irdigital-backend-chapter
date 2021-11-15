const express = require('express');
const sleep = require('./sleep');
const router = express.Router();
const latencies = [100,150,200,300,400,1100,2000,4100,1000,200,500,300,400,700,5000,5100];

const tpsa = require('./db/oechsle.json');
const hpsa = require('./db/promart.json');
const pvea = require('./db/plazavea.json');
const database = {
    tpsa,
    hpsa,
    pvea,
};

async function responseWithRandomLatency(tenantId, orderId) {
    const randomLatency = latencies[Math.floor(Math.random() * latencies.length)];
    const isError = Math.floor(Math.random() * 10) + 1;
    let randomStatus = (isError > 1) ? 0 : Math.floor(Math.random() * 6) + 1;
    const data = database[tenantId];
    let order = null;
    console.log(isError);
    console.log(randomStatus);
    if ( data != null ) {
        order = data.filter(it => it.orderId == orderId)[0];
        if(order == null) {
            randomStatus = 4;//not found
        }
    } else {
        randomStatus = 4;//not found
    }
 
    const statuses = [
        {status: 200, body: order },
        {status: 500, body: { error: 'INTERNAL_SERVER_ERROR VITEX'} },
        {status: 504, body: { error: 'GATEWAY_TIMEOUT VITEX'} },
        {status: 400, body: { error:  'Forbidden VITEX'} },
        {status: 404, body: { error:  `VITEX order ${orderId} not found`} },
        {status: 401, body: { error:  'UNAUTHORIZED VITEX'} },
        {status: 403, body: { error:  'FORBIDDEN VITEX'} },
    ];
    const { status, body } = statuses[randomStatus];
    await sleep(randomLatency);
    return { status, body };
}

router.route('/:tenantId/oms/pvt/orders/:orderId').get(async (req, res, next) => {
    const { tenantId, orderId } = req.params;
    const { status, body} = await responseWithRandomLatency(tenantId, orderId);
    return res.status(status).send(body);
});

module.exports  = router;