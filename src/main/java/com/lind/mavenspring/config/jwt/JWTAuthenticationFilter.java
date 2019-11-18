package com.lind.mavenspring.config.jwt;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lind.mavenspring.exception.LoginFailLimitException;
import com.lind.mavenspring.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    /**
     * 用户token前缀key 单点登录使用
     */
    String USER_TOKEN = "XBOOT_USER_TOKEN:";
    private Boolean tokenRedis;
    private Integer tokenExpireTime;
    private StringRedisTemplate redisTemplate;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, Boolean tokenRedis, Integer tokenExpireTime, StringRedisTemplate redisTemplate) {
        super(authenticationManager);
        this.tokenRedis = tokenRedis;
        this.tokenExpireTime = tokenExpireTime;
        this.redisTemplate = redisTemplate;
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(Const.HEADER);
        if (StrUtil.isBlank(header)) {
            header = request.getParameter(Const.HEADER);
        }
        Boolean notValid = StrUtil.isBlank(header) || (!tokenRedis && !header.startsWith(Const.TOKEN_SPLIT));
        if (notValid) {
            chain.doFilter(request, response);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            e.toString();
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String header, HttpServletResponse response) {

        // 用户名
        String username = null;
        // 权限
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (tokenRedis) {
            // redis
            String v = redisTemplate.opsForValue().get(Const.TOKEN_PRE + header);
            if (StrUtil.isBlank(v)) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 401, "登录已失效，请重新登录"));
                return null;
            }
            TokenUser user = new Gson().fromJson(v, TokenUser.class);
            username = user.getUsername();
            for (String ga : user.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(ga));
            }
            if (tokenRedis && !user.getSaveLogin()) {
                // 若未保存登录状态重新设置失效时间
                redisTemplate.opsForValue().set(USER_TOKEN + username, v, tokenExpireTime, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(Const.TOKEN_PRE + header, v, tokenExpireTime, TimeUnit.MINUTES);
            }
        } else {
            // JWT
            try {
                // 解析token
                Claims claims = Jwts.parser()
                        .setSigningKey(Const.JWT_SIGN_KEY)
                        .parseClaimsJws(header.replace(Const.TOKEN_SPLIT, ""))
                        .getBody();

                //获取用户名
                username = claims.getSubject();
                //获取权限
                String authority = claims.get(Const.AUTHORITIES).toString();

                if (StrUtil.isNotBlank(authority)) {
                    List<String> list = new Gson().fromJson(authority, new TypeToken<List<String>>() {
                    }.getType());
                    for (String ga : list) {
                        authorities.add(new SimpleGrantedAuthority(ga));
                    }
                }
            } catch (LoginFailLimitException e) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 401, "登录已失效，请重新登录"));
            } catch (Exception e) {
                log.error(e.toString());
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "解析token错误"));
            }
        }

        if (StrUtil.isNotBlank(username)) {
            //Exrick踩坑提醒 此处password不能为null
            User principal = new User(username, "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
        }
        return null;
    }
}

