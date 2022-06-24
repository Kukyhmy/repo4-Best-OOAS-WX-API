package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @Description: SearchUserMeetingInMonthForm
 * @Author Kuky
 * @Date: 2021/6/12 15:18
 * @Version 1.0
 */
@Data
@ApiModel
public class SearchUserMeetingInMonthForm {
    @Range(min = 2000, max = 9999)
    private Integer year;

    @Range(min = 1, max = 12)
    private Integer month;
}
