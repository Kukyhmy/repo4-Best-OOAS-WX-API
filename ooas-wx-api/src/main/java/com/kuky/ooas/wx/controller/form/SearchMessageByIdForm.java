package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: SearchMessageByIdForm
 * @Author Kuky
 * @Date: 2021/6/9 22:01
 * @Version 1.0
 */
@ApiModel
@Data
public class SearchMessageByIdForm {
    @NotBlank
    private String id;
}
