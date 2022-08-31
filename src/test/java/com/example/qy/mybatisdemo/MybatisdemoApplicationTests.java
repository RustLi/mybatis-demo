package com.example.qy.mybatisdemo;

import com.example.qy.mybatisdemo.dal.dto.UserQueryDto;
import com.example.qy.mybatisdemo.dal.dto.UserQueryParam;
import com.example.qy.mybatisdemo.dal.user.entity.User;
import com.example.qy.mybatisdemo.dal.user.mapper.User2Mapper;
import com.example.qy.mybatisdemo.dal.user.mapper.UserMapper;
import com.example.qy.mybatisdemo.dto.*;
import com.example.qy.mybatisdemo.service.UserTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class MybatisdemoApplicationTests {

    @Autowired
    private UserTest userTest;

    @Autowired
    private UserMapper userMapper;

    private Long subjectUnitId;

    @Autowired
    private User2Mapper user2Mapper;


    @Test
    void contextLoads() {
    }

    @Test
    void user2Test(){
        List<User> userList = user2Mapper.queryByName("2341");
        System.out.println(userList);

        for (User user : userList){
            System.out.println("name = " + user.getName());
        }
    }

    @Test
    void userTest(){
//        userTest.getUser();

//        System.out.println(222);
//        Map<Long,String> idAndNameMap = userMapper.queryIdAndName(1L);
//        System.out.println(idAndNameMap);
//
//        System.out.println(333);
//        List<Integer> userIdList = userMapper.selectIdsByCode();
//        System.out.println(333 + " " + userIdList);
//
//        boolean isNull = userIdList == null;
//        System.out.println(isNull);

//        User user = new User();
//        user.setName("lwl0128");
//        user.setAge(22);

//        userMapper.insertSelective(user);

//        System.out.println(user);

//        userMapper.updateUser(user);

//        System.out.println(111);
//
//        UserInfo userInfo = new UserInfo();
//        userInfo.setName("lwl");
//        userInfo.setAge(11);
//
//        List<ClassInfo> classInfoList = Lists.newArrayList();
//        ClassInfo classInfo = new ClassInfo();
//        classInfo.setScore("111");
//        classInfo.setStage("aaa");
//        classInfoList.add(classInfo);
//
//        userInfo.setClassInfoList(classInfoList);
//        log.error("lwl: userInfo = {}",userInfo);
//        Integer isDefault = 0;
//        List<UserQueryParam> paramList = Lists.newArrayList();
        List<UserQueryDto> dtoList = Lists.newArrayList();
        UserQueryParam param = new UserQueryParam();
        param.setIsDefault(1);
//        param.setName("aaa_1");
//        param.setAge(115);
//        paramList.add(param);
        UserQueryDto dto = new UserQueryDto();
        dto.setName("aaa_1");
        dto.setAge(115);
        dtoList.add(dto);
        param.setList(dtoList);
//        List<User> userList = userMapper.queryListByParam(param);
//        System.out.println("444 user  = " + userList);

        List<UserQueryDto> queryDtoList = Lists.newArrayList();
        UserQueryDto queryDto = new UserQueryDto();
        queryDto.setName("aaa_1");
        queryDto.setAge(115);
        queryDtoList.add(queryDto);

        UserQueryParam param1 = new UserQueryParam();
        param1.setList(queryDtoList);
        param1.setIsDefault(8);
        userMapper.delByCondition(param1);

//        List<User> userList1 = userMapper.queryByParam(1,queryDtoList);
//        System.out.println("userList1 222 = " + userList1);

    }

    @Test
    void mapperTest(){
        System.out.println("111..");
        User user = userMapper.selectByPrimaryKey(1);
        System.out.println("111.. user = " + user);

        user.setName("lll");
        user.setAge(22);

        userMapper.updateByPrimaryKeySelective(user);

        System.out.println("222: user = " + user);
//
//        int count = userMapper.queryCount(1);
//        System.out.println("lwl: 111 count = " + count);
//
//        log.info("lwl: user:{}",user);

//        List<Integer> ids = userMapper.selectIdsByCode();
//        System.out.println(ids);

//        List<User> userList = Lists.newArrayList();
//        User user = new User();
//        user.setName("bbb");
//        user.setAge(123);
//        user.setTime(new Date());
//        user.setIsDefault(0);
//        userList.add(user);
//        userMapper.initSetting(userList);

        System.out.println(2222);



    }

    @Test
    void corpTest(){
        System.out.println(111);
        Source source = new Source();
        source.setName("bbb");
        source.setId(333L);

        List<SourceDto> list = Lists.newArrayList();
        SourceDto sourceDto = new SourceDto();
        sourceDto.setId(1L);
        sourceDto.setName("lwl-1");
        list.add(sourceDto);

        Set<String> strList = Sets.newHashSet();
        strList.add("bbb");
        source.setStrList(strList);

        source.setList(list);

        Target target = new Target();
        List<TargetDto> mList = Lists.newArrayList();
        for (SourceDto dto: list) {
            TargetDto targetDto = new TargetDto();
            BeanUtils.copyProperties(dto,targetDto);
            mList.add(targetDto);
        }
        target.setList(mList);
        BeanUtils.copyProperties(source,target);

        System.out.println(source);
        System.out.println("-----");
        System.out.println(target);
    }

    @Test
    void insertTest(){
        System.out.println(222);
        User user1 = new User();
        user1.setName("aaa_1");
        user1.setAge(111);
//        user1.setIsDefault(0);
        userMapper.insertSelective(user1);

        int id = user1.getId();
        System.out.println(id);
    }

    @Test
    void batchInsertTest(){
        User user1 = new User();
        user1.setName("aaa_11");
        user1.setAge(111);
        user1.setTime(new Date());
        user1.setIsDefault(0);

        User user2 = new User();
        user2.setName("aaa_12");
        user2.setAge(222);
        user2.setTime(new Date());
        user2.setIsDefault(0);

        List<User> userList = Lists.newArrayList();
        userList.add(user1);
        userList.add(user2);
        userMapper.batchInsert(userList);

        System.out.println(222);
        System.out.println(userList);

        for (User tempUser: userList) {
            Integer id = tempUser.getId();
            System.out.println("id  = " + id);
        }
    }

    @Test
    void batchUpdateTest(){

//        List<User> userList = Lists.newArrayList();
//        User user = userMapper.selectByPrimaryKey(1);
//        user.setTime(new Date());
//        System.out.println("user = " + user);
//        userList.add(user);

//        List<Integer> ids = new ArrayList<>();
//        ids.add(1);
//        ids.add(2);
//        List<User> userList = userMapper.queryByIds(ids);
//        System.out.println("userList = " + userList);
//        userMapper.batchUpdate(userList);
//
//        System.out.println(222);
//        System.out.println(userList);
//
//        for (User tempUser: userList) {
//            Integer id = tempUser.getId();
//            System.out.println("id = " + id);
//        }

        System.out.println(111);
//        userMapper.updateTest(null);
//        System.out.println(222);

        String name = userMapper.queryTest(null);

        System.out.println("name 111 = " + name);
    }
}
