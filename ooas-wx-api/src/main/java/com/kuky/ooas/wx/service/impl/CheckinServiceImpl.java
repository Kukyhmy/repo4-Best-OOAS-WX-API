package com.kuky.ooas.wx.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.kuky.ooas.wx.config.SystemConstants;
import com.kuky.ooas.wx.controller.form.CheckinForm;
import com.kuky.ooas.wx.db.dao.*;
import com.kuky.ooas.wx.db.pojo.TbCheckin;
import com.kuky.ooas.wx.db.pojo.TbFaceModel;
import com.kuky.ooas.wx.exception.EmosException;
import com.kuky.ooas.wx.service.CheckinService;
import com.kuky.ooas.wx.task.EmailTask;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @Description: CheckinServiceImpl
 * @Author Kuky
 * @Date: 2021/6/6 20:03
 * @Version 1.0
 */
@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {
    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private TbHolidaysDao holidaysDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbCheckinDao checkinDao;

    @Autowired
    private TbUserDao tbUserDao;

    @Autowired
    private TbFaceModelDao tbFaceModelDao;

    @Autowired
    private TbCityDao tbCityDao;

    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Value("${emos.email.hr}")
    private String hrEmail;
    @Autowired
    private EmailTask emailTask;

    @Value("${emos.code}")
    private String code;

    @Override
    public String validCanCheckIn(int userId, String date) {
        boolean bool_1 = holidaysDao.searchTodayIsHolidays() != null ? true : false;
        boolean bool_2 = workdayDao.searchTodayIsWorkday() != null ? true : false;
        String type = "?????????";
        if (DateUtil.date().isWeekend()) {
            type = "?????????";
        }
        if (bool_1) {
            type = "?????????";
        } else if (bool_2) {
            type = "?????????";
        }

        if (type.equals("?????????")) {
            return "????????????????????????";
        } else {
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String end = DateUtil.today() + " " + systemConstants.attendanceEndTime;
            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);
            if (now.isBefore(attendanceStart)) {
                return "?????????????????????????????????";
            } else if (now.isAfter(attendanceEnd)) {
                return "?????????????????????????????????";
            } else {
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                boolean bool = checkinDao.haveCheckin(map) != null ? true : false;
                return bool ? "???????????????????????????????????????" : "????????????";
            }
        }
    }

    @Override
    public void checkin(CheckinForm form, int userId, String path) {
        //????????????
        Date d1 = DateUtil.date();  //????????????
        Date d2 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime);  //????????????
        Date d3 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime); //??????????????????
        int status = 1;
        if (d1.compareTo(d2) <= 0) {
            status = 1; // ????????????
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2; //??????
        } else {
            throw new EmosException("????????????????????????????????????");
        }
        //???????????????????????????????????? ??????????????????
        //??????
           //Date workingStartCheckingTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceStartTime);  //????????????????????????

        //??????
        //????????????????????????????????????
        String faceModel = tbFaceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("?????????????????????");
        } else {
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            request.form("code", code);
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                log.error("????????????????????????");
                throw new EmosException("????????????????????????");
            }
            String body = response.body();
            if ("?????????????????????".equals(body) || "???????????????????????????".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("??????????????????????????????");
            } else if ("True".equals(body)) {
                //???????????????????????????????????????????????????
                //????????????????????????
                int risk = 1;
                //??????????????????
                if (form.getCity() != null && form.getCity().length() > 0 && form.getDistrict() != null && form.getDistrict().length() > 0) {
                    String code = tbCityDao.searchCode(form.getCity());
                    //??????????????????
                    try {
                        String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + form.getDistrict();
                        Document document = Jsoup.connect(url).get();
                        Elements elements = document.getElementsByClass(" list-detail");
                        for (Element one : elements) {
                            String result = one.text().split(" ")[1];
//                            result = "?????????"; //Test
                            if ("?????????".equals(result)) {
                                risk = 3;
                                //??????????????????
                                HashMap<String, String> hashMap = tbUserDao.searchNameAndDept(userId);
                                String name = hashMap.get("name");
                                String deptName = hashMap.get("dept_name");
                                deptName = deptName != null ? deptName : "";
                                SimpleMailMessage mailMessage = new SimpleMailMessage();
                                mailMessage.setTo(hrEmail);
                                mailMessage.setSubject("??????" + name + "?????????????????????????????????");
                                mailMessage.setText(deptName + "??????" + name + "???" + DateUtil.format(new Date(), "yyyy???MM???dd???") + "??????" + form.getAddress() + "????????????????????????????????????????????????????????????????????????????????????");
                                emailTask.sendAsync(mailMessage);

                            } else if ("?????????".equals(result)) {
                                risk = risk < 2 ? 2 : risk;
                            }
                        }
                    } catch (IOException e) {
                        log.error("????????????", e);
                        throw new EmosException("????????????????????????");
                    }
                    //??????????????????
                    TbCheckin entity = new TbCheckin();
                    entity.setUserId(userId);
                    entity.setAddress(form.getAddress());
                    entity.setCountry(form.getCountry());
                    entity.setProvince(form.getProvince());
                    entity.setCity(form.getCity());
                    entity.setDistrict(form.getDistrict());
                    entity.setStatus((byte) status);
                    entity.setRisk(risk);
                    entity.setDate(DateUtil.today());
                    entity.setCreateTime(d1);
                    checkinDao.insert(entity);
                }
            }
        }
    }

    @Override
    public void createFaceModel(int userId, String path) {
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo", FileUtil.file(path));
        request.form("code", code);
        HttpResponse response = request.execute();
        String body = response.body();
        if ("?????????????????????".equals(body) || "???????????????????????????".equals(body)) {
            throw new EmosException(body);
        } else {
            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            tbFaceModelDao.insert(entity);
        }
    }

    @Override
    public HashMap searchTodayCheckin(int userId) {
        HashMap map = checkinDao.searchTodayCheckin(userId);
        return map;
    }

    @Override
    public long searchCheckinDays(int userId) {
        long days = checkinDao.searchCheckinDays(userId);
        return days;
    }

    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap param) {
        ArrayList<HashMap> checkinList = checkinDao.searchWeekCheckin(param);
        ArrayList<String> holidaysList = holidaysDao.searchHolidaysInRange(param);
        ArrayList<String> workdayList = workdayDao.searchWorkdayInRange(param);

        DateTime startDate = DateUtil.parseDate(param.get("startDate").toString());
        DateTime endDate = DateUtil.parseDate(param.get("endDate").toString());
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        ArrayList<HashMap> list = new ArrayList();
        range.forEach(one -> {
            String date = one.toString("yyyy-MM-dd");
            //??????????????????????????????????????????
            String type = "?????????";
            if (one.isWeekend()) {
                type = "?????????";
            }
            if (holidaysList != null && holidaysList.contains(date)) {
                type = "?????????";
            } else if (workdayList != null && workdayList.contains(date)) {
                type = "?????????";
            }

            String status = "";
            if (type.equals("?????????") && DateUtil.compare(one, DateUtil.date()) <= 0) {
                status = "??????";
                boolean flag = false;
                for (HashMap<String, String> map : checkinList) {
                    if (map.containsValue(date)) {
                        status = map.get("status");
                        flag = true;
                        break;
                    }
                }
                DateTime endTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);
                String today = DateUtil.today();
                if (date.equals(today) && DateUtil.date().isBefore(endTime) && flag == false) {
                    status = "";
                }
            }
            HashMap map = new HashMap();
            map.put("date", date);
            map.put("status", status);
            map.put("type", type);
            map.put("day", one.dayOfWeekEnum().toChinese("???"));
            list.add(map);
        });
        return list;
    }

    @Override
    public ArrayList<HashMap> searchMonthCheckin(HashMap param) {
        return this.searchWeekCheckin(param);
    }

}
