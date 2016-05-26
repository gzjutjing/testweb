package configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by admin on 2016/5/25.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 3个之中最后加载
     * 如果加载ignoring()，可以不用登录即可直接访问资源
     * ignoring()可以antMatchers多个
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        logger.debug("--------------WebSecurity initialize start--------");
        //web.ignoring().antMatchers("/statics/**");
        logger.debug("--------------WebSecurity initialize end--------");
    }

    /**
     * 3个之中第二个加载
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("--------------HttpSecurity initialize start--------");
        http.authorizeRequests().antMatchers("/**").hasRole("admin");
        //开启默认登录页面
        http.formLogin();
        //authenticated先后顺序不同会完全不同，如果放最前面会代表所有的都是已经认证的，放后面则会先匹配前面的，其它的不认证
        http.authorizeRequests().anyRequest().authenticated();
        http.csrf().disable();
        logger.debug("--------------HttpSecurity initialize end--------");
    }

    /**
     * 3个之中最先加载
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        logger.debug("--------------AuthenticationManagerBuilder initialize start--------");
        auth.inMemoryAuthentication().withUser("user").password("user").roles("user");
        logger.debug("--------------AuthenticationManagerBuilder initialize end--------");
    }
}
