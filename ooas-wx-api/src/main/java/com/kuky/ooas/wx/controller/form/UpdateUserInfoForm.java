package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: UpdateUserInfoForm
 * @Author Kuky
 * @Date: 2021/6/13 19:44
 * @Version 1.0
 */
@Data
@ApiModel
public class UpdateUserInfoForm {
    @NotNull
    @Min(1)
    private Integer userId;

    @NotBlank
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$")
    private String name;

    @NotBlank
    @Pattern(regexp = "^男$|^女$")
    private String sex;

    @NotBlank
    private String deptName;

    @NotBlank
    @Pattern(regexp = "^1[0-9]{10}$")
    private String tel;

    @NotBlank
    @Pattern(regexp = "^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$")
    private String email;

    @NotBlank
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$")
    private String hiredate;

    @NotBlank
    private String role;

    @Range(min = 1, max = 2)
    private int status;
}
