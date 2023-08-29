package com.github.wjlong1128.fileupload.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文件记录表实体
 * </p>
 *
 * @author wjlong1128
 * @since 2023-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file_record")
public class FileRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件md5值
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件content-type
     */
    private String contentType;

    /**
     * 文件mime-type
     */
    private String mimeType;
    /**
     * 文件大小
     */
    private String size;

    /**
     * 文件bucket
     */
    private String bucket;

    /**
     * 文件访问路径
     */
    private String path;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 文件原名称
     */
    private String originalName;

    /**
     * 文件下载次数
     */
    private Long downloadCount;

    /**
     * 存储的类型
     */
    private String storageType;

    /**
     * 存储的主键
     */
    private Integer storageId;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
