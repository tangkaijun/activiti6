package com.github.kjtang.activiti.config;


import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by kjtang on 2018/1/18.
 */
@Configuration
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public Docket customDocket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(apiInfo());
        docket.select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        docket.select().paths(PathSelectors.regex("/activiti6/api/.*")).build();
        return docket;
    }

    private ApiInfo apiInfo() {
       // Contact contact = new Contact("唐开军", "http://www.github.com/tangkaijun", "571856518@qq.com");
        return new ApiInfo("用户管理API",
                "API接口",    //小标题
                "0.0.1",        //版本
                "www.baidu.com",//termsOfServiceUrl
                null,
                "API接口",//链接显示文字
                "http://www.baidu.com"//网站链接
        );
    }

}
