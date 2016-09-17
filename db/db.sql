/*
 Product: Pay-GW
 Creator: camry_camry@sina.com at 2016-09-16
 Copyright: Copyright 2016 by Vitular Corp. Ltd
 */

grant all on *.* to 'pay'@'%' identified by 'vitular123#' with grant option;
grant all on *.* to 'pay'@'localhost' identified by 'vitular123#' with grant option;

/* user database */
drop database if exists pay_gw_user;
create database pay_gw_user;

/* order database */
drop database if exists pay_gw_order;
create database pay_gw_order;

/* pay database */
drop database if exists pay_gw_pay;
create database pay_gw_pay;
