package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: InsertDeptForm
 * @Author Kuky
 * @Date: 2021/6/12 23:33
 * @Version 1.0
 */
@Data
@ApiModel
public class InsertDeptForm {
    @NotBlank
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{3,15}$")
    private String deptName;
}
