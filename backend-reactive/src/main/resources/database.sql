DROP TABLE orders;
CREATE TABLE orders(
    order_uuid VARCHAR(200),
    tenant_id VARCHAR(200),
    order_id VARCHAR(200),
    sequence VARCHAR(200),
    creation_date TIMESTAMP WITHOUT TIME ZONE,
    client_name VARCHAR(200),
    total_value NUMBER(19,2),
    shipping_estimated_date_max TIMESTAMP WITHOUT TIME ZONE,
    shipping_estimated_date_min TIMESTAMP WITHOUT TIME ZONE,
    authorized_date TIMESTAMP WITHOUT TIME ZONE,
    currency_code VARCHAR(200),
    hostname VARCHAR(200),
    PRIMARY KEY(order_uuid)
);

CREATE TABLE orders_log(
    log_id UUID,
    tenant_id VARCHAR(200),
    order_id VARCHAR(200),
    log_error TEXT,
    event_timestamp TIMESTAMP WITHOUT TIME ZONE,
    PRIMARY KEY(log_id)
);