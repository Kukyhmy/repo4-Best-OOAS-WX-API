package com.kuky.ooas.wx.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kuky.ooas.wx.db.pojo.TbHolidays;
import com.kuky.ooas.wx.db.pojo.TbWorkday;
import com.kuky.ooas.wx.service.HolidaysService;
import com.kuky.ooas.wx.service.WorkdayService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description: CalendarScheduledTask
 * @Author Kuky
 * @Date: 2021/6/9 11:50
 * @Version 1.0
 */
@Log4j2
@Component
public class CalendarScheduledTask {

    @Value("${emos.calendar.url}")
    private String url;

    @Value("${emos.calendar.app_id}")
    private String addId;

    @Value("${emos.calendar.app_secret}")
    private String appSecret;

    @Autowired
    private HolidaysService holidaysService;

    @Autowired
    private WorkdayService workdayService;


    // 每年12月28日 01:00:00 同步下一年特殊节假日和特殊工作日
    @Scheduled(cron = "0 29 14 9 6 ?")
    private void getCalendarInfo() {
//        int year = DateUtil.date().year() + 1; // 下一年
        int year = DateUtil.date().year() ;
        log.info("{} 开始更新 {} 年的特殊节假日与特殊工作日", DateUtil.now(), year);
        try {
            String reqUrl = url + year;
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("ignoreHoliday", false);
            paramMap.put("app_id", addId);
            paramMap.put("app_secret", appSecret);
            String res = HttpUtil.get(reqUrl, paramMap);
            JSONObject json = JSONUtil.parseObj(res);
            log.info("json:"+json);
            JSONArray jsonArray = json.get("data", JSONArray.class);
            log.info("jsonArray:"+jsonArray);
            List<TbHolidays> holidays = new ArrayList<>();
            List<TbWorkday> workdays = new ArrayList<>();
            List<JSONObject> months = JSONUtil.toList(jsonArray, JSONObject.class);
            log.info("months:"+months);
            for (JSONObject month : months) {
                JSONArray jsonArrayDays = month.get("days", JSONArray.class);
                List<JSONObject> days = JSONUtil.toList(jsonArrayDays, JSONObject.class);

                for (JSONObject day : days) {
                    String date = day.get("date", String.class);
                    int weekDay = day.get("weekDay", Integer.class);
                    // type:  0 工作日 1 假日 2 节假日 如果ignoreHoliday参数为true，这个字段不返回
                    int type = day.get("type", Integer.class);
                    // 如果在周一至周五区间，并且是休息日，则算做特殊休息日
                    if (1 <= weekDay && weekDay <= 5 && (type == 1 || type == 2)) {
                        TbHolidays holiday = new TbHolidays();
                       //LocalDate--->Date
                        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
                        ZoneId zone = ZoneId.systemDefault();
                        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
                        holiday.setDate(Date.from(instant));
                        holidays.add(holiday);
                        continue;
                    }
                    // 如果是周六或周日，并且是工作日，则算做特殊工作日
                    if ((6 == weekDay || 7 == weekDay) && type == 0) {
                        TbWorkday workday = new TbWorkday();
                        //LocalDate--->Date
                        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
                        ZoneId zone = ZoneId.systemDefault();
                        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
                        workday.setDate(Date.from(instant));
                        workdays.add(workday);
                    }
                }
            }
            // TODO 是否删除前一年的信息？ 如果需要可以加上 DELETE FROM table WHERE YEAR(date) < #{thisYear}
            // 保存至数据库
            holidaysService.saveBatch(holidays);
            workdayService.saveBatch(workdays);

            log.info("{} 更新完成", DateUtil.now());
        } catch (RuntimeException e) {
            log.error("更新日历信息时出现异常: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
