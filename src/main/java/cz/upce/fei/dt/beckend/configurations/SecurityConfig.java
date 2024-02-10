package cz.upce.fei.dt.beckend.configurations;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import cz.upce.fei.dt.ui.views.LoginView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {

    //private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //.csrf(AbstractHttpConfigurer::disable)
                //.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(url-> { url
                    .requestMatchers(antMatchers("/images/*.png")).permitAll();
                    //.requestMatchers(antMatchers("/users/**")).hasRole("ADMIN");
                })
                .authenticationProvider(authenticationProvider);
                //.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        super.configure(http);
        setLoginView(http, LoginView.class);
    }


//    @Override
//    protected void configure(WebSecurity web) throws Exception {
//        web.ignoring().requestMatchers(new AntPathRequestMatcher("/images/**"));
//        super.configure(web);
//    }
}
