package com.ytyo.Dao;

import com.ytyo.Model.Anchor;
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
public interface AnchorMapper {
    List<Anchor> selectAllAnchor();

    Anchor selectAnchorById(Long id);

    List<User> selectAnchorByNickname(String nickname);
    int deleteAnchorById(long anchorId);

    @UpdateProvider(value = DynamicSqlUtil.class, method = "generateUpdateSql")
    int updateAnchorById(@Param("model") Anchor anchor);

    int countByMap(@Param("fields") Map<String, Object> fields);

    int insertAnchor(Anchor anchor);
}
