package com.kuky.ooas.wx.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: JwtUtil
 * @Author Kuky
 * @Date: 2021/6/4 17:50
 * @Version 1.0
 */
@Component
@Slf4j
public class JwtUtil {
    @Value("${emos.jwt.secret}")
    private String secret;

    @Value("${emos.jwt.expire}")
    private int expire;

    public String createToken(int userId){
        //计算过期日期
        Date date= DateUtil.offset(new Date(), DateField.DAY_OF_YEAR,5);
        //把秘钥封装成一个加密算法对象
        Algorithm algorithm=Algorithm.HMAC256(secret);
        //创建一个内部类的对象builder
        JWTCreator.Builder builder= JWT.create();
        String token=builder.withClaim("userId",userId).withExpiresAt(date).sign(algorithm);
        return token;
    }

    public int getUserId(String token){
        DecodedJWT jwt=JWT.decode(token);
        int userId=jwt.getClaim("userId").asInt();
        return userId;
    }

    public void verifierToken(String token){
        //通过秘钥创建算法对象
        Algorithm algorithm=Algorithm.HMAC256(secret);
        //创建验证对象
        JWTVerifier verifier=JWT.require(algorithm).build();
        verifier.verify(token);
    }
}