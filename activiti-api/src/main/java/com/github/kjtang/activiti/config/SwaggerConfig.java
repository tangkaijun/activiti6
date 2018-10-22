package com.github.kjtang.activiti.config;


import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
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

    private static String apiTitle = "Activiti6-RESTFful APIS";

    private static String version = "1.0.0";

    @Bean
    public Docket customDocket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(apiInfo());
        docket.select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        docket.select().paths(PathSelectors.regex("/activiti6/.*")).build();
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(apiTitle)
                .version(version)
                .license("详情请查看swagger官网api")
                .licenseUrl("https://github.com/OAI/OpenAPI-Specification/blob/master/versions/1.2.md#524-parameter-object")
                .build();
    }

}
