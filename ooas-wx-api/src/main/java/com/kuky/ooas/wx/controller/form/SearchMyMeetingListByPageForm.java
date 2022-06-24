package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: SearchMyMeetingListByPageForm
 * @Author Kuky
 * @Date: 2021/6/10 15:50
 * @Version 1.0
 */
@ApiModel
@Data
public class SearchMyMeetingListByPageForm {
    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Range(min = 1,max = 40)
    private Integer length;
}
