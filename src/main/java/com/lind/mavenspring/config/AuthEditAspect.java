package com.lind.mavenspring.config;

import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 建立一个AOP拦截器,可以拦截某个注解.
 */
@Component
@Aspect
public class AuthEditAspect {
    @Around("@annotation(authEdit)")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp, AuthEdit authEdit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object id = StrUtil.isBlank(request.getParameter("id")) ?
                request.getParameter("counterpartId") : request.getParameter("id");
        if (StrUtil.isEmptyIfStr(id)) {
            return pjp.proceed();
        } else {
            String value = authEdit.value();
            if (StrUtil.equals(value, "admin")) {
                return pjp.proceed();
            }
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();

            writer.write("{\"message\":\"您没有操作权限\"}");
            writer.flush();

        }
        return null;
    }
}
