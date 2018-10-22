package com.github.kjtang.activiti.config;

import com.github.kjtang.activiti.interceptor.ResponseInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.util.StreamUtils.*;

/**
 * Created by kjtang on 2018/10/19.
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ResponseInterceptor());
        super.addInterceptors(registry);
    }

    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        return new MappingJackson2HttpMessageConverter(){
            @Override
            protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
                if(object instanceof String){
                    Charset charset = this.getDefaultCharset();
                    copy((String)object, charset, outputMessage.getBody());
                }else{
                    super.writeInternal(object, type, outputMessage);
                }
            }
        };
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = mappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(new LinkedList<MediaType>(){{
            add(MediaType.TEXT_HTML);
            add(MediaType.APPLICATION_JSON_UTF8);
        }});
        converters.add(new StringHttpMessageConverter());
        converters.add(converter);
    }

}
