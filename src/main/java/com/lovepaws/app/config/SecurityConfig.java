package com.lovepaws.app.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.lovepaws.app.security.CustomSuccessHandler;
import com.lovepaws.app.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    private final CustomSuccessHandler successHandler;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                //  Recursos estáticos y rutas públicas
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
<<<<<<< HEAD
                .requestMatchers("/", "/index", "/home", "/usuarios/registro", "/usuarios/recuperar-password", "/usuarios/reset-password", "/login", "/registro", "/mascotas/**", "/nosotros", "/contacto", "/adopcion").permitAll()
=======
                .requestMatchers("/", "/index", "/home", "/usuarios/registro", "/usuarios/recuperar-password", "/login", "/registro", "/mascotas/**", "/nosotros", "/contacto", "/adopcion").permitAll()
>>>>>>> refs/heads/codex/update-card-and-label-styles
                // Rutas protegidas por rol
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/gestor/**").hasAnyRole("GESTOR","ADMIN")
                .requestMatchers("/adopcion/gestor/**").hasRole("GESTOR")
                .requestMatchers("/adopcion/mis-adopciones", "/adopcion/solicitar", "/adopcion/cancelar/**").hasRole("ADOPTANTE")
                .requestMatchers("/mascota/catalogo/**").hasRole("ADOPTANTE")
                // Todo lo demás requiere login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/usuarios/login")                    // Página de login personalizada
                .loginProcessingUrl("/login")  //  URL que procesa el formulario
                .usernameParameter("username")          // Campo del formulario
                .passwordParameter("password")
                .successHandler(successHandler)

           // Redirección después de login exitoso
                .failureUrl("/usuarios/login?error=true")             // Redirección si falla
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendRedirect("/acceso-denegado"))
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendRedirect("/usuarios/login"))
            )
            .csrf(Customizer.withDefaults()) // Mantiene CSRF habilitado
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
