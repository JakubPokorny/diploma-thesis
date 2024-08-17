package cz.upce.fei.dt.backend.configurations;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import cz.upce.fei.dt.ui.views.LoginView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {

    private final AuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(url -> url.requestMatchers(antMatchers("/images/*.png")).permitAll())
                .authenticationProvider(authenticationProvider);

        super.configure(http);
        setLoginView(http, LoginView.class);
    }
}
