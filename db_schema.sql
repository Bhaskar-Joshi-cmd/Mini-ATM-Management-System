-- MySQL dump 10.13  Distrib 9.6.0, for macos26.2 (arm64)
--
-- Host: localhost    Database: mini_atm_db
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '6f4d44d4-f920-11f0-8351-83a09a23def3:1-254';

--
-- Current Database: `mini_atm_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `mini_atm_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `mini_atm_db`;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `account_number` varchar(20) NOT NULL,
  `customer_name` varchar(100) NOT NULL,
  `balance` decimal(15,2) DEFAULT '0.00',
  `account_type` varchar(20) DEFAULT NULL,
  `daily_limit` decimal(15,2) DEFAULT '2000.00',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `account_number` (`account_number`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'1234567890','Bhaskar',13400.00,'Savings',10000.00),(2,'1111111111','Nitin',0.00,'Saving',10000.00),(3,'2222222222','Rohan',0.00,'Current',10000.00),(5,'2222233333','rahul',100000012500.00,'Current',10000.00);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login` (
  `login_id` int NOT NULL AUTO_INCREMENT,
  `account_number` varchar(20) NOT NULL,
  `pin` varchar(4) NOT NULL,
  `last_login` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`login_id`),
  KEY `account_number` (`account_number`),
  CONSTRAINT `login_ibfk_1` FOREIGN KEY (`account_number`) REFERENCES `accounts` (`account_number`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES (1,'1234567890','1234','2026-04-04 16:23:26'),(2,'1111111111','4321','2026-04-05 18:38:49'),(3,'2222222222','2468','2026-04-05 18:40:40'),(4,'2222233333','4545','2026-04-23 07:58:39');
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `account_number` varchar(20) NOT NULL,
  `transaction_type` enum('Withdraw','Deposit','Check Balance') NOT NULL,
  `amount` decimal(15,2) DEFAULT NULL,
  `new_balance` decimal(15,2) DEFAULT NULL,
  `transaction_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transaction_id`),
  KEY `account_number` (`account_number`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`account_number`) REFERENCES `accounts` (`account_number`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,'1234567890','Deposit',1000.00,15000.00,'2026-04-04 16:05:44'),(2,'1234567890','Withdraw',500.00,14500.00,'2026-04-04 16:05:44'),(3,'1234567890','Deposit',2000.00,16500.00,'2026-04-05 03:30:00'),(4,'1234567890','Withdraw',1500.00,15000.00,'2026-04-05 05:00:00'),(5,'1234567890','Withdraw',200.00,14800.00,'2026-04-05 06:30:00'),(6,'1234567890','Deposit',3000.00,17800.00,'2026-04-05 08:30:00'),(7,'1234567890','Withdraw',3300.00,14500.00,'2026-04-05 10:00:00'),(8,'1234567890','Withdraw',4000.00,10500.00,'2026-04-05 16:35:56'),(9,'1234567890','Withdraw',100.00,10400.00,'2026-04-05 16:42:23'),(10,'1234567890','Withdraw',100.00,10300.00,'2026-04-05 16:46:06'),(11,'1234567890','Deposit',100.00,10400.00,'2026-04-05 17:26:03'),(12,'1234567890','Deposit',2000.00,12400.00,'2026-04-12 18:29:55'),(13,'1234567890','Withdraw',2000.00,10400.00,'2026-04-12 18:30:22'),(14,'1234567890','Deposit',4000.00,14400.00,'2026-04-16 05:22:44'),(15,'1234567890','Withdraw',2000.00,12400.00,'2026-04-23 07:56:29'),(16,'1234567890','Deposit',1000.00,13400.00,'2026-04-23 07:56:37'),(17,'2222233333','Deposit',2500.00,2500.00,'2026-04-23 07:59:20'),(18,'2222233333','Deposit',10000.00,12500.00,'2026-04-23 07:59:27'),(19,'2222233333','Deposit',100000000000.00,100000012500.00,'2026-04-23 07:59:45');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-24 21:25:55
