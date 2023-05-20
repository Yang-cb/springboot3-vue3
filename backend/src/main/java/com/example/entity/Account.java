package com.example.entity;

import lombok.Data;

/**
 * separate-project.com.example.entity
 *
 * @description 用户基本信息
 */
@Data
public class Account {
    private Integer id;
    private String email;
    private String username;
    private String password;
}
