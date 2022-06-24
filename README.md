# Best-OOAS-WX-API(好安逸在线办公自动化微信小程序)
基于SpringBoot+SpringMVC+Mybatis+Shiro+JWT等技术构建的在线协同办公微信小程序的后端项目。
## 项目描述
好安逸在线协同办公是一款供企业员工使用的在线协同办公微信小程序。主要功能包括人脸考勤签到，GPS坐标定位，分析签到地
址是否为疫情高⻛险地区，智能疫情⻛险管控；会议管理，多人在线语音+视频会议(目前此功能需要企业资质暂不对外开放，经借用企业账号已调试通过)；云文档、在线审批、通讯录、公告通知、部门管理、员工管理等企业常用功能。

## 功能模块图
各个模块的功能介绍：
![功能模块图](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/FunctionalModuleDiagram.png)

---
### 前端技术栈
- Vue
- uni-app
- JavaScript
- Less

### 后端技术栈
- SpringBoot SpringMVC
- Mybatis
- Shiro
- JWT 
- JSOUP (解析HTML)
- RabbitMQ
- MongoDB
- Redis

---

## 模块设计
### 消息通知模块设计
![消息通知模块设计](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/xiaoxiDesign.png)

### 认证与授权设计
将JWT和Shiro框架对接起来，这样Shiro框架就会拦截所有的Http请求，然后验证请求提交的Token是否有效。
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/JWT.png)
Token缓存方案：把Token缓存到Redis，然后设置Redis里面缓存的Token过期时间为正常Token的1倍,然后根据情况刷新Token的过期时间；当Token失效，但是缓存还存在时需要刷新token；
服务端刷新Token过期时间，其实就是生成一个新的Token给客户端，往响应里面添加token，客户端那边判断每次Ajax响应里面是否包含Token，如果包含，就把Token保存起来。
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/tokenReflesh.png)

### 人脸签到模块设计
人脸识别的签到功能：
用户在页面点击签到，通过微信小程序的接口方法得到地理坐标，利用腾讯位置服务的JS调用接口把地理坐标转换成地址信息；通过本地宝这个网站得出该地区是新冠疫情的高风险还是低风险地区，URL地址传参的方式获取本地宝返回的HTML响应，从HTML中解析出风险等级信息。如果员工是在疫情高风险地区签到的，Emos系统会立即向公司人事部门发送告警邮件。
在Docker中安装人脸识别镜像，运行人脸识别程序，后端调用接口创建人脸模型得到用户的人脸模型数据保存至数据库，调用接口比对人脸模型，实现签到业务。
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/facecheckin.png)

### 会议审批流程设计
创建会议记录时，开启审批工作流，创建工作流实例；当部门经理或者总经理角色在页面点击审批按钮后，向工作流项目发起会议审批请求，无论会议审批通过还是不通过，workflow项目都会向ooas-wx-api工程发送审批结果通知。
在页面可根据工作流项目查询出某个审批人的待审批/已审批的工作流实例(会议列表)
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/meetingaproval.png)

---
## 开发环境
* JDK : Oracle JDK 15
* MySQL：版本为8.0；因为数据表中的字段使用了JSON格式，所以MySQL版本至少要是5.7以上的，低版本的MySQL不支持JSON。
* MongoDB： 5.0；Windows环境下双击mongo.bat文件启动数据库，启动Navicat，选择创建MongoDB连接。
* Redis：3.0
* Maven: 3.8.5
* Intelli IDEA: Lombok插件
* 微信小程序开发工具
* HBuilderX工具：开发UNI-APP框架的移动端项目的必然首选
* VirtualBox虚拟机工具：安装CentOS7系统

---
## Best-OOAS项目(前端+后端)截图
### 首页登录
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/login.png)

### 企业内部员工注册页面
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/register.png)

### 办公系统首页
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/index.png)
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/my.png)
### 消息模块
![消息列表页](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/messageList.png)
![删除某条消息](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/deleteMessage.png)

### 考勤模块
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/takephotocheckin.png)
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/kaoqing.png)

### 在线视频会议模块
系统首页展示当月员工的哪些日期有会议，通过日历控件展示，日历下方显示当月员工需要参加的会议列表(带翻页)，默认情况下会议列表的内容取决于日历的月份，日历切换到哪月，下方的会议列表就会发生变化。
![首页展示用户当月参加的会议](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/indexMeetings.png)

- 会议列表页
   - 前端排版设计:相同日期的会议要合并到同一个小列表中
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/meetingList.png)
- 会议详情页
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/meetingDetail.png)

### 在线审批模块
工作流项目与ooas-wx-api项目形成分布式调用关系，打包成JAR文件部署到docker中，方便部署调试。
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/daishenpi.png)
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/yishenpi.png)

### 权限管理模块
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/quanxianmanagement.png)

### 部门管理模块
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/deptmanagement.png)

### 员工管理模块
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/yuangongmanagement.png)

### 通讯录
![](http://rdxq3ue7m.hn-bkt.clouddn.com/readme/run/tongxunlu.png)

---

## 登录系统
用户必须完成注册才能登入系统，注册时填写的激活码由管理员添加员工后，生成激活码，并且用邮件发送到员工邮箱中。
## 数据库文件
- *数据库文件目录为`static-files/emos1.sql`*
- *工作流项目jar为`static-files/emos-workflow.jar`*

  emos1：
* 介绍
  * act
    * act开头的表，表示activity7工作流相关的内容
  * sys
    * sys开头的表，表示系统常量数据相关的内容
  * tb
    * tb开头的表，表示业务相关的内容