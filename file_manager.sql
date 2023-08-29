create database if not exists wjl_file character set 'utf8mb4';

use wjl_file;
drop table if exists file_record;
create table if not exists file_record
(
    id            char(32) primary key comment '文件md5值',
    file_name      varchar(100) not null comment '文件名称',
    mime_type  varchar(50)  not null comment '文件真实类型',
    content_type  varchar(50)  not null comment '文件content-type',
    size          varchar(100) not null comment '文件大小',
    bucket        varchar(30)  not null comment '文件bucket',
    path          varchar(200) not null comment '文件访问路径',
    url           varchar(200) comment '文件访问地址',
    download_count bigint default 0 comment '文件下载次数',
    original_name varchar(100) comment '文件原名称',
    storage_type  char(10) comment '存储的类型',
    storage_id    int comment '存储的主键',
    description   varchar(255) comment '文件描述',
    create_time   datetime     not null comment '创建时间',
    update_time   datetime     not null comment '更新时间'
);