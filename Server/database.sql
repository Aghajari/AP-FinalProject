CREATE DATABASE  IF NOT EXISTS `Discord` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `Discord`;
CREATE TABLE `message` (
  `message_index` int NOT NULL AUTO_INCREMENT,
  `fromId` varchar(200) DEFAULT NULL,
  `toId` varchar(200) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `type` smallint DEFAULT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `data` json DEFAULT NULL,
  `seen` bit(1) DEFAULT NULL,
  `edited` bit(1) DEFAULT NULL,
  PRIMARY KEY (`message_index`)
);
USE `Discord`;
CREATE TABLE `opened_chats` (
  `opened_chats_index` int NOT NULL AUTO_INCREMENT,
  `ownerId` varchar(200) DEFAULT NULL,
  `userId` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`opened_chats_index`)
);
USE `Discord`;
CREATE TABLE `server` (
  `index` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `channels` json DEFAULT NULL,
  `owner` varchar(45) DEFAULT NULL,
  `image` text,
  `permissions` int DEFAULT NULL,
  `serverId` varchar(45) DEFAULT NULL,
  `inviteCode` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`index`),
  UNIQUE KEY `id_UNIQUE` (`index`)
);
USE `Discord`;
CREATE TABLE `user_servers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `server_id` varchar(45) DEFAULT NULL,
  `user_id` varchar(45) DEFAULT NULL,
  `permissions` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
);
USE `Discord`;
CREATE TABLE `friendships` (
  `friendships_index` int NOT NULL AUTO_INCREMENT,
  `fromId` varchar(200) DEFAULT NULL,
  `toId` varchar(200) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`friendships_index`)
);
USE `Discord`;
CREATE TABLE `blocked` (
  `index` int NOT NULL AUTO_INCREMENT,
  `fromId` varchar(45) DEFAULT NULL,
  `toId` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`index`),
  UNIQUE KEY `index_UNIQUE` (`index`)
);
USE `Discord`;
CREATE TABLE `bot` (
  `index` int NOT NULL AUTO_INCREMENT,
  `client_id` varchar(45) DEFAULT NULL,
  `link` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`index`),
  UNIQUE KEY `index_UNIQUE` (`index`)
);
USE `Discord`;