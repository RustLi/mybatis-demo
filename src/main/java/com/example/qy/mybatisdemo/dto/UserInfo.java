package com.example.qy.mybatisdemo.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    private String name;
    private Integer age;
    private List<ClassInfo> classInfoList;

}
