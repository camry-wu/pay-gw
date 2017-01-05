
# --- !Ups

use pay_gw_pay;
-- 公告
create table `Announcement` (
    `Oid`			int(11) not null auto_increment,	-- id
	`Type`			int(5) default 1,					-- 1: 公告, 2: 配置
    `Content`		varchar(2000) default null,			-- 内容
    `StartTime`		datetime default null,				-- 开始时间
    `EndTime`		datetime default null,				-- 结束时间
    `InsertTime`	datetime DEFAULT NULL,				-- 插入时间
    `IsActive`		tinyint DEFAULT 1,					-- 逻辑删除标志
	`versions`		int(11) not null,					-- 配置总版本号
    PRIMARY KEY (`Oid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- 业务渠道配置
create table `BizChannel` (
	`Oid`				int(11) not null auto_increment,	-- id
	`ChannelId`         varchar(50) not null,				-- 渠道号
	`ChannelName`       varchar(50),						-- 渠道名称
	`ChannelDomain`     varchar(50) not null,				-- 渠道域名
	`ChaInternalIp`     varchar(200),						-- 内网 ip，可多个
	`ChaInternetIp`     varchar(50),						-- 外网 ip
	`SubChannelId`      varchar(50),						-- 子渠道号
	`SubChannelName`    varchar(50),						-- 子渠道名称
	`AppId`             varchar(50),						-- 应用号
	`AppName`           varchar(50),						-- 应用名称
	`AppLoginKey`       varchar(50),						-- 公众号LoginKey
	`AppLoginSecretKey` varchar(200),						-- 公众号登录秘钥
	`SecretKey`         varchar(200),						-- 请求秘钥
    `InsertTime`		datetime DEFAULT NULL,				-- 插入时间
    `LastModify`		datetime DEFAULT NULL,				-- 修改时间
	`IsActive`			tinyint DEFAULT 1,					-- 逻辑删除标志
    `Version`			int(11) DEFAULT 1,					-- 乐观锁控制
    PRIMARY KEY (`Oid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- 业务渠道支持的多种支付方式配置
create table `BizChannel_PayMethod` (
	`Oid`				int(11) not null auto_increment,	-- id
	`BizChannelId`		varchar(32) not null,				-- 渠道配置编号
	`PayMethod`			int(10) not null,					-- 支付方式
	`SpNo`				varchar(50),						-- 商户号
	`SpName`			varchar(50),						-- 商户名称
	`SubSpNo`			varchar(50),						-- 子商户号
	`SubSpName`			varchar(50),						-- 子商户名称
	`SpExtId`			varchar(100),						-- 商户扩展信息
	`SubSpExtId`		varchar(100),						-- 子商户扩展信息
	`SignKey`			varchar(1024),						-- 支付秘钥
	`ExtKey`			varchar(1024),						-- 备用秘钥
	`Reserve`			varchar(1024),						-- 扩展信息
	`RefundEnabled`		tinyint(1) default 1 not null,		-- 是否支持原路退回
    `InsertTime`		datetime DEFAULT NULL,				-- 插入时间
    `LastModify`		datetime DEFAULT NULL,				-- 修改时间
	`IsActive`			tinyint DEFAULT 1,					-- 逻辑删除标志
    `Version`			int(11) DEFAULT 1,					-- 乐观锁控制
    PRIMARY KEY (`Oid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;


# --- !Downs

drop table `Announcement`;
drop table `BizChannel`;
drop table `BizChannel_PayMethod`;
