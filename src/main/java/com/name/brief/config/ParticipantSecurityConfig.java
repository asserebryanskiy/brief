package com.name.brief.config;

import com.name.brief.config.authentication.ParticipantPreAuthenticationFilter;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(2)
public class ParticipantSecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String LOGIN_PROCESSING_URL = "/login";

    private final PlayerService playerService;
    private final GameSessionService gameSessionService;

    public ParticipantSecurityConfig(PlayerService playerService,
                                     GameSessionService gameSessionService) {
        this.playerService = playerService;
        this.gameSessionService = gameSessionService;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/webjars/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(playerService);
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
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .successHandler((req, res, auth) -> {
                    res.sendRedirect("/game");
                })
                .failureForwardUrl("/")
                .and()
            .logout()
                .logoutUrl("/game/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
            .sessionManagement()
                .maximumSessions(1)
                    .sessionRegistry(playerSessionRegistry())
                    .maxSessionsPreventsLogin(true)
                    .and()
                .and()
            .rememberMe()
                .userDetailsService(playerService)
                .alwaysRemember(true)
                .useSecureCookie(false)
                .tokenValiditySeconds(60 * 60 * 24 * 2)
                .authenticationSuccessHandler((req, res, auth) -> res.sendRedirect("/game"))
                .and()
            .addFilterBefore(preAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .csrf().disable();
    }

    @Bean
    public SessionRegistry playerSessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ParticipantPreAuthenticationFilter preAuthenticationFilter() {
        return new ParticipantPreAuthenticationFilter(gameSessionService, playerService);
    }
}
