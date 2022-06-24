package com.kuky.ooas.wx.service;

import cn.hutool.json.JSONObject;
import com.kuky.ooas.wx.db.pojo.TbUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @Description: UserService
 * @Author Kuky
 * @Date: 2021/6/5 17:32
 * @Version 1.0
 */
public interface UserService {

    public int registerUser(String registerCode,String code,String nickname,String photo);

    public Set<String> searchUserPermissions(int userId);

    public Integer login(String code);

    public TbUser searchById(int userId);

    public String searchUserHiredate(int userId);

    public HashMap searchUserSummary(int userId);

    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    public ArrayList<HashMap> searchMembers(List param);

    public List<HashMap> selectUserPhotoAndName(List param);

    public String searchMemberEmail(int id);

    public void insertUser(HashMap param);

    public HashMap searchUserInfo(int userId);

    public int updateUserInfo(HashMap param);

    public void deleteUserById(int id);

    public JSONObject searchUserContactList();
}