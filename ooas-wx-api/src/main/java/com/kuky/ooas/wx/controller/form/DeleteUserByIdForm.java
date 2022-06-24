package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: DeleteUserByIdForm
 * @Author Kuky
 * @Date: 2021/6/13 19:50
 * @Version 1.0
 */
@Data
@ApiModel
public class DeleteUserByIdForm {
    @NotNull
    @Min(1)
    private Integer id;
}
