package configuration;

import com.test.mapper.IUserSecurityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by admin on 2016/5/25.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    @Qualifier("securityUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private IUserSecurityDao userSecurityDao;

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/org/springframework/security/messages_zh_CN");
        return messageSource;
    }

    /**
     * 3个之中最后加载
     * 如果加载ignoring()，可以不用登录即可直接访问资源
     * ignoring()可以antMatchers多个
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        logger.debug("--------------WebSecurity initialize start--------");
        web.ignoring().antMatchers("/statics/**", "/*.ico");
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
        http.authorizeRequests()
                //.antMatchers("/").access("hasRole('ROLE_USER')")
                //.antMatchers("/mock").access("hasRole('ROLE_ADMIN')")
                //.accessDecisionManager(accessDecisionManager())
                .expressionHandler(webSecurityExpressionHandler());
        //
        http.authorizeRequests().antMatchers("/error").permitAll().anyRequest().authenticated();
        //开启默认登录页面
        http.formLogin().loginPage("/login").failureUrl("/login?error=1").failureForwardUrl("/login?error=1").permitAll()
                .loginProcessingUrl("/login_check").usernameParameter("username").passwordParameter("pwd").permitAll()
                .defaultSuccessUrl("/").and().httpBasic()
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))//csrf都是post提交，从新定义后取消了postget限制
                .logoutSuccessUrl("/login").logoutSuccessHandler(new SimpleUrlLogoutSuccessHandler()).permitAll();//SimpleUrlLogoutSuccessHandler默认url跳转
        //authenticated先后顺序不同会完全不同，如果放最前面会代表所有的都是已经认证的，放后面则会先匹配前面的，其它的不认证
        //http.authorizeRequests().anyRequest().authenticated();

        //http.csrf().disable();//默认开启
        http.exceptionHandling().accessDeniedPage("/accessDeny");
        //session
        http.sessionManagement().sessionFixation().changeSessionId().maximumSessions(1).expiredUrl("/");
        //remember key默认SpringSecured,有效期默认2周
        http.rememberMe().key("aaaaaaaaaaaa").tokenValiditySeconds(365 * 24 * 60 * 60)//秒
                .rememberMeParameter("remember-me").rememberMeCookieName("__rmbm__");
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
        //auth.inMemoryAuthentication().withUser("user").password("user").roles("user");
        auth.userDetailsService(userDetailsService).passwordEncoder(new Md5PasswordEncoder());
        logger.debug("--------------AuthenticationManagerBuilder initialize end--------");
    }

    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new RoleVoter());
        //decisionVoters.add(new WebExpressionVoter());
        //decisionVoters.add(webExpressionVoter());
        AffirmativeBased affirmativeBased = new AffirmativeBased(decisionVoters);
        return affirmativeBased;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        return webSecurityExpressionHandler;
    }

    @Bean
    public WebExpressionVoter webExpressionVoter() {
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(webSecurityExpressionHandler());
        return webExpressionVoter;
    }

    @Bean
    public SecurityMetadataSource securityMetadataSource() {
        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();
        Collection<ConfigAttribute> attsAdmin = new ArrayList<>();
        ConfigAttribute configAttributeAdmin = new org.springframework.security.access.SecurityConfig("/");
        attsAdmin.add(configAttributeAdmin);
        configAttributeAdmin = new org.springframework.security.access.SecurityConfig("/mock");
        attsAdmin.add(configAttributeAdmin);
        configAttributeAdmin = new org.springframework.security.access.SecurityConfig("/mock1");
        attsAdmin.add(configAttributeAdmin);
        RequestMatcher requestMatcherAdmin = new AntPathRequestMatcher("ROLE_ADMIN");
        requestMap.put(requestMatcherAdmin, attsAdmin);

        RequestMatcher requestMatcherUser = new AntPathRequestMatcher("ROLE_USER");
        Collection<ConfigAttribute> attsUser = new ArrayList<>();
        ConfigAttribute configAttributeUser = new org.springframework.security.access.SecurityConfig("/");
        attsUser.add(configAttributeUser);
        configAttributeUser = new org.springframework.security.access.SecurityConfig("/login");
        attsUser.add(configAttributeUser);
        requestMap.put(requestMatcherUser, attsUser);

        DefaultFilterInvocationSecurityMetadataSource metadataSource = new DefaultFilterInvocationSecurityMetadataSource(requestMap);
        return metadataSource;
    }
}
