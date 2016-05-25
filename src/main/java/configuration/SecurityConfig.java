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

    @Override
    public void configure(WebSecurity web) throws Exception {
        logger.debug("--------------WebSecurity initialize start--------");
        web.ignoring().antMatchers("/statics/**", "/**/*.html");
        logger.debug("--------------WebSecurity initialize end--------");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("--------------HttpSecurity initialize start--------");
        http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().authorizeRequests().antMatchers("/**").hasRole("admin");
        http.csrf().disable();
        logger.debug("--------------HttpSecurity initialize end--------");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        logger.debug("--------------AuthenticationManagerBuilder initialize start--------");
        auth.inMemoryAuthentication().withUser("user").password("user").roles("user");
        logger.debug("--------------AuthenticationManagerBuilder initialize end--------");
    }
}
