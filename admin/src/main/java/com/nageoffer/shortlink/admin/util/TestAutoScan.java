package com.nageoffer.shortlink.admin.util;

import com.nageoffer.shortlink.common.util.ToolComponent;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAutoScan {
    @Resource
    private ToolComponent toolComponent;


    @PostMapping("/api/test/testAutoScan")
    public String testAutoScan()
    {
        toolComponent.doSomething();
        return "success";
    }
}
