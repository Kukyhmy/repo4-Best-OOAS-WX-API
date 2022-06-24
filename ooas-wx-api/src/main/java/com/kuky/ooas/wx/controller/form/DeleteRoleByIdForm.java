package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: DeleteRoleByIdForm
 * @Author Kuky
 * @Date: 2021/6/12 23:20
 * @Version 1.0
 */
@Data
@ApiModel
public class DeleteRoleByIdForm {
    @NotNull
    @Min(value = 3,message = "禁止删除系统内置角色")
    private Integer id;
}

