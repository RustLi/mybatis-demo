package com.example.qy.mybatisdemo.dto;

import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class Source {
    private String name;

    private Long id;

    private List<SourceDto> list;

    private Collection<String> strList;
}
