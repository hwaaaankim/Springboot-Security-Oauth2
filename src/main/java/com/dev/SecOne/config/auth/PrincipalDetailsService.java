package com.dev.SecOne.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dev.SecOne.model.User;
import com.dev.SecOne.repository.UserRepository;


// SecurityConfig 에서 loginProcessingUrl 로 설정 된 요청이 오면
// UserDetailsService 타입으로 IoC 타입으로 IoC 되어있는 loadUserByUsername 메서드 실행 

@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	// 메서드 종료시 @AuthenticationPrincipal 어노테이션이 생성
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			return null;
		}
		return new PrincipalDetails(user);
		
		// Security Session => Authentication => UserDetails
	}

}