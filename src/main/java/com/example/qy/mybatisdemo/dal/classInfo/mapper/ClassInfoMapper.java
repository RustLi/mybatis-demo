package com.example.qy.mybatisdemo.dal.classInfo.mapper;

import com.example.qy.mybatisdemo.dal.classInfo.entity.ClassInfo;
import com.example.qy.mybatisdemo.dal.classInfo.entity.ClassInfoCriteria;
import tk.mybatis.mapper.common.Mapper;

public interface ClassInfoMapper extends Mapper<ClassInfo> {
    int deleteByFilter(ClassInfoCriteria filter);
}