package it.sevenbits.springboottutorial.web.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class CustomHandlerInterceptor extends HandlerInterceptorAdapter {
    static final String SECURITY_SERVICE_NAME = "security";

    @Autowired
    UserResolver userResolver;

    @Override
    public void postHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        ModelAndView mav
    ) throws Exception {
        mav.addObject(SECURITY_SERVICE_NAME, userResolver);
    }
}
