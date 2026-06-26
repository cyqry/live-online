package com.ytyo.Service;

import com.ytyo.Const.GeneralConst;
import com.ytyo.Const.RemoteConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Dao.AnchorMapper;
import com.ytyo.Dao.UserMapper;
import com.ytyo.Manager.UserManager;
import com.ytyo.Model.Anchor;
import com.ytyo.Model.AnchorRegistrationInfo;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AnchorService {


    @Autowired
    AnchorMapper anchorMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserManager userManager;
    @Autowired
    ObjectMapper objectMapper;

    public Option<Anchor> selectAnchor(Long id) {
        if (id == null)
            return Option.None();
        Anchor anchor = anchorMapper.selectAnchorById(id);
        return Option.from(anchor);
    }

    public boolean existAnchor(Long id) {
        if (id == null)
            return false;
        return anchorMapper.countByMap(Map.of("id", id)) > 0;
    }


    public Option<List<User>> blurrySelectAnchorByNickname(String nickname) {
        List<User> users = anchorMapper.selectAnchorByNickname(nickname);
        return Option.from(users);
    }

    public boolean anchorRegister(User loginUser, @Validated AnchorRegistrationInfo registrationInfo, String cookieValue) {
        if (loginUser == null) {
            return false;
        }

        //主播注册

        //更新身份信息
        User user = new User();
        user.setId(loginUser.getId());
        user.setRealName(registrationInfo.getRealName());
        user.setIdNumber(registrationInfo.getIdNumber());

        if (!userManager.updateUser(user, cookieValue)) {
            return false;
        }


        Anchor anchor = new Anchor();
        anchor.setId(loginUser.getId());
        anchor.setDescription(registrationInfo.getDescription());

        Anchor oldAnchor = anchorMapper.selectAnchorById(loginUser.getId());
        if (oldAnchor != null)
            return false;

        int i = anchorMapper.insertAnchor(anchor);
        if (i < 1) {
            return false;
        }


        ResponseEntity<String> response = null;
        try {
            //添加房间
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add(RemoteConst.REMOTE_INVOKE_HEADER, RemoteConst.REMOTE_INVOKE_TOKEN);
            headers.add(GeneralConst.X_USER_HEADER, RequestUtil.encodeUser(user).unwrap());

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("roomItemCategoryId", registrationInfo.getRoomItemCategoryId());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            String url = "http://localhost:8081/createRoomBase";


            //若开启事务， 那么此时还未提交主播注册，由于外键约束，这里请求添加房间会失败。
            response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return true;
            } else {
                System.out.println(response.getStatusCode());
                // handle error
                return false;
            }
        } catch (RestClientException | NoneException e) {
            //抛出异常,这里不使用事务
            //模拟回滚
            anchorMapper.deleteAnchorById(loginUser.getId());
            throw new RuntimeException(e);
        } finally {
            if (response != null)
                System.out.println(response.getBody());
        }
    }
}
