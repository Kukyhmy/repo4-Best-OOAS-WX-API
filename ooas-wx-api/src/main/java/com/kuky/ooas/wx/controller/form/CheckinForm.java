package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description: CheckinForm
 * @Author Kuky
 * @Date: 2021/6/7 20:40
 * @Version 1.0
 */
@Data
@ApiModel
public class CheckinForm {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}
