package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: SearchRoomIdByUUIDForm
 * @Author Kuky
 * @Date: 2021/6/12 14:02
 * @Version 1.0
 */
@Data
@ApiModel
public class SearchRoomIdByUUIDForm {
    @NotBlank
    private String uuid;
}