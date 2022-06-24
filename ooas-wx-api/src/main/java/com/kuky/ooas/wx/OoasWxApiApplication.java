package com.kuky.ooas.wx;

import cn.hutool.core.util.StrUtil;
import com.kuky.ooas.wx.config.SystemConstants;
import com.kuky.ooas.wx.db.dao.SysConfigDao;
import com.kuky.ooas.wx.db.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
@EnableScheduling
@EnableAsync
@SpringBootApplication
@ServletComponentScan
@Slf4j
public class OoasWxApiApplication {
    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private SystemConstants constants;

    @Value("${emos.image-folder}")
    private String imageFolder;

    public static void main(String[] args) {
        SpringApplication.run(OoasWxApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        List<SysConfig> list = sysConfigDao.selectAllParam();
        list.forEach(one -> {
            String key = one.getParamKey();
            key = StrUtil.toCamelCase(key);
            String value = one.getParamValue();
            try {
                Field field = constants.getClass().getDeclaredField(key);
                field.set(constants, value);

            } catch (Exception e) {
                log.error("执行异常", e);
            }
        });
        new File(imageFolder).mkdirs();
    }
}

