package com.kuky.ooas.wx.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.kuky.ooas.wx.common.util.R;
import com.kuky.ooas.wx.config.SystemConstants;
import com.kuky.ooas.wx.config.shiro.JwtUtil;
import com.kuky.ooas.wx.controller.form.CheckinForm;
import com.kuky.ooas.wx.controller.form.SearchMonthCheckinForm;
import com.kuky.ooas.wx.exception.EmosException;
import com.kuky.ooas.wx.service.CheckinService;
import com.kuky.ooas.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Description: CheckinController
 * @Author Kuky
 * @Date: 2021/6/6 21:06
 * @Version 1.0
 */
@RequestMapping("/checkin")
@RestController
@Api("签到模块Web接口")
@Slf4j
public class CheckinController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @Value("${emos.image-folder}")
    private String imageFolder;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConstants constants;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户今天是否可以签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId, DateUtil.today());
        return R.ok(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinForm form, @RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传文件");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("必须提交JPG格式图片");
        } else {
            String path = imageFolder + "/" + fileName;
            try {
                file.transferTo(Paths.get(path));
               /* HashMap param = new HashMap();
                param.put("userId", userId);
                param.put("path", path);
                param.put("city", form.getCity());
                param.put("district", form.getDistrict());
                param.put("address", form.getAddress());
                param.put("country", form.getCountry());
                param.put("province", form.getProvince());*/
                checkinService.checkin(form, userId, path);
                return R.ok("签到成功");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new EmosException("图片保存错误");
            } finally {
                FileUtil.del(path);
            }
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传文件");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("必须提交JPG格式图片");
        } else {
            String path = imageFolder + "/" + fileName;
            try {
                file.transferTo(Paths.get(path));
                checkinService.createFaceModel(userId, path);
                return R.ok("人脸建模成功");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new EmosException("图片保存错误");
            } finally {
                FileUtil.del(path);
            }
        }
    }

    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public R searchTodayCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);

        //考勤开始时间和结束时间
        HashMap resultMap = checkinService.searchTodayCheckin(userId);
        resultMap.put("attendanceTime", constants.attendanceTime);
        resultMap.put("closingTime", constants.closingTime);
        long days = checkinService.searchCheckinDays(userId);
        resultMap.put("checkinDays", days);

        //判断日期是否在用户入职之前
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        //beginOfWeek()取得本周一；DateUtil.date()为当前的日期对象
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());
        //如果本周一在用户入职时间之前，则startDate为入职时间
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());
        HashMap param = new HashMap();
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        param.put("userId", userId);
        ArrayList<HashMap> list = checkinService.searchWeekCheckin(param);
        resultMap.put("weekCheckin", list);
        return R.ok().put("result", resultMap);
    }

    @PostMapping("/searchMonthCheckin")
    @ApiOperation("查询用户某月签到数据")
    public R searchMonthCheckin(@Valid @RequestBody SearchMonthCheckinForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        //查询入职日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        //把月份处理成双数字
        String month = form.getMonth() < 10 ? "0" + form.getMonth() : "" + form.getMonth();
        //某年某月的起始日期
        DateTime startDate = DateUtil.parse(form.getYear() + "-" + month + "-01");
        //如果查询的月份早于员工入职日期的月份就抛出异常
        //DateUtil.beginOfMonth(hiredate)返回入职日期当月的日期对象
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) {
            throw new EmosException("只能查询入职之后的考勤数据");
        }
        //如果查询月份与入职月份恰好是同月，本月考勤查询开始日期设置成入职日期
        //startDate>=hiredate
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        //某年某月的截止日期
        DateTime endDate = DateUtil.endOfMonth(startDate);

        HashMap param = new HashMap();
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        param.put("userId", userId);

        ArrayList<HashMap> list = checkinService.searchMonthCheckin(param);
        //统计月考勤数据
        int sum_1 = 0, sum_2 = 0, sum_3 = 0;
        for (HashMap<String, String> map : list) {
            String type = map.get("type");
            String status = map.get("status");
            if ("工作日".equals(type)) {
                if ("正常".equals(status)) {
                    sum_1++;
                } else if ("迟到".equals(status)) {
                    sum_2++;
                } else if ("缺勤".equals(status)) {
                    sum_3++;
                }
            }
        }
        return R.ok().put("list", list).put("sum_1", sum_1).put("sum_2", sum_2).put("sum_3", sum_3);
    }
}

