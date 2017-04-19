package com.yc.room1000.engine.config;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefisher.core.spring.config.swagger.SwaggerConfig;


/**
 * 
 * Description: WebSpringMVCConfig
 *  
 * Created on 2017年4月19日 
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@Configuration
@ComponentScan(basePackages = {
        "com.yc.room1000.**.controller" }, 
    includeFilters = @Filter(value = Controller.class))
@Import(SwaggerConfig.class)
@EnableWebMvc
@EnableAspectJAutoProxy
public class WebSpringMVCConfig extends WebMvcConfigurerAdapter {
    
    /**
     * 
     * Description: 消息转换器配置
     * 
     * @author jinyanan
     *
     * @param converters converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Charset charset = Charset.forName("UTF-8");
        
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper om = jacksonConverter.getObjectMapper();
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        om.setLocale(Locale.CHINA);
        om.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(new MediaType("text", "html", charset));
        mediaTypeList.add(new MediaType("application", "json", charset));
        jacksonConverter.setSupportedMediaTypes(mediaTypeList);
        
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(charset);
        stringConverter.setWriteAcceptCharset(false);
        
        //保持以下顺序
        //"application/json" "application/*+json"
        converters.add(jacksonConverter);
        //"application/xml" "text/xml" "application/*+xml"
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        //"application/xml" "text/xml" "application/*+xml"
        converters.add(new SourceHttpMessageConverter<>());
        //"application/x-www-form-urlencoded" "multipart/form-data"
        converters.add(new AllEncompassingFormHttpMessageConverter());
        //"application/octet-stream" "*/*"
        converters.add(new ByteArrayHttpMessageConverter());
        //"text/plain" "*/*"
        converters.add(stringConverter);
        //"*/*"
        converters.add(new ResourceHttpMessageConverter());
    }
    
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger/**").addResourceLocations("/WEB-INF/swagger/");
    }
    
    /**
     * 
     * Description: 设置springdispatch默认名字为default
     * 
     * @author jinyanan
     *
     * @param configurer configurer
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
    /**
     * 
     * Description: 
     * 
     * @author jinyanan
     *
     * @return CommonsMultipartResolver
     */
    @Bean
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(10485760);
        commonsMultipartResolver.setMaxInMemorySize(4096);
        return commonsMultipartResolver;
    }
    
}
