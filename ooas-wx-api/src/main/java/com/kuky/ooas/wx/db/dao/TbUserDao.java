package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Mapper
public interface TbUserDao {

    public boolean haveRootUser();

    public int insert(HashMap param);

    public Integer searchIdByOpenId(String openId);

    public Set<String> searchUserPermissions(int userId);

    public TbUser searchById(int userId);

    public HashMap searchNameAndDept(int userId);

    public String searchUserHiredate(int userId);

    public HashMap searchUserSummary(int userId);

    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    public ArrayList<HashMap> searchMembers(List param);

    public HashMap searchUserInfo(int userId);

    public int searchDeptManagerId(int id);

    public int searchGmId();

    public List<HashMap> selectUserPhotoAndName(List param);

    public String searchMemberEmail(int id);


    public long searchUserCountInDept(int deptId);

    public int searchUserIdByEmail(String email);

    public int activeUserAccount(HashMap param);
    public int updateUserInfo(HashMap param);

    public int deleteUserById(int id);

    public ArrayList<HashMap> searchUserContactList();
}