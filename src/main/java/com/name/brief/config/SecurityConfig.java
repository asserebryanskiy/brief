package com.name.brief.config;

import com.name.brief.config.authentication.PlayerAuthenticationFilter;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.service.PlayerService;
import com.name.brief.service.UserService;
import com.name.brief.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    @Profile("heroku")
    public Filter httpsEnforcerFilter(){
        return new HttpsEnforcer();
    }

    @Configuration
    @Order(1)
    public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {
        private final UserService userService;

        @Autowired
        public AdminSecurityConfig(UserService userService) {
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
                .and().formLogin()
                    .loginPage("/login/admin")
                    .loginProcessingUrl("/login/admin")
                    .successHandler(successHandler())
                    .failureHandler(failureHandler())
                .and().logout()
                    .logoutUrl("/logout/admin")
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                .and().rememberMe()
                    .useSecureCookie(true)
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

    @Configuration
    @Order(2)
    public static class PlayerSecurityConfig extends WebSecurityConfigurerAdapter {

        private final PlayerService playerService;
        private final GameSessionService gameSessionService;
        private final PlayerAuthenticationService playerAuthenticationService;
        private final MessageSource messageSource;

        @Autowired
        public PlayerSecurityConfig(PlayerService playerService,
                                    GameSessionService gameSessionService,
                                    PlayerAuthenticationService playerAuthenticationService,
                                    MessageSource messageSource) {
            this.playerService = playerService;
            this.gameSessionService = gameSessionService;
            this.playerAuthenticationService = playerAuthenticationService;
            this.messageSource = messageSource;
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/assets/**", "/webjars/**");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(playerService);
        }

        @Bean
        public Filter playerAuthenticationFilter() throws Exception {
            PlayerAuthenticationFilter filter = new PlayerAuthenticationFilter(gameSessionService, playerAuthenticationService);
            filter.setAuthenticationManager(authenticationManager());
            filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login","POST"));

            // success authentication handler
            filter.setAuthenticationSuccessHandler(((request, response, authentication) ->
                    response.sendRedirect("/game")));

            // failure authentication handler
            filter.setAuthenticationFailureHandler((request, response, exception) ->
                    response.sendRedirect("/"));

            // configure session authentication strategies
            List<SessionAuthenticationStrategy> strategies = new ArrayList<>(6);
            SessionRegistry sessionRegistry = sessionRegistry();

            // set session registry also to playerAuthenticationService to give it an opportunity
            // to programmatically logout players
            playerAuthenticationService.setSessionRegistry(sessionRegistry);
            strategies.add(new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry));
            strategies.add(new SessionFixationProtectionStrategy());
            strategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry));
            filter.setSessionAuthenticationStrategy(
                    new CompositeSessionAuthenticationStrategy(strategies));

            return filter;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                    .antMatchers("/", "/playerDetails", "/playerLogin", "/login", "/demo/**").permitAll()
                    .antMatchers("/game/**").hasRole("PLAYER")
                    .anyRequest().hasAnyRole("PLAYER", "ADMIN", "MODERATOR")
                    .and()
                .formLogin()
                    .loginPage("/")
                    .and()
                .logout()
                    .logoutUrl("/game/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .and()
                .addFilterBefore(playerAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

            http.sessionManagement().maximumSessions(1)
                    .maxSessionsPreventsLogin(true)
                    .sessionRegistry(sessionRegistry())
                    .expiredUrl("/");
        }

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher sessionEventPublisher() {
            return new HttpSessionEventPublisher();
        }
    }
}
