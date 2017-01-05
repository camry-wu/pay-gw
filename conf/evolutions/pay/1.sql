
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

/*
-- 支付请求记录
create table `PayRecord` (
	`Oid`			VARCHAR2(50) not null,				-- uuid
	paysn            NUMBER(30),
	amount           NUMBER(10),
	payamount        NUMBER(10),
	paystate         NUMBER(2),
	payway           NUMBER(10),
	noticetradesn    VARCHAR2(50),
	successpaytime   DATE,
	noticepayaccount VARCHAR2(50),
	noticetradestate VARCHAR2(50),
	params           VARCHAR2(4000),
	clientid         VARCHAR2(30),
	custid           VARCHAR2(50),
	goodsname        VARCHAR2(255),
	mobileno         NUMBER(20)
	inserttime       DATE,
	lastmodify       DATE,
	status           NUMBER(1),
	version          NUMBER(2),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 与第三方交互日志
create table `PayRecordLog_ThirdParty` (
    `Oid`			int(11) not null auto_increment,	-- id
	paysn        NUMBER(30),
	payway       NUMBER(10),
	result       NUMBER(2),
	reqparams    VARCHAR2(1000),
	respparams   VARCHAR2(1000),
	inserttime   DATE,
	status       NUMBER(1),
	type         NUMBER(1),
	version      NUMBER(2),
	payid        VARCHAR2(50),
	returnparams VARCHAR2(1000)
)

-- 异常处理记录
create table PayRecord_Fail
(
    `Oid`			int(11) not null auto_increment,	-- id
	paysn        NUMBER(30),
	failmsg      VARCHAR2(255),
	operstate    NUMBER(2),
	inserttime   DATE,
	status       NUMBER(1),
	lastmodify   DATE,
	operresult   VARCHAR2(255),
	operid       VARCHAR2(50),
	opername     VARCHAR2(50),
	operdeptid   NUMBER(10),
	operdeptname VARCHAR2(50),
	payid        VARCHAR2(50),
	version      NUMBER(2)
)

-- 退款请求记录
create table Refund (
	oid            VARCHAR2(50) not null,
	paysn          NUMBER(30),
	refundsn       NUMBER(30),
	refundtype     NUMBER(2),
	clientid       VARCHAR2(30),
	totalamount    NUMBER(10),
	refundamount   NUMBER(10),
	state          NUMBER(2),
	payway         NUMBER(10),
	reqparams      VARCHAR2(1000),
	noticerefundsn VARCHAR2(50),
	noticestatus   VARCHAR2(50),
	inserttime     DATE,
	status         NUMBER(1),
	lastmodify     DATE,
	version        NUMBER(5)
)

-- 退款交互日志
create table Refund_Log (
  oid        VARCHAR2(50) not null,
  refundid   VARCHAR2(50),
  paysn      NUMBER(30),
  refundsn   NUMBER(30),
  payway     NUMBER(10),
  result     NUMBER(2),
  reqparams  VARCHAR2(1000),
  respparams VARCHAR2(1000),
  type       NUMBER(1),
  inserttime DATE,
  status     NUMBER(1),
  lastmodify DATE,
  version    NUMBER(5)
)

-- 退款异常处理
create table Refund_Fail (
  oid          VARCHAR2(50) not null,
  refundid     VARCHAR2(50),
  paysn        NUMBER(30),
  refundsn     NUMBER(30),
  failmsg      VARCHAR2(500),
  operstate    NUMBER(2),
  operresult   VARCHAR2(200),
  operid       VARCHAR2(30),
  opername     VARCHAR2(50),
  operdeptid   VARCHAR2(30),
  operdeptname VARCHAR2(50),
  inserttime   DATE,
  status       NUMBER(1),
  lastmodify   DATE,
  version      NUMBER(5)
)
*/

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
