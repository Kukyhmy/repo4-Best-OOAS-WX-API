package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: DeleteMeetingByIdForm
 * @Author Kuky
 * @Date: 2021/6/11 20:32
 * @Version 1.0
 */
@ApiModel
@Data
public class DeleteMeetingByIdForm {
    @NotNull
    @Min(1)
    private Integer id;
}
