package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: TestSayHelloForm
 * @Author Kuky
 * @Date: 2021/6/4 1:42
 * @Version 1.0
 */
@ApiModel
@Data
public class TestSayHelloForm {

        @NotBlank
     /*   @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$")
        @ApiModelProperty("姓名")*/
        private String name;
    }

