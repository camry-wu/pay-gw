/*
 Product: Pay-GW
 Creator: camry_camry@sina.com at 2016-09-16
 Copyright: Copyright 2016 by Vitular Corp. Ltd
*/

use pay_gw_user;

CREATE TABLE `user` (
    `oid` int(11) NOT NULL AUTO_INCREMENT,
    `pubId` varchar(20) DEFAULT NULL,
    `openId` varchar(255) DEFAULT NULL,
    `career` varchar(12) DEFAULT NULL,
    PRIMARY KEY (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

CREATE TABLE `address` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `pubId` varchar(32) DEFAULT NULL,
    `userId` int(11) DEFAULT NULL,
    `name` varchar(32) DEFAULT NULL,
    `province` char(4) DEFAULT NULL,
    `city` varchar(10) DEFAULT NULL,
    `address` varchar(255) DEFAULT NULL,
    `mobile` varchar(12) DEFAULT NULL,
    `isDefault` int(1) DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
