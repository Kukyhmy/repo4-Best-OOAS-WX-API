package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: UpdateDeptByIdForm
 * @Author Kuky
 * @Date: 2021/6/12 23:34
 * @Version 1.0
 */
@Data
@ApiModel
public class UpdateDeptByIdForm {
    @NotNull
    @Min(1)
    private Integer id;

    @NotBlank
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{3,15}$")
    private String deptName;
}
