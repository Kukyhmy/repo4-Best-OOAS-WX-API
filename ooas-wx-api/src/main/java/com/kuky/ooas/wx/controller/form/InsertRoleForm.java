package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: InsertRoleForm
 * @Author Kuky
 * @Date: 2021/6/12 23:14
 * @Version 1.0
 */
@Data
@ApiModel
public class InsertRoleForm {
    @NotBlank
    private String roleName;
    @NotBlank
    private String permissions;
}
