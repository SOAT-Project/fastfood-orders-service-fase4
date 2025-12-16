ALTER TABLE orders
DROP CONSTRAINT check_order_status;

ALTER TABLE orders
ADD CONSTRAINT check_order_status
CHECK (
    status IN (
        'PENDING',
        'RECEIVED',
        'PREPARING',
        'READY',
        'COMPLETED'
    )
);
