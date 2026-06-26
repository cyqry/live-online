package com.ytyo.Dao;

import com.ytyo.Model.User;
import com.ytyo.Utils.DynamicSqlUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {
    List<User> selectAllUser();


    //删除和更新必须由manager调用
    int deleteUserById(long id);

    @UpdateProvider(value = DynamicSqlUtil.class, method = "generateUpdateSql")
    int updateUserById(@Param("model") User user);

    int countByMap(@Param("fields") Map<String, Object> fields);

    int insertUser(User user);

    User getUserByPhonePassword(String phone, String password);

    User getUserByEmailPassword(String email, String password);

    User getUserById(long id);

    User getUserByPersonalityId(String personalityId);
    List<User> getUsersByIds(List<Long> ids);

    String getUserPropertyById(long id);
}
