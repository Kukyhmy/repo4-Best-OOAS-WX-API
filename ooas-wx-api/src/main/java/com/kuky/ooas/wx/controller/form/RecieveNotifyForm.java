package com.kuky.ooas.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: RecieveNotifyForm
 * @Author Kuky
 * @Date: 2021/6/11 22:36
 * @Version 1.0
 */
@Data
@ApiModel
public class RecieveNotifyForm {
    /**
     工作流实例id
     */
    @NotBlank
    private String processId;

    /**
     * 会议id
     */
    @NotBlank
    private String uuid;

    /**
     * 审批的结果
     */
    @NotBlank
    private String result;
}
