package com.kuky.ooas.wx.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Description: SystemConstants
 * @Author Kuky
 * @Date: 2021/6/6 18:06
 * @Version 1.0
 */
@Data
@Component
public class SystemConstants {
    public String attendanceStartTime;
    public String attendanceTime;
    public String attendanceEndTime;
    public String closingStartTime;
    public String closingTime;
    public String closingEndTime;
}
