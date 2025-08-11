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
  `name` VARCHAR(250) COMMENT 'name',
  `cover` VARCHAR(250) COMMENT '封面',
  `author` VARCHAR(250) COMMENT '作者',
  `download_url` VARCHAR(250) COMMENT '下载地址',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  COMMENT ='Novel information';
