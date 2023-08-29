package com.github.wjlong1128.fileupload.entity;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class Customer {
    private Integer id;
    private String username;
}
