package com.dev.SecOne.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.dev.SecOne.model.User;

import lombok.Data;

// 시큐리티가 /login(loginProcessingUrl) 요청이 오면 낚아채서 로그인을 진행
// 로그인 진행이 완료되면 Security Session을 만들어서 Security ContextHolder 라는 key 값에 Session 정보를 저장
// session에 들어갈 수 있는 Object는 정해져있다 (Authentication 타입 객체만 저장된다)
// Authentication 안에 User정보가 존재 해야 한다
// User 오브젝트 타입 => UserDetails 타입의 객체 

// Security Session => Authentication => UserDetails

@Data
public class PrincipalDetails implements UserDetails{

	private static final long serialVersionUID = 1L;
	
	private User user;

	public PrincipalDetails(User user) {
		super();
		this.user = user;
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}
	
	
	// User의 권한을 Return
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
//		collet.add(()->{ return user.getRole();});
		collet.add(new GrantedAuthority() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collet;
	}
	
}
