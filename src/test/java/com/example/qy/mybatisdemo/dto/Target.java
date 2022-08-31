package com.example.qy.mybatisdemo.dto;

import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class Target {
    private Long id;

    private String name;

    private String address;

    private List<TargetDto> list;

    private List<String> strList;
}
