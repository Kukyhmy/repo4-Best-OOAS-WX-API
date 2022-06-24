package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: LoginForm
 * @Author Kuky
 * @Date: 2021/6/6 0:19
 * @Version 1.0
 */
@ApiModel
@Data
public class LoginForm {

    @NotBlank(message = "临时授权不能为空")
    private String code;
}
