package com.example.qy.mybatisdemo.dal.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserQueryParam {
//    private String name;
//    private Integer age;

    private Integer isDefault;
    private List<UserQueryDto> list;
}
