package com.dev.SecOne.controller;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dev.SecOne.config.auth.PrincipalDetails;
import com.dev.SecOne.model.User;
import com.dev.SecOne.repository.UserRepository;

@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication, 
			@AuthenticationPrincipal PrincipalDetails userDetails) {
		// @AuthenticationPrincipal UserDetails userDetails 에서 userDetails 로 implements 했기 때문에
		// PrincipalDetails 타입으로도 받을 수 있다
		System.out.println("/test/login ===========");
		PrincipalDetails pricipalDetails = (PrincipalDetails) authentication.getPrincipal();
		// UserDetails 타입으로 형 변환  해야하나 PrincipalDetails가 Userdetails를 implements 해서
		// PrincipalDetails 타입으로 형 변환 가능
		System.out.println("authentication : " + pricipalDetails.getUser()); 
		// getUser를 위해 PrincipalDetails 에 @Data 어노테이션 추가 
//		System.out.println("userDetails : " + userDetails.getUsername());
		System.out.println("userDetails : " + userDetails.getUser());
		return "Session정보 확인";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) {
		System.out.println("/test/oauth/login ===========");
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		// PrincipalOauth2UserService 의 loadUser를 통해서 받아온 정보
		System.out.println("authentication : " + authentication.getPrincipal()); 
		System.out.println("userDetails : " + oauth2User.getAttributes());
		
		System.out.println("oauth2User : " + oauth.getAttributes());
		
		return "Oauth Session정보 확인";
	}
	
	@GetMapping({ "", "/" })
	public @ResponseBody String index() {
		return "인덱스 페이지입니다.";
	}

	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principal,
			Authentication authentication) {
		System.out.println(authentication.getPrincipal());
		System.out.println("Principal : " + principal);
		System.out.println("OAuth2 : "+principal.getUser().getProvider());
		// iterator 순차 출력 해보기
		Iterator<? extends GrantedAuthority> iter = principal.getAuthorities().iterator();
		while (iter.hasNext()) {
			GrantedAuthority auth = iter.next();
			System.out.println(auth.getAuthority());
		}

		return "유저 페이지입니다.";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "어드민 페이지입니다.";
	}

	// @PostAuthorize("hasRole('ROLE_MANAGER')")
	// : manager() 메서드 종료 후 해당 어노테이션이 적용, 여러개의 권한을 적용 하고 싶을 때 사용
	// @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	// : manager() 메서드 실행 직전에 해당 어노테이션이 적용, 여러개의 권한을 적용 하고 싶을 때 사용
	@Secured("ROLE_MANAGER")
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "매니저 페이지입니다.";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/join")
	public String join() {
		return "join";
	}

	@PostMapping("/joinProc")
	public String joinProc(User user) {
		System.out.println("회원가입 진행 : " + user);
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		userRepository.save(user);
		return "redirect:/";
	}
}
