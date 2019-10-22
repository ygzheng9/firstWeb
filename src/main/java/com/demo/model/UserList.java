package com.demo.model;

import lombok.Data;

import java.util.List;

/**
 * @author ygzheng
 */
@Data
public class UserList {
    private int code;
    private String msg;
    private List<User> data;

}
