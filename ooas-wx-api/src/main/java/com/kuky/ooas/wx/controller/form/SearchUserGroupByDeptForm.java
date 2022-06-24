package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @Description: SearchUserGroupByDeptForm
 * @Author Kuky
 * @Date: 2021/6/10 19:25
 * @Version 1.0
 */
@Data
@ApiModel
public class SearchUserGroupByDeptForm {

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,15}$")
    private String keyword;
}
