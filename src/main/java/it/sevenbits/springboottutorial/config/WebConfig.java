package it.sevenbits.springboottutorial.config;

import it.sevenbits.springboottutorial.web.utils.CustomHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private CsrfInterceptor csrfInterceptor;

    @Autowired
    private CustomHandlerInterceptor handlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(csrfInterceptor);
        registry.addInterceptor(handlerInterceptor);
    }
}
