package com.kuky.ooas.wx.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kuky.ooas.wx.db.dao.*;
import com.kuky.ooas.wx.db.dao.*;
import com.kuky.ooas.wx.db.pojo.MessageEntity;
import com.kuky.ooas.wx.db.pojo.TbUser;
import com.kuky.ooas.wx.exception.EmosException;
import com.kuky.ooas.wx.service.UserService;
import com.kuky.ooas.wx.task.ActiveCodeTask;
import com.kuky.ooas.wx.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description: UserServiceImpl
 * @Author Kuky
 * @Date: 2021/6/5 17:33
 * @Version 1.0
 */
@Service
@Scope("prototype")
@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private MessageTask messageTask;

    @Autowired
    private TbDeptDao deptDao;

    @Autowired
    private ActiveCodeTask activeCodeTask;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbCheckinDao checkinDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MessageRefDao messageRefDao;

    @Autowired
    private TbFaceModelDao faceModelDao;

    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;
    }

    @Override
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions = userDao.searchUserPermissions(userId);
        return permissions;
    }

    @Override
    public Integer login(String code) {
        String openId = getOpenId(code);
        Integer id = userDao.searchIdByOpenId(openId);
        if (id == null) {
            throw new EmosException("帐户不存在");
        }
        // 从消息队列中接收消息，转移到消息表
        //     messageTask.receiveAysnc(id+"");
        return id;
    }

    @Override
    public TbUser searchById(int userId) {
        TbUser user = userDao.searchById(userId);
        return user;
    }

    @Override
    public String searchUserHiredate(int userId) {
        return userDao.searchUserHiredate(userId);
    }

    @Override
    public HashMap searchUserSummary(int userId) {
        return userDao.searchUserSummary(userId);
    }

    public ArrayList<HashMap> searchUserGroupByDept(String keyword) {
        ArrayList<HashMap> list_1 = deptDao.searchDeptMembers(keyword);
        ArrayList<HashMap> list_2 = userDao.searchUserGroupByDept(keyword);
        for (HashMap map_1 : list_1) {
            long deptId = (Long) map_1.get("id");
            ArrayList members = new ArrayList();
            for (HashMap map_2 : list_2) {
                long id = (Long) map_2.get("deptId");
                if (deptId == id) {
                    members.add(map_2);
                }
            }
            map_1.put("members", members);
        }
        return list_1;
    }

    @Override
    public ArrayList<HashMap> searchMembers(List param) {
        ArrayList<HashMap> list = userDao.searchMembers(param);
        return list;
    }


    @Override
    public List<HashMap> selectUserPhotoAndName(List param) {
        List<HashMap> list = userDao.selectUserPhotoAndName(param);
        return list;
    }

    @Override
    public String searchMemberEmail(int id) {
        String email = userDao.searchMemberEmail(id);
        return email;
    }


    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        if (registerCode.equals("000000")) {
            boolean bool = userDao.haveRootUser();
            if (!bool) {
                String openId = getOpenId(code);
                HashMap param = new HashMap();
                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userDao.insert(param);
                int id = userDao.searchIdByOpenId(openId);

                MessageEntity entity = new MessageEntity();
                entity.setSenderId(0);
                entity.setSenderName("系统消息");
                entity.setUuid(IdUtil.simpleUUID());
                entity.setMsg("欢迎您注册成为超级管理员，请及时更新你的员工个人信息。");
                entity.setSendTime(new Date());
                messageTask.sendAsync(id + "", entity);
                return id;
            } else {
                throw new EmosException("无法绑定超级管理员账号");
            }
        } else if (!redisTemplate.hasKey(registerCode)) {
            //判断邀请码是否有效
            throw new EmosException("不存在这个激活码");
        } else {
            int userId = Integer.parseInt(redisTemplate.opsForValue().get(registerCode).toString());
            //把当前用户绑定到ROOT帐户
            TbUser entity = new TbUser();
            String openId = getOpenId(code);
            HashMap param = new HashMap();
            param.put("openId", openId);
            param.put("nickname", nickname);
            param.put("photo", photo);
            param.put("userId", userId);
            int row = userDao.activeUserAccount(param);
            if (row != 1) {
                throw new EmosException("账号激活失败");
            }
            redisTemplate.delete(registerCode);
            return userId;
        }
    }

    @Override
    public void insertUser(HashMap param) {
        //保存记录
        int row = userDao.insert(param);
        if (row == 1) {
            String email = (String) param.get("email");
            //根据Email查找新添加用户的主键值
            int userId = userDao.searchUserIdByEmail(email);
            //生成激活码，并且用邮件发送
            activeCodeTask.sendActiveCodeAsync(userId, email);
        } else {
            throw new EmosException("员工数据添加失败");
        }
    }

    @Override
    public HashMap searchUserInfo(int userId) {
        HashMap map = userDao.searchUserInfo(userId);
        return map;
    }

    @Override
    public int updateUserInfo(HashMap param) {
        //更新员工记录
        int rows = userDao.updateUserInfo(param);
        //更新成功就发送消息通知
        if (rows == 1) {
            Integer userId = (Integer) param.get("userId");
            String msg = "你的个人资料已经被成功修改";
            MessageEntity entity = new MessageEntity();
            entity.setSenderId(0);  //系统自动发出
            entity.setSenderPhoto("../../static/system.jpg");
            entity.setSenderName("系统消息");
            entity.setMsg(msg);
            entity.setSendTime(new Date());
            messageTask.sendAsync(userId.toString(), entity);
        }
        return rows;
    }

    @Override
    public void deleteUserById(int id) {
        int row = userDao.deleteUserById(id); //删除员工数据
        if (row != 1) {
            throw new EmosException("删除员工失败");
        }
        checkinDao.deleteUserCheckin(id);
        messageDao.deleteUserMessage(id);
        messageRefDao.deleteUserMessageRef(id);
        faceModelDao.deleteFaceModel(id);
        messageTask.deleteQueue(id + "");
    }

    /**
     * @Description:因为要从用户的姓名提取出拼音，然后在通讯录上面按照拼音排序，需要用一个汉语拼音的依赖库PinyinUtil
     * @param
     * @return: cn.hutool.json.JSONObject
     * @Author: Kuky
     * @Date: 2022/6/13 19:54
     */
    @Override
    public JSONObject searchUserContactList() {
        ArrayList<HashMap> list = userDao.searchUserContactList();
        String letter = null;
        JSONObject json = new JSONObject(true);
        JSONArray array = null;
        for (HashMap<String, String> map : list) {
            String name = map.get("name");
            String firstLetter = PinyinUtil.getPinyin(name).charAt(0) + "";
            firstLetter = firstLetter.toUpperCase();
            if (letter == null || !letter.equals(firstLetter)) {
                letter = firstLetter;
                array = new JSONArray();
                json.set(letter, array);
            }
            array.put(map);
        }
        return json;
    }
}