-- MySQL dump 10.16  Distrib 10.1.13-MariaDB, for osx10.6 (i386)
--
-- Host: localhost    Database: supermarket
-- ------------------------------------------------------
-- Server version	10.1.13-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `cust_id` int(11) NOT NULL AUTO_INCREMENT,
  `cust_name` varchar(200) DEFAULT NULL,
  `cust_balance` decimal(10,0) DEFAULT '0',
  `cust_points` int(11) DEFAULT '0',
  `cust_username` varchar(50) DEFAULT NULL,
  `cust_password` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`cust_id`),
  UNIQUE KEY `customer_cust_username_uindex` (`cust_username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Kassem Bagher',4130,242,'kassem','kassem123'),(2,'Khalid Dowaid',12,90,'khalid','khalid123'),(3,'Rohan',1000,0,'rohan','rohan123'),(4,'Tim',50,50,'tim','tim');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_order`
--

DROP TABLE IF EXISTS `customer_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `customer` int(11) DEFAULT NULL,
  `order_date` mediumtext,
  `order_subtotal` decimal(10,0) DEFAULT '0',
  `order_discount` double DEFAULT '0',
  `order_total` decimal(10,0) DEFAULT '0',
  `order_status` varchar(20) DEFAULT NULL,
  `order_points` int(11) DEFAULT '0',
  `bonus_points` int(11) DEFAULT '0',
  PRIMARY KEY (`order_id`),
  KEY `customer_order_customer_cust_id_fk` (`customer`),
  CONSTRAINT `customer_order_customer_cust_id_fk` FOREIGN KEY (`customer`) REFERENCES `customer` (`cust_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_order`
--

LOCK TABLES `customer_order` WRITE;
/*!40000 ALTER TABLE `customer_order` DISABLE KEYS */;
INSERT INTO `customer_order` VALUES (31,1,'1492586021',379,12,367,'canceled',240,0),(32,1,'1492586494',450,12,438,'canceled',240,45),(33,1,NULL,0,0,0,'pending',0,0);
/*!40000 ALTER TABLE `customer_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discount`
--

DROP TABLE IF EXISTS `discount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `discount` (
  `disc_quantity` int(11) NOT NULL,
  `product` int(11) NOT NULL,
  `disc_percentage` int(11) DEFAULT NULL,
  PRIMARY KEY (`disc_quantity`,`product`),
  KEY `discount_product_id_fk` (`product`),
  CONSTRAINT `discount_product_id_fk` FOREIGN KEY (`product`) REFERENCES `product` (`prod_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discount`
--

LOCK TABLES `discount` WRITE;
/*!40000 ALTER TABLE `discount` DISABLE KEYS */;
INSERT INTO `discount` VALUES (5,1,50),(10,2,16),(20,2,20);
/*!40000 ALTER TABLE `discount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employee` (
  `emp_id` int(11) NOT NULL AUTO_INCREMENT,
  `emp_name` varchar(200) DEFAULT NULL,
  `emp_password` varchar(256) DEFAULT NULL,
  `emp_username` varchar(50) DEFAULT NULL,
  `emp_role` varchar(20) DEFAULT NULL COMMENT 'manager\nsales\nwarehouse',
  PRIMARY KEY (`emp_id`),
  UNIQUE KEY `employee_emp_username_uindex` (`emp_username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'system','BXEexrvzcQgQAXErZVTzsH2cf7wDeM3biHpPBpBwzdfhxZpq7jGaNiaGhCyBqLjk','system','system'),(2,'Kim S','kim','kim','sales'),(3,'Frank W','frank','frank','warehouse'),(5,'Tim M','tim','tim','manager');
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_item` (
  `cust_order` int(11) DEFAULT NULL,
  `product` int(11) DEFAULT NULL,
  `item_quantity` int(11) DEFAULT NULL,
  `item_discount` decimal(10,0) DEFAULT '0',
  `item_total` int(11) DEFAULT NULL,
  KEY `order_item_order_id_fk` (`cust_order`),
  KEY `order_item_product_prod_id_fk` (`product`),
  CONSTRAINT `order_item_order_id_fk` FOREIGN KEY (`cust_order`) REFERENCES `customer_order` (`order_id`),
  CONSTRAINT `order_item_product_prod_id_fk` FOREIGN KEY (`product`) REFERENCES `product` (`prod_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (31,5,129,0,129),(31,1,5,0,250),(32,1,7,0,450),(33,1,5,0,250);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `prod_id` int(11) NOT NULL AUTO_INCREMENT,
  `prod_name` varchar(200) DEFAULT NULL,
  `prod_unit_price` decimal(10,0) DEFAULT '0',
  `prod_stock_level` int(11) DEFAULT NULL,
  `prod_replenish_level` int(11) DEFAULT NULL,
  `prod_type` varchar(3) DEFAULT 'pcs',
  `supplier` int(11) DEFAULT NULL,
  PRIMARY KEY (`prod_id`),
  KEY `product_supplier_id_fk` (`supplier`),
  CONSTRAINT `product_supplier_id_fk` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`sup_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Banana',100,74,50,'kg',1),(2,'Apple',3,170,8,'kg',1),(3,'Tomato Paste',2,70,20,'pcs',2),(4,'Asian Noodles',1,98,30,'pcs',2),(5,'Baked Beans',1,169,40,'pcs',1),(6,'Lamb Leg Bone-in',9,192,30,'kg',1),(7,'Greek Style Yogurt',4,40,10,'pcs',2),(8,'Just Organic Coconut',4,219,35,'pcs',2);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_order`
--

DROP TABLE IF EXISTS `purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `purchase_order` (
  `prd__id` int(11) NOT NULL AUTO_INCREMENT,
  `prd_date` mediumtext,
  `employee` int(11) DEFAULT NULL,
  `supplier` int(11) DEFAULT NULL,
  PRIMARY KEY (`prd__id`),
  KEY `purchase_order_employee_id_fk` (`employee`),
  KEY `purchase_order_supplier_id_fk` (`supplier`),
  CONSTRAINT `purchase_order_employee_id_fk` FOREIGN KEY (`employee`) REFERENCES `employee` (`emp_id`),
  CONSTRAINT `purchase_order_supplier_id_fk` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`sup_id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order`
--

LOCK TABLES `purchase_order` WRITE;
/*!40000 ALTER TABLE `purchase_order` DISABLE KEYS */;
INSERT INTO `purchase_order` VALUES (49,'1492570120',1,1),(60,'1492572114',1,1),(71,'1492573105',1,1),(72,'1492575465',1,1),(73,'1492577621',1,1),(74,'1492586021',1,1);
/*!40000 ALTER TABLE `purchase_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_order_item`
--

DROP TABLE IF EXISTS `purchase_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `purchase_order_item` (
  `purchase_order` int(11) DEFAULT NULL,
  `product` int(11) DEFAULT NULL,
  `item_quantity` int(11) DEFAULT NULL,
  `item_total` decimal(10,0) DEFAULT '0',
  KEY `purchase_order_item_purchase_order_prd__id_fk` (`purchase_order`),
  CONSTRAINT `purchase_order_item_purchase_order_prd__id_fk` FOREIGN KEY (`purchase_order`) REFERENCES `purchase_order` (`prd__id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order_item`
--

LOCK TABLES `purchase_order_item` WRITE;
/*!40000 ALTER TABLE `purchase_order_item` DISABLE KEYS */;
INSERT INTO `purchase_order_item` VALUES (49,1,20,40),(49,3,2,4),(60,1,20,40),(60,3,2,4),(71,1,10,1000),(72,1,50,5000),(73,1,20,2000),(73,3,2,4),(74,5,40,40);
/*!40000 ALTER TABLE `purchase_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier` (
  `sup_id` int(11) NOT NULL AUTO_INCREMENT,
  `sup_name` varchar(100) DEFAULT NULL,
  `sup_address` varchar(200) DEFAULT NULL,
  `sup_postcode` int(11) DEFAULT NULL,
  `sup_phone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`sup_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` VALUES (1,'Alde','280-282 Bai Rd, Sheltenham VIK',3192,'0288851111'),(2,'WoodWorth','1 WoodWorth Wai \nBella Vista VIK',3033,'0288850001');
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-20  9:49:59
