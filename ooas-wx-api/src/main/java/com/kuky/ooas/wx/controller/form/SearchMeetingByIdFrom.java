package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: SearchMeetingByIdFrom
 * @Author Kuky
 * @Date: 2021/6/11 1:19
 * @Version 1.0
 */
@ApiModel
@Data
public class SearchMeetingByIdFrom {
    @NotNull
    @Min(1)
    private Integer id;
}
