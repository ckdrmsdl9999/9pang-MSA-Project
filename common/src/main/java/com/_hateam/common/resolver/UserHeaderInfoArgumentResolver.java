package com._hateam.common.resolver;


import com._hateam.common.annotation.UserHeader;
import com._hateam.common.dto.UserHeaderInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Enumeration;

@Component
public class UserHeaderInfoArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserHeaderInfo.class) &&
                parameter.hasParameterAnnotation(UserHeader.class);
    }

    @Override
    public UserHeaderInfo resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
        // 헤더 확인 (소문자만 시도)
        String userIdStr = webRequest.getHeader("x-user-id");  // 소문자로만 시도
        String userRole = webRequest.getHeader("x-user-role"); // 소문자로만 시도

        System.out.println("Resolver에서 읽은 값: x-user-id=" + userIdStr + ", x-user-role=" + userRole);

        UserHeaderInfo userInfo = new UserHeaderInfo();

        // userId 변환
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                userInfo.setUserId(Long.parseLong(userIdStr));
            } catch (NumberFormatException e) {
                System.out.println("숫자 변환 오류: " + e.getMessage());
            }
        }

        userInfo.setUserRole(userRole);
        System.out.println("생성된 UserInfo: " + userInfo);

        return userInfo;
    }







//    @Override
//    public UserHeaderInfo resolveArgument(MethodParameter parameter,
//                                          ModelAndViewContainer mavContainer,
//                                          NativeWebRequest webRequest,
//                                          WebDataBinderFactory binderFactory) {
//
//        String userIdStr = webRequest.getHeader("X-User-Id");
//        String userRole = webRequest.getHeader("X-User-Role");
//
//        UserHeaderInfo userInfo = new UserHeaderInfo();
//
//        // userId 변환
//        if (userIdStr != null && !userIdStr.isEmpty()) {
//            try {
//                userInfo.setUserId(Long.parseLong(userIdStr));
//            } catch (NumberFormatException e) {
//                // 로깅
//            }
//        }
//
//        userInfo.setUserRole(userRole);
//
//        return userInfo;
//    }
}
