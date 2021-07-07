package com.dev.SecOne.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.dev.SecOne.config.oauth.PrincipalOauth2UserService;

// 일반 SNS로그인의 경우
// 1.코드받기(인증)
// 2.엑세스 토큰받기(정보접근권한)
// 3.사용자프로필정보 가져오기
// 4.그 정보를 토대로 회원 가입을 자동으로 처리 등
// 5.가져온 정보가 부족한 경우 해당 정보를 입력 받는 등


@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) 
// 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
// securedEnabled = true => Secured 어노테이션 활성화
// prePostEnabled = true => preAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()
			//.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
			//.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
		.and()
			.formLogin()
			.loginPage("/login")
//			.usernameParameter("id")
			.loginProcessingUrl("/loginProc")
//			login 페이지를 요청해서 로그인 인증이 되는 경우에는 "/" 로 가고, 그 외의 다른 경로를 요청해서
//			login 인증이 진행 된 경우에는 요청한 페이지로 이동이 된다
			.defaultSuccessUrl("/")
		.and()
			.oauth2Login()
			.loginPage("/login")
			// 구글 로그인 완료된 후의 후처리
			// 일반 SNS로그인이 아닌 Oauth2-Client를 이용하는 경우 코드X, (엑세스토큰+사용자프로필정보)를 한번에 받는다
			// 
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
	}
}























