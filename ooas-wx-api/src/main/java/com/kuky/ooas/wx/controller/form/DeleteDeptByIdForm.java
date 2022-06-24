package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: DeleteDeptByIdForm
 * @Author Kuky
 * @Date: 2021/6/12 23:33
 * @Version 1.0
 */
@Data
@ApiModel
public class DeleteDeptByIdForm {
    @NotNull
    @Min(1)
    private Integer id;
}
