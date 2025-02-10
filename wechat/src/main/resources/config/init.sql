CREATE TABLE `wechat_movie` (
                                `PK_ID` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `title` varchar(128) NOT NULL,
                                `movie_url` varchar(128)  NULL,
                                `mediaId` varchar(128)  NULL,
                                `pic_url` varchar(128)  NULL,
                                `author` varchar(128)  NULL,
                                `description` varchar(128)  null,
                                PRIMARY KEY (`PK_ID`)
);

CREATE TABLE `wechat_music` (
                                `PK_ID` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `title` varchar(128) NOT NULL,
                                `music_url` varchar(128)  NULL,
                                `author` varchar(128)  NULL,
                                `description` varchar(128)  null,
                                PRIMARY KEY (`PK_ID`)
);

CREATE TABLE `wechat_opinion` (
                                  `PK_ID` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `reply` text NULL,
                                  `text` text  null,
                                  PRIMARY KEY (`PK_ID`)
);

CREATE TABLE `wechat_media_info` (
                                     `PK_ID` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `name` varchar(128) NOT NULL,
                                     `media_type` varchar(128)  NULL,
                                     `mediaId` varchar(128)  NULL,
                                     `url` varchar(128)  NULL,
                                     PRIMARY KEY (`PK_ID`)
);

CREATE TABLE `wechat_user_info` (
                                    `PK_ID` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `userId` varchar(128) NOT NULL,
                                    `location_x` varchar(128)  NULL,
                                    `location_y` varchar(128)  NULL,
                                    `is_sese` int  NULL,
                                    `label` varchar(128)  NULL,
                                    PRIMARY KEY (`PK_ID`)
);