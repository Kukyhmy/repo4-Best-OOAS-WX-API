package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: SearchMembersForm
 * @Author Kuky
 * @Date: 2021/6/10 21:59
 * @Version 1.0
 */
@Data
@ApiModel
public class SearchMembersForm {
    @NotBlank
    private String members;

}
