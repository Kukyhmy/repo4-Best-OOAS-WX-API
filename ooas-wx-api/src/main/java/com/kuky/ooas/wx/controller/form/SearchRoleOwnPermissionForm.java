package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: SearchRoleOwnPermissionForm
 * @Author Kuky
 * @Date: 2021/6/12 23:09
 * @Version 1.0
 */
@Data
@ApiModel
public class SearchRoleOwnPermissionForm {
    @NotNull
    @Min(0)
    private Integer id;

}
