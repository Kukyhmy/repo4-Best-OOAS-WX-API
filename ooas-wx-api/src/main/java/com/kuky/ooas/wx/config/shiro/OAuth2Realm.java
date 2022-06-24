package com.kuky.ooas.wx.config.shiro;

import com.kuky.ooas.wx.db.pojo.TbUser;
import com.kuky.ooas.wx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Description: OAuth2Realm
 * @Author Kuky
 * @Date: 2021/6/4 18:21
 * @Version 1.0
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserService userService;

        @Override
        public boolean supports(AuthenticationToken token) {
            return token instanceof OAuth2Token;
        }

        /**
         * 授权(验证权限时调用)
         */
        @Override
        protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
            TbUser user = (TbUser) principals.getPrimaryPrincipal();
            // 查询用户的权限列表
            Set<String> permissionsSet = userService.searchUserPermissions(user.getId());
            // 把权限列表添加到info对象中，保存权限列表
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.setStringPermissions(permissionsSet);
            return info;
        }

        /**
         * 认证(验证登录时调用)
         * 认证方法返回认证对象SimpleAuthenticationInfo（用户信息，Token字符串，Realm类的名字）
         */
        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
            //TODO 从令牌中获取userId，然后检测该账户是否被冻结。
            String accessToken = (String) token.getPrincipal();
            Integer userId = jwtUtil.getUserId(accessToken);
            TbUser tbUser = userService.searchById(userId);
            if (tbUser==null) {
                //status==2 代表用户已离职
                throw new LockedAccountException("账号已被锁定，请联系管理员");
            }
            //往info对象中添加用户信息、Token字符串
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(tbUser,accessToken,getName());
            return info;
        }
    }
