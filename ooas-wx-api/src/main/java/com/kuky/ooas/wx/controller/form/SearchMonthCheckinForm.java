package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @Description: SearchMonthCheckinForm
 * @Author Kuky
 * @Date: 2021/6/8 22:50
 * @Version 1.0
 */
@Data
@ApiModel
public class SearchMonthCheckinForm {
    @NotNull
    @Range(min = 2000, max = 3000)
    private Integer year;

    @NotNull
    @Range(min = 1, max = 12)
    private Integer month;
}
