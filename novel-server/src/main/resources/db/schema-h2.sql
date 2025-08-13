
CREATE TABLE IF NOT EXISTS `appConfig` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键ID',
  `config_key` VARCHAR(100) DEFAULT NULL COMMENT 'name',
  `config_value` VARCHAR(2500) COMMENT 'value',
  PRIMARY KEY (`id`),
  KEY `index_config_key` (`config_key`)
);

CREATE TABLE IF NOT EXISTS `searchResult` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键ID',
  `source_id` VARCHAR(100) COMMENT '书源id',
  `content` VARCHAR(2500) COMMENT 'result',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `novel` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键ID',
  `name` VARCHAR(250) COMMENT 'name',
  `cover` VARCHAR(250) COMMENT '封面',
  `author` VARCHAR(250) COMMENT '作者',
  `intro` VARCHAR(2048) COMMENT 'Description',
  `category_id` INT NOT NULL DEFAULT 0 COMMENT 'Category ID',
  `status` INT NOT NULL DEFAULT 0 COMMENT 'Status: 0-Ongoing, 1-Completed',
  `word_count` BIGINT NOT NULL DEFAULT 0 COMMENT 'Word count',
  `last_update_time` DATETIME COMMENT 'Last update time',
  `latest_chapter` VARCHAR(255) COMMENT 'Latest chapter',
  `save_type` VARCHAR(8) NOT NULL DEFAULT 'html' COMMENT 'Save type',
  `download_url` VARCHAR(255) COMMENT 'Download URL',
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `chapter`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `book_id` bigint(20) DEFAULT NULL COMMENT '目录ID',
    `title`  VARCHAR(255) COMMENT '小说章节名',
    `order1` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '章节顺序',
    `word_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '字数',
    `content`  MEDIUMTEXT COMMENT '小说章节内容',
    PRIMARY KEY (`id`)
)
