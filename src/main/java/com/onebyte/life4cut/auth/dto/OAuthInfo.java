package com.onebyte.life4cut.auth.dto;

import com.onebyte.life4cut.auth.exception.NotSupportOAuthType;
import com.onebyte.life4cut.common.constants.OAuthType;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthInfo {

  @Value("${auth.kakao.secret}")
  private static String kakaoSecret;

  @Value("${auth.naver.secret}")
  private static String naverSecret;

  @Value("${auth.google.secret}")
  private static String googleSecret;

  private String email;
  private OAuthType oauthType;
  private String oauthId;
  private String secret;

  private OAuthInfo(String email, OAuthType oauthType, String oauthId, String secret) {
    this.email = email;
    this.oauthType = oauthType;
    this.oauthId = oauthId;
    this.secret = secret;
  }

  public static OAuthInfo createOAuthInfo(String type, OAuth2AuthenticationToken token) {
    if (type.equals("kakao-login")) {
      return createKakao(token);
    } else if (type.equals("naver-login")) {
      return createNaver(token);
    } else if (type.equals("google-login")) {
      return createGoogle(token);
    } else {
      throw new NotSupportOAuthType();
    }
  }

  private static OAuthInfo createKakao(OAuth2AuthenticationToken token) {
    String email =
        ((Map<String, Object>) token.getPrincipal().getAttribute("kakao_account"))
            .get("email")
            .toString();
    String id = token.getPrincipal().getAttribute("id").toString();
    return new OAuthInfo(email, OAuthType.KAKAO_LOGIN, id, kakaoSecret);
  }

  private static OAuthInfo createNaver(OAuth2AuthenticationToken token) {
    String email =
        ((Map<String, Object>) token.getPrincipal().getAttribute("response"))
            .get("email")
            .toString();
    String id =
        ((Map<String, Object>) token.getPrincipal().getAttribute("response")).get("id").toString();
    return new OAuthInfo(email, OAuthType.NAVER_LOGIN, id, naverSecret);
  }

  private static OAuthInfo createGoogle(OAuth2AuthenticationToken token) {
    String email = token.getPrincipal().getAttribute("email");
    String id = token.getPrincipal().getAttribute("id");
    return new OAuthInfo(email, OAuthType.GOOGLE_LOGIN, id, googleSecret);
  }
}
