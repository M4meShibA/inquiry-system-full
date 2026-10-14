package com.example.inquiry.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ログイン不要: トップ、問い合わせフォーム(お客様向け)、ログイン画面、CSS
                        .requestMatchers("/login", "/error", "/style.css").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/contact", "/contact/thanks").permitAll()
                        .requestMatchers(HttpMethod.POST, "/contact").permitAll()
                        // それ以外(/admin 以下の一覧・詳細・返信・削除など)は管理者のみ
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/inquiries", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${app.admin.username:}") String username,
            @Value("${app.admin.password:}") String password,
            PasswordEncoder encoder) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            // 初期パスワードのまま公開してしまう事故を防ぐため、未設定なら起動させない
            throw new IllegalStateException(
                    "環境変数 ADMIN_USERNAME と ADMIN_PASSWORD を設定してから起動してください");
        }
        return new InMemoryUserDetailsManager(
                User.withUsername(username)
                        .password(encoder.encode(password))
                        .roles("ADMIN")
                        .build());
    }
}
