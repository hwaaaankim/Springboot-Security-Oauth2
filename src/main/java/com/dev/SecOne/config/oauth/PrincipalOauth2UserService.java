package com.dev.SecOne.config.oauth;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.dev.SecOne.config.auth.PrincipalDetails;
import com.dev.SecOne.config.oauth.provider.FaceBookUserInfo;
import com.dev.SecOne.config.oauth.provider.GoogleUserInfo;
import com.dev.SecOne.config.oauth.provider.NaverUserInfo;
import com.dev.SecOne.config.oauth.provider.OAuth2UserInfo;
import com.dev.SecOne.model.User;
import com.dev.SecOne.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	
//	@Autowired
//	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	// userRequest 는 code를 받아서 accessToken을 응답 받은 객체
	// oauth2 로그인 이후에 후처리를 하는 메서드
	// Google 로그인 버튼 클릭 -> Google Login Form -> Login 완료 -> Return되는 Code 를 Oauth2-client Library가 받는다
	// -> Code를 통해 AccessToken 을 요청(Oauth2-client Library) -> userRequest 정보
	// 회원 프로필을 받아야 한다(loadUser 메서드를 통해서 받는다)
	// 메서드 종료시 @AuthenticationPrincipal 어노테이션이 생성
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest); // google의 회원 프로필 조회

		// code를 통해 구성한 정보
		System.out.println("userRequest clientRegistration : " + userRequest.getClientRegistration());
		System.out.println("userRequest accessToken : " + userRequest.getAccessToken().getTokenValue());
		System.out.println("getAttributes : "+ super.loadUser(userRequest).getAttributes());
		// token을 통해 응답받은 회원정보
		System.out.println("oAuth2User : " + oAuth2User);
	
		
		//강제로 회원가입 진행 초기 버전
//		String provider = userRequest.getClientRegistration().getRegistrationId();//google
//		String providerId = oAuth2User.getAttribute("sub");
//		String username = provider+"_"+providerId; //google_10974285618291642686
//		String password = bCryptPasswordEncoder.encode("Password"); // 필요없으나 임의로 생성 해 준다
//		String email = oAuth2User.getAttribute("email");
//		String role = "ROLE_USER";
//		
//		User user = userRepository.findByUsername(username);
//		if(user ==null) {
//			user = User.builder()
//					.username(username)
//					.password(password)
//					.email(email)
//					.role(role)
//					.provider(provider)
//					.providerId(providerId)
//					.build();
//			System.out.println(user);
//			userRepository.save(user);
//		}
//		
//		return new PrincipalDetails(user, oAuth2User.getAttributes());
		
		return processOAuth2User(userRequest, oAuth2User);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

		// Attribute를 파싱해서 공통 객체로 묶는다. 관리가 편함.
		OAuth2UserInfo oAuth2UserInfo = null;
		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청~~");
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청~~");
			oAuth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
		} else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
			System.out.println("네이버 로그인 요청~~");
			oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
			// response 라는 객체 안에 다시 response라는 Map의 형태로 User정보가 들어있기 때문에 response를 전달해준다
		} else {
			System.out.println("우리는 구글과 페이스북만 지원해요 ㅎㅎ");
		}

		//System.out.println("oAuth2UserInfo.getProvider() : " + oAuth2UserInfo.getProvider());
		//System.out.println("oAuth2UserInfo.getProviderId() : " + oAuth2UserInfo.getProviderId());
		Optional<User> userOptional = 
				userRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
		
		User user;
		if (userOptional.isPresent()) {
			user = userOptional.get();
			// user가 존재하면 update 해주기
			user.setEmail(oAuth2UserInfo.getEmail());
			userRepository.save(user);
		} else {
			// user의 패스워드가 null이기 때문에 OAuth 유저는 일반적인 로그인을 할 수 없음.
			user = User.builder()
					.username(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId())
					.email(oAuth2UserInfo.getEmail())
					.role("ROLE_USER")
					.provider(oAuth2UserInfo.getProvider())
					.providerId(oAuth2UserInfo.getProviderId())
					.build();
			userRepository.save(user);
		}

		return new PrincipalDetails(user, oAuth2User.getAttributes());
	}
}