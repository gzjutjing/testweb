package configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 只能描述controller 接口
 * rest api文档网页生成，具有立即修改立即生效的特点，维护文档和修改代码整合为一体
 * Created by jingjiong on 2017/1/19.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket creatApi() {
        return new Docket(DocumentationType.SWAGGER_12).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("测试文档").description("测试文档描述").version("1.0").build();
    }


}
