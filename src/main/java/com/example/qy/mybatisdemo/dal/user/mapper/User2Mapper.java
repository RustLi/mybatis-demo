package com.example.qy.mybatisdemo.dal.user.mapper;

import com.example.qy.mybatisdemo.dal.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author: lwl
 * @date: 2022/8/4
 * @description:  仅采用  mybatis-spring-boot-starter 使用 Repository
 **/
@Mapper
@Repository
public interface User2Mapper {
    List<User> queryByName(@Param("name")String name);
}
