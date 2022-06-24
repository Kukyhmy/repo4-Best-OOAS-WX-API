package com.kuky.ooas.wx.controller;

import com.kuky.ooas.wx.common.util.R;
import com.kuky.ooas.wx.controller.form.TestSayHelloForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description: TestController
 * @Author Kuky
 * @Date: 2021/6/4 0:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/test")
@Api("测试Web接口")
public class TestController {


        @PostMapping("/sayHello")
        @ApiOperation("最简单的测试方法")
        public R sayHello(@Valid @RequestBody TestSayHelloForm form){
            return R.ok().put("message","Hello"+form.getName());
        }

    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    @RequiresPermissions(value = {"A", "B"}, logical = Logical.OR)
    public R addUser() {
        return R.ok("用户添加成功");
    }
    }
