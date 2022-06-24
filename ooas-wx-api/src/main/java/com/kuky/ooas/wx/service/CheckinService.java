package com.kuky.ooas.wx.service;

import com.kuky.ooas.wx.controller.form.CheckinForm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Description: CheckinService
 * @Author Kuky
 * @Date: 2021/6/6 20:02
 * @Version 1.0
 */
public interface CheckinService {
    public String validCanCheckIn(int userId, String date);

    public void checkin(CheckinForm form, int userId, String path);

    public void createFaceModel(int userId, String path);

    public HashMap searchTodayCheckin(int userId);

    public long searchCheckinDays(int userId);

    public ArrayList<HashMap> searchWeekCheckin(HashMap param);

    public ArrayList<HashMap> searchMonthCheckin(HashMap param);
}
