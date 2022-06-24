package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: SelectUserPhotoAndNameForm
 * @Author Kuky
 * @Date: 2021/6/11 22:52
 * @Version 1.0
 */
@Data
@ApiModel
public class SelectUserPhotoAndNameForm {
    @NotBlank
    private String ids;
}
