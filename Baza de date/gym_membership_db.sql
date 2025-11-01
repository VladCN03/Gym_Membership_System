-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gym_membership_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `email` varchar(255) NOT NULL,
  `membership_type_id` bigint unsigned DEFAULT NULL,
  `trainer_id` bigint unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `birth_year` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_member__email` (`email`),
  KEY `ix_member__membership_type_id` (`membership_type_id`),
  KEY `ix_member__trainer_id` (`trainer_id`),
  KEY `ix_member__name` (`name`),
  CONSTRAINT `fk_member__membership_type` FOREIGN KEY (`membership_type_id`) REFERENCES `membership_type` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_member__trainer` FOREIGN KEY (`trainer_id`) REFERENCES `trainer` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (1,'Alex D','alex@gym.ro',1,2,'2025-10-18 11:29:23','2025-11-01 16:26:47',NULL),(2,'Bianca C','bianca@gym.ro',2,1,'2025-10-18 11:29:23','2025-10-18 11:29:23',NULL),(3,'Ciprian L','ciprian@gym.ro',9,2,'2025-10-18 11:29:23','2025-11-01 19:47:43',NULL),(5,'Vlad C','vlad@gym.ro',6,5,'2025-10-18 13:20:41','2025-10-18 13:22:15',NULL),(6,'Tavi A','tavi@gym.ro',6,5,'2025-10-18 15:52:12','2025-11-01 19:48:18',NULL),(7,'Vasile D','vasile@gym.ro',6,5,'2025-10-18 15:56:48','2025-11-01 19:50:08',NULL),(8,'Roli B','roli@gym.ro',3,3,'2025-10-21 14:05:23','2025-11-01 19:48:00',NULL);
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_type`
--

DROP TABLE IF EXISTS `membership_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_type` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_membership_type__type` (`type`),
  CONSTRAINT `ck_membership_type__price_nonneg` CHECK ((`price` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_type`
--

LOCK TABLES `membership_type` WRITE;
/*!40000 ALTER TABLE `membership_type` DISABLE KEYS */;
INSERT INTO `membership_type` VALUES (1,'Basic',89.00,'2025-10-18 11:29:23','2025-11-01 18:03:23'),(2,'Plus',149.00,'2025-10-18 11:29:23','2025-10-18 11:29:23'),(3,'Premium',199.00,'2025-10-18 11:29:23','2025-10-18 11:29:23'),(6,'VIP',249.00,'2025-10-18 13:17:11','2025-10-18 13:17:11'),(9,'Student',59.00,'2025-11-01 19:06:08','2025-11-01 19:49:39');
/*!40000 ALTER TABLE `membership_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trainer`
--

DROP TABLE IF EXISTS `trainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainer` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `email` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_trainer__email` (`email`),
  KEY `ix_trainer__name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trainer`
--

LOCK TABLES `trainer` WRITE;
/*!40000 ALTER TABLE `trainer` DISABLE KEYS */;
INSERT INTO `trainer` VALUES (1,'Ana Ionescu','anaa@gym.ro','2025-10-18 11:29:23','2025-11-01 17:01:29'),(2,'Mihai Pop','mihai@gym.ro','2025-10-18 11:29:23','2025-10-18 11:29:23'),(3,'Ioana Vasilescu','ioana@gym.ro','2025-10-18 11:29:23','2025-10-18 11:29:23'),(5,'Petrica TheCat','petrica@gym.ro','2025-10-18 13:18:38','2025-10-18 13:18:38'),(10,'Ion Popescu','ionn@gym.ro','2025-11-01 19:06:33','2025-11-01 19:06:41');
/*!40000 ALTER TABLE `trainer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_members_per_trainer`
--

DROP TABLE IF EXISTS `v_members_per_trainer`;
/*!50001 DROP VIEW IF EXISTS `v_members_per_trainer`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_members_per_trainer` AS SELECT 
 1 AS `trainer_id`,
 1 AS `trainer_name`,
 1 AS `members_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_membership_stats_by_type`
--

DROP TABLE IF EXISTS `v_membership_stats_by_type`;
/*!50001 DROP VIEW IF EXISTS `v_membership_stats_by_type`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_membership_stats_by_type` AS SELECT 
 1 AS `membership_type_id`,
 1 AS `type`,
 1 AS `price`,
 1 AS `members_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_members_per_trainer`
--

/*!50001 DROP VIEW IF EXISTS `v_members_per_trainer`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_members_per_trainer` AS select `t`.`id` AS `trainer_id`,`t`.`name` AS `trainer_name`,count(`m`.`id`) AS `members_count` from (`trainer` `t` join `member` `m` on((`m`.`trainer_id` = `t`.`id`))) group by `t`.`id`,`t`.`name` order by `members_count` desc,`t`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_membership_stats_by_type`
--

/*!50001 DROP VIEW IF EXISTS `v_membership_stats_by_type`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_membership_stats_by_type` AS select `mt`.`id` AS `membership_type_id`,`mt`.`type` AS `type`,`mt`.`price` AS `price`,count(`m`.`id`) AS `members_count` from (`membership_type` `mt` left join `member` `m` on((`m`.`membership_type_id` = `mt`.`id`))) group by `mt`.`id`,`mt`.`type`,`mt`.`price` order by `members_count` desc,`mt`.`price` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-01 21:55:19
