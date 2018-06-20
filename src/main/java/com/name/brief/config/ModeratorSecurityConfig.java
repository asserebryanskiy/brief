package com.name.brief.config;

import com.name.brief.service.UserService;
import com.name.brief.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Collection;

@Configuration
@Order(1)
public class ModeratorSecurityConfig extends WebSecurityConfigurerAdapter {
    public static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserService userService;

    @Autowired
    public ModeratorSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requestMatchers()
                .antMatchers("/moderator/**", "/admin/**", "/login/admin", "/logout/admin")
                .and()
            .authorizeRequests()
                .antMatchers("/moderator/**").hasRole("MODERATOR")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .and()
            .formLogin()
                .loginPage("/login/admin")
                .loginProcessingUrl("/login/admin")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .and()
            .logout()
                .logoutUrl("/logout/admin")
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
            .rememberMe()
                .useSecureCookie(false)
                .tokenValiditySeconds(86940)
                .alwaysRemember(true);

            /*http.sessionManagement()
                    .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                        .maxSessionsPreventsLogin(true)
                        .expiredUrl("/login/admin");*/
    }

        /*@Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }*/

    @Bean
    public EvaluationContextExtension securityExtension() {
        return new EvaluationContextExtensionSupport() {
            @Override
            public String getExtensionId() {
                return "security";
            }

            @Override
            public Object getRootObject() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return new SecurityExpressionRoot(authentication) {};
            }
        };
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            request.getSession().setAttribute("flash",
                    new FlashMessage("Разлогинивание успешно", FlashMessage.Status.SUCCESS));

            response.sendRedirect("/login/admin");
        };
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            request.getSession().setAttribute("flash",
                    new FlashMessage("Неправильное имя пользователя/пароль", FlashMessage.Status.FAILURE));

            response.sendRedirect("/login/admin?error=true");
        };
    }

    private AuthenticationSuccessHandler successHandler() {
        return ((request, response, authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities.contains(new SimpleGrantedAuthority("ROLE_MODERATOR")))
                response.sendRedirect("/moderator");
            else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
                response.sendRedirect("/admin");
            else {
                request.getSession().setAttribute("flash",
                        new FlashMessage("No roles found", FlashMessage.Status.FAILURE));
                response.sendRedirect("/login/admin");
            }
        });
    }
}
