package com.ytyo.Controller;

import com.ytyo.annotation.authority.SuperAdmin;
import com.ytyo.Model.ElasticSearch.Category.SecondLevelCategory;
import com.ytyo.Service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class InitController {
    @Autowired
    InitService initService;

    @PostMapping("/reinitCategory")
    @SuperAdmin
    public boolean reinitCategory(@RequestBody Map<String, Map<SecondLevelCategory, List<String>>> map) {
        return initService.reInit(map);
    }
}
