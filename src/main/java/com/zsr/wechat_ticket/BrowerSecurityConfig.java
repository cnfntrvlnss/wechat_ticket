package com.zsr.wechat_ticket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Configuration
public class BrowerSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/test/**");
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()        // 定义哪些URL需要被保护、哪些不需要被保护
                .anyRequest()               // 任何请求,登录后可以访问
                .authenticated()
                .and()
                .formLogin().defaultSuccessUrl("/admin/main")
                .and()
                .csrf().disable();
    }
    
    
    @Component
    public class MyUserDetailsService implements UserDetailsService {

        private Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            logger.info("用户的用户名: {}", username);
            // TODO 根据用户名，查找到对应的密码，与权限
            
            SecurityUser user = new SecurityUser(username, "123456", AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
            return user;
      }
    }


}
