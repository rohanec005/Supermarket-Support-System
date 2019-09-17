CREATE TABLE supermarket.customer
(
  cust_id       INT                           NOT NULL auto_increment
  PRIMARY KEY,
  cust_name     VARCHAR(200)                  NULL,
  cust_balance  DECIMAL(10, 2) DEFAULT '0.00' NULL,
  cust_points   INT DEFAULT '0'               NULL,
  cust_username VARCHAR(50)                   NULL,
  cust_password VARCHAR(256)                  NULL,
  CONSTRAINT customer_cust_username_uindex
  UNIQUE (cust_username)
);

CREATE TABLE supermarket.customer_order
(
  order_id       INT                           NOT NULL auto_increment
  PRIMARY KEY,
  customer       INT                           NULL,
  order_date     MEDIUMTEXT                    NULL,
  order_subtotal DECIMAL(10, 2) DEFAULT '0.00' NULL,
  order_total    DECIMAL(10, 2) DEFAULT '0.00' NULL,
  order_status   VARCHAR(20)                   NULL,
  order_discount DECIMAL(10, 2) DEFAULT '0.00' NULL,
  order_points   INT DEFAULT '0'               NULL,
  bonus_points   INT DEFAULT '0'               NULL,
  CONSTRAINT customer_order_customer_cust_id_fk
  FOREIGN KEY (customer) REFERENCES supermarket.customer (cust_id)
);

CREATE INDEX customer_order_customer_cust_id_fk
  ON customer_order (customer);

CREATE TABLE supermarket.discount
(
  disc_quantity   INT NOT NULL,
  product         INT NOT NULL,
  disc_percentage INT NULL,
  PRIMARY KEY (disc_quantity, product)
);

CREATE INDEX discount_product_id_fk
  ON discount (product);

CREATE TABLE supermarket.employee
(
  emp_id       INT          NOT NULL auto_increment
  PRIMARY KEY,
  emp_name     VARCHAR(200) NULL,
  emp_password VARCHAR(256) NULL,
  emp_username VARCHAR(50)  NULL,
  emp_role     VARCHAR(20)  NULL,
  CONSTRAINT employee_emp_username_uindex
  UNIQUE (emp_username)
);

COMMENT ON COLUMN employee.emp_role IS 'manager
sales
warehouse';

CREATE TABLE supermarket.order_item
(
  cust_order    INT                           NULL,
  product       INT                           NULL,
  item_quantity INT                           NULL,
  item_total    INT                           NULL,
  item_discount DECIMAL(10, 2) DEFAULT '0.00' NULL,
  CONSTRAINT order_item_order_id_fk
  FOREIGN KEY (cust_order) REFERENCES supermarket.customer_order (order_id)
);

CREATE INDEX order_item_order_id_fk
  ON order_item (cust_order);

CREATE INDEX order_item_product_prod_id_fk
  ON order_item (product);

CREATE TABLE supermarket.product
(
  prod_id              INT                           NOT NULL auto_increment
  PRIMARY KEY,
  prod_name            VARCHAR(200)                  NULL,
  prod_unit_price      DECIMAL(10, 2) DEFAULT '0.00' NULL,
  prod_stock_level     INT                           NULL,
  prod_replenish_level INT                           NULL,
  prod_type            VARCHAR(3) DEFAULT 'pcs'      NULL,
  supplier             INT                           NULL
);

CREATE INDEX product_supplier_id_fk
  ON product (supplier);

ALTER TABLE discount
  ADD CONSTRAINT discount_product_id_fk
FOREIGN KEY (product) REFERENCES supermarket.product (prod_id);

ALTER TABLE order_item
  ADD CONSTRAINT order_item_product_prod_id_fk
FOREIGN KEY (product) REFERENCES supermarket.product (prod_id);

CREATE TABLE supermarket.purchase_order
(
  prd_date MEDIUMTEXT NULL,
  employee INT        NULL,
  supplier INT        NULL,
  prd_id   INT        NOT NULL auto_increment
  PRIMARY KEY,
  CONSTRAINT purchase_order_employee_id_fk
  FOREIGN KEY (employee) REFERENCES supermarket.employee (emp_id)
);

CREATE INDEX purchase_order_employee_id_fk
  ON purchase_order (employee);

CREATE INDEX purchase_order_supplier_id_fk
  ON purchase_order (supplier);

CREATE TABLE supermarket.purchase_order_item
(
  purchase_order INT                           NULL,
  product        INT                           NULL,
  item_quantity  INT                           NULL,
  item_total     DECIMAL(10, 2) DEFAULT '0.00' NULL,
  CONSTRAINT purchase_order_item_purchase_order_prd__id_fk
  FOREIGN KEY (purchase_order) REFERENCES supermarket.purchase_order (prd_id),
  CONSTRAINT purchase_order_item_product_prod_id_fk
  FOREIGN KEY (product) REFERENCES supermarket.product (prod_id)
);

CREATE INDEX purchase_order_item_purchase_order_prd__id_fk
  ON purchase_order_item (purchase_order);

CREATE INDEX purchase_order_item_product_prod_id_fk
  ON purchase_order_item (product);

CREATE TABLE supermarket.supplier
(
  sup_id       INT          NOT NULL auto_increment
  PRIMARY KEY,
  sup_name     VARCHAR(100) NULL,
  sup_address  VARCHAR(200) NULL,
  sup_postcode INT          NULL,
  sup_phone    VARCHAR(15)  NULL
);

ALTER TABLE product
  ADD CONSTRAINT product_supplier_id_fk
FOREIGN KEY (supplier) REFERENCES supermarket.supplier (sup_id);

ALTER TABLE purchase_order
  ADD CONSTRAINT purchase_order_supplier_id_fk
FOREIGN KEY (supplier) REFERENCES supermarket.supplier (sup_id);

CREATE VIEW supermarket.report_best_selling AS
  SELECT
  `supermarket`.`product`.`prod_name` AS `product`,
  sum(`supermarket`.`order_item`.`item_total`) AS `sales`
FROM (`supermarket`.`order_item`
JOIN `supermarket`.`product` ON ((`supermarket`.`order_item`.`product` = `supermarket`.`product`.`prod_id`)))
GROUP BY `supermarket`.`order_item`.`product`
ORDER BY sum(`supermarket`.`order_item`.`item_total`) DESC
LIMIT 5;

CREATE VIEW supermarket.report_sales AS
  SELECT date_format(from_unixtime(`co`.`order_date`), '%d-%m-%Y') AS `new_date`,
  sum(`co`.`order_total`) AS `sales`
FROM `supermarket`.`customer_order` `co`
WHERE (`co`.`order_date` IS NOT NULL )
GROUP BY DATE_FORMAT (from_unixtime(`co`.`order_date`), '%d-%m-%Y') DESC;

CREATE VIEW supermarket.report_supply AS
  SELECT
  `supermarket`.`purchase_order`.`prd_id` AS `prd_id`,
DATE_FORMAT (from_unixtime(`supermarket`.`purchase_order`.`prd_date`), '%d-%m-%Y') AS `order_date`,
  `sp`.`sup_name` AS `sup_name`,
  `prod`.`prod_name` AS `prod_name`,
  `item`.`item_quantity` AS `item_quantity`,
  `item`.`item_total` AS `item_total`
FROM (((`supermarket`.`purchase_order`
JOIN `supermarket`.`purchase_order_item` `item`
ON ((`supermarket`.`purchase_order`.`prd_id` = `item`.`purchase_order`))) JOIN `supermarket`.`supplier` `sp`
ON ((`supermarket`.`purchase_order`.`supplier` = `sp`.`sup_id`))) JOIN `supermarket`.`product` `prod`
ON ((`item`.`product` = `prod`.`prod_id`)))
ORDER BY DATE_FORMAT (from_unixtime(`supermarket`.`purchase_order`.`prd_date`), '%d-%m-%Y') DESC;

