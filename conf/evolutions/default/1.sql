
# --- !Ups

use pay_gw_user;
CREATE TABLE `User` (
    `Oid`			int(11) NOT NULL AUTO_INCREMENT,
    `PubId`			varchar(36) DEFAULT NULL,
    `OpenId`		varchar(255) DEFAULT NULL,
    `OpenIdSrc`		varchar(20) DEFAULT NULL,
    `Mobile`		varchar(12) DEFAULT NULL,
    `Career`		varchar(255) DEFAULT NULL,
    `InsertTime`	datetime DEFAULT NULL,
    `LastModify`	datetime DEFAULT NULL,
    `IsActive`		tinyint DEFAULT 1,
    `Version`		int(11) DEFAULT 1,
    PRIMARY KEY (`Oid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

CREATE TABLE `Address` (
    `Oid`			int(11) NOT NULL AUTO_INCREMENT,
    `PubId`			varchar(36) DEFAULT NULL,
    `UserId`		int(11) DEFAULT NULL,
    `Name`			varchar(255) DEFAULT NULL,
    `Country`		char(8) DEFAULT NULL,
    `Province`		char(8) DEFAULT NULL,
    `City`			varchar(255) DEFAULT NULL,
    `Address`		varchar(255) DEFAULT NULL,
    `Mobile`		varchar(12) DEFAULT NULL,
    `IsDefault`		int(1) DEFAULT 0,
    `InsertTime`	datetime DEFAULT NULL,
    `LastModify`	datetime DEFAULT NULL,
    `IsActive`		tinyint DEFAULT 1,
    `Version`		int(11) DEFAULT 1,
    PRIMARY KEY (`Oid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

# --- !Downs

drop table `user`;
drop table `address`;
