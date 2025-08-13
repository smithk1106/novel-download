-- CREATE DATABASE `novel_simple` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `appConfig` (
  `id` BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
  `config_key` VARCHAR(100) DEFAULT NULL COMMENT 'name',
  `config_value` VARCHAR(2500) COMMENT 'value',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  COMMENT ='System Config';

CREATE TABLE IF NOT EXISTS `searchResult` (
  `id` BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
  `source_id` VARCHAR(100) COMMENT '书源id',
  `content` VARCHAR(2500) COMMENT 'result',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  COMMENT ='Search result';

CREATE TABLE IF NOT EXISTS `novel` (
  `id` BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键ID',
  `name` VARCHAR(255) COMMENT 'Book name',
  `cover` VARCHAR(512) COMMENT 'Book cover',
  `author` VARCHAR(255) COMMENT 'Author',
  `intro` VARCHAR(2048) COMMENT 'Description',
  `category_id` INT NOT NULL DEFAULT 0 COMMENT 'Category ID',
  `status` INT NOT NULL DEFAULT 0 COMMENT 'Status: 0-Ongoing, 1-Completed',
  `word_count` BIGINT NOT NULL DEFAULT 0 COMMENT 'Word count',
  `last_update_time` DATETIME COMMENT 'Last update time',
  `latest_chapter` VARCHAR(255) COMMENT 'Latest chapter',
  `save_type` VARCHAR(8) NOT NULL DEFAULT 'html' COMMENT 'Save type',
  `download_url` VARCHAR(255) COMMENT 'Download URL',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  COMMENT ='Novel information';

CREATE TABLE IF NOT EXISTS `chapter`
(
    `id`       BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `book_id` BIGINT(20) DEFAULT NULL COMMENT '目录ID',
    `title`  VARCHAR(255) COMMENT '小说章节名',
    `order1` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '章节顺序',
    `word_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '字数',
    `content`  MEDIUMTEXT COMMENT '小说章节内容',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='小说内容表';
