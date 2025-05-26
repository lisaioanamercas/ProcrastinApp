//package org.example.backend.security;
//
//import org.example.backend.security.jwt.AuthEntryPointJwt;
//import org.example.backend.security.jwt.AuthTokenFilter;
//import org.example.backend.service.UserDetailsServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//
//    @Autowired
//    private AuthEntryPointJwt unauthorizedHandler;
//
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public AuthTokenFilter authenticationJwtTokenFilter() {
//        return new AuthTokenFilter();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//    @Bean
//    // Metoda filterChain configurează securitatea aplicației, inclusiv autentificarea și autorizarea.
//    // Aceasta include configurarea unui filtru JWT pentru autentificare, gestionarea sesiunilor și permisiunile pentru diferite endpoint-uri.
//    // Este folosită pentru a configura securitatea aplicației, inclusiv autentificarea și autorizarea utilizatorilor.
//    // Această metodă returnează un obiect SecurityFilterChain care definește regulile de securitate pentru aplicație.
//
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(unauthorizedHandler))
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
//                        .anyRequest().authenticated()
//                );
//
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
//// clasa SecurityConfig configurează securitatea aplicației, inclusiv autentificarea și autorizarea.
//// Aceasta include configurarea unui filtru JWT pentru autentificare, gestionarea sesiunilor și permisiunile pentru diferite endpoint-uri.
//// De asemenea, definește un cod de parolă pentru criptarea parolelor utilizatorilor.
//// Această configurație asigură că aplicația este protejată împotriva accesului neautorizat și că utilizatorii pot fi autentificați în mod sigur.
//// Este folosită pentru a configura securitatea aplicației, inclusiv autentificarea și autorizarea utilizatorilor.
//// Este o clasă de configurare care extinde WebSecurityConfigurerAdapter și definește un filtru JWT pentru autentificare.
//// De asemenea, configurează gestionarea sesiunilor și permisiunile pentru diferite endpoint-uri.
//// Această configurație asigură că aplicația este protejată împotriva accesului neautorizat și că utilizatorii pot fi autentificați în mod sigur.
