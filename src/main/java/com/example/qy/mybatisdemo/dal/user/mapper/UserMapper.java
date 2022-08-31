package com.example.qy.mybatisdemo.dal.user.mapper;

import com.example.qy.mybatisdemo.annotations.MapF2F;
import com.example.qy.mybatisdemo.dal.dto.UserQueryDto;
import com.example.qy.mybatisdemo.dal.dto.UserQueryParam;
import com.example.qy.mybatisdemo.dal.user.entity.User;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserMapper extends Mapper<User> {

    int queryCount(@Param("id")Integer id);

    int batchInsert(List<User> list);

    int batchUpdate(@Param("list") List<User> list);

    List<Integer> selectIdsByCode();

    void initSetting(Collection<User> userList);

    @MapF2F
    Map<Long,String> queryIdAndName(@Param("id")Long id);

    void updateUser(User user);

    List<User> queryByName(@Param("name")String name);

    List<User> queryByIds(@Param("ids")Collection<Integer> ids);

    List<User> queryByParam(@Param("isDefault")Integer isDefault, @Param("list")List<UserQueryDto> list);

    List<User> queryListByParam(UserQueryParam param);

    void delByCondition(UserQueryParam param);

    void updateTest(String name);

    String queryTest(String name);
}