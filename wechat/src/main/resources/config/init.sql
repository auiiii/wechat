/*
SQLyog Ultimate v12.14 (64 bit)
MySQL - 8.0.35 : Database - wechat
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`wechat` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `wechat`;

/*Table structure for table `wechat_media_info` */

DROP TABLE IF EXISTS `wechat_media_info`;

CREATE TABLE `wechat_media_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `media_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mediaId` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `wechat_media_info` */

/*Table structure for table `wechat_movie` */

DROP TABLE IF EXISTS `wechat_movie`;

CREATE TABLE `wechat_movie` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `movie_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mediaId` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pic_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `author` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `wechat_movie` */

insert  into `wechat_movie`(`id`,`title`,`movie_url`,`mediaId`,`pic_url`,`author`,`description`) values
(1,'哪吒','无',NULL,'无','饺子','神话');

/*Table structure for table `wechat_music` */

DROP TABLE IF EXISTS `wechat_music`;

CREATE TABLE `wechat_music` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `music_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `author` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `wechat_music` */

/*Table structure for table `wechat_opinion` */

DROP TABLE IF EXISTS `wechat_opinion`;

CREATE TABLE `wechat_opinion` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `wechat_opinion` */

/*Table structure for table `wechat_user_info` */

DROP TABLE IF EXISTS `wechat_user_info`;

CREATE TABLE `wechat_user_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `location_x` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `location_y` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_sese` int DEFAULT NULL,
  `label` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `wechat_user_info` */

insert  into `wechat_user_info`(`id`,`userId`,`location_x`,`location_y`,`is_sese`,`label`) values
(1,'o_tON55uNWm5CVXb0zvexhQ4GWaU','29.617430','106.498726',0,'两江新区黄杨路');

/* ============================================================
   SportPal 运动小程序相关表
   - 所有主键统一采用 BIGINT UNSIGNED 自增，业务实体用 Long
   - user_id / feed_id / comment_id 等外键列同为 BIGINT UNSIGNED
   ============================================================ */

/*Table structure for table `sp_users` */

DROP TABLE IF EXISTS `sp_users`;

CREATE TABLE `sp_users` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信openId（账号密码/手机号注册用户为空）',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名（账号密码登录用）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `password_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码哈希（bcrypt）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_open_id` (`open_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='小程序用户表';

/*Table structure for table `sp_feeds` */

DROP TABLE IF EXISTS `sp_feeds`;

CREATE TABLE `sp_feeds` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `user_id` bigint unsigned NOT NULL COMMENT '发布用户ID',
  `exercise_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '运动类型',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '动态内容',
  `images` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片URL（JSON数组）',
  `location_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置名称',
  `likes` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int unsigned NOT NULL DEFAULT '0' COMMENT '评论数',
  `view_count` int unsigned NOT NULL DEFAULT '0' COMMENT '浏览数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_created` (`user_id`,`created_at`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运动动态表';

/*Table structure for table `sp_feed_comments` */

DROP TABLE IF EXISTS `sp_feed_comments`;

CREATE TABLE `sp_feed_comments` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `feed_id` bigint unsigned NOT NULL COMMENT '所属动态ID',
  `user_id` bigint unsigned NOT NULL COMMENT '评论用户ID',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `likes` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_feed_id` (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='动态评论表';

/*Table structure for table `sp_feed_likes` */

DROP TABLE IF EXISTS `sp_feed_likes`;

CREATE TABLE `sp_feed_likes` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
  `feed_id` bigint unsigned DEFAULT NULL COMMENT '动态ID（点赞动态时填写，评论点赞为空）',
  `user_id` bigint unsigned NOT NULL COMMENT '点赞用户ID',
  `comment_id` bigint unsigned DEFAULT NULL COMMENT '评论ID（点赞评论时填写，动态点赞为空）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_feed` (`user_id`,`feed_id`),
  UNIQUE KEY `uk_user_comment` (`user_id`,`comment_id`),
  KEY `idx_feed_id` (`feed_id`),
  KEY `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='点赞记录表（动态/评论共用）';

/*Table structure for table `sp_checkins` */

DROP TABLE IF EXISTS `sp_checkins`;

CREATE TABLE `sp_checkins` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `exercise_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '运动类型',
  `images` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片URL（JSON数组）',
  `location_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置名称',
  `note` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '打卡备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_created` (`user_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运动打卡表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
