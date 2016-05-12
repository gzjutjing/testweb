
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11)  DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO `test`(user_id,name) VALUES (1,'zhangsan');
INSERT INTO `test`(user_id,name) VALUES (2,'lisi');
INSERT INTO `test`(user_id,name) VALUES (3,'wangwu');
