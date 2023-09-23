package com.onebyte.life4cut.user.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.onebyte.life4cut.auth.dto.CustomUserDetails;
import com.onebyte.life4cut.common.ControllerTest;
import com.onebyte.life4cut.config.TestSecurityConfiguration;
import com.onebyte.life4cut.user.controller.dto.UserFindResponse;
import com.onebyte.life4cut.user.domain.User;
import com.onebyte.life4cut.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(UserController.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class, SpringExtension.class})
@ImportAutoConfiguration(TestSecurityConfiguration.class)
class UserControllerTest extends ControllerTest {

  @MockBean
  UserService userService;

  private static final String baseUri = "/api/v1/users";

  @WithMockUser
  @DisplayName("고유한 유저 닉네임으로 유저를 조회한다.")
  @Test
  void findUser() throws Exception {
    // given
    String nickname = "bell";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("nickname", nickname);

    User user = User.builder()
        .id(1L)
        .nickname(nickname)
        .oauthId("12345")
        .oauthType("kakao")
        .profilePath("profilePath")
        .email("bell@gmail.com")
        .build();
    UserFindResponse result = UserFindResponse.of(user);

    // when
    Mockito.when(userService.findUserByNickname(nickname)).thenReturn(user);

    // then
    ResultActions actions = performGet(baseUri, params);
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data", equalTo(asParsedJson(result))))
        .andDo(
            document(
                "{class_name}/{method_name}",
                queryParameters(
                    parameterWithName("nickname").description("유저 닉네임")
                ),
                responseFields(
                    fieldWithPath("message").type(STRING).description("응답 메시지"),
                    fieldWithPath("data").type(OBJECT).description("데이터"),
                    fieldWithPath("data.userId").type(NUMBER).description("유저 ID"),
                    fieldWithPath("data.nickname").type(STRING).description("유저 닉네임"),
                    fieldWithPath("data.email").type(STRING).description("유저 이메일"),
                    fieldWithPath("data.profilePath").type(STRING).description("유저 프로필 사진 파일 경로")
                )
            )
        )
        .andDo(print());
  }

  @DisplayName("@AuthenticationPrincipal을 통해 본인을 조회한다.")
  @Test
  void findMe() throws Exception {
    // given
    String nickname = "bell";
    User user = User.builder()
        .id(1L)
        .nickname(nickname)
        .oauthId("12345")
        .oauthType("kakao")
        .profilePath("profilePath")
        .email("bell@gmail.com")
        .build();
    CustomUserDetails principal = new CustomUserDetails(user.getNickname(), "password",
        user.getId(), AuthorityUtils.createAuthorityList("ADMIN"));
    UserFindResponse result = UserFindResponse.of(user);

    // when
    Mockito.when(userService.findUser(anyLong())).thenReturn(user);

    // then
    ResultActions actions = performGet(baseUri + "/me", principal);
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data", equalTo(asParsedJson(result))))
        .andDo(
            document(
                "{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("message").type(STRING).description("응답 메시지"),
                    fieldWithPath("data").type(OBJECT).description("데이터"),
                    fieldWithPath("data.userId").type(NUMBER).description("유저 ID"),
                    fieldWithPath("data.nickname").type(STRING).description("유저 닉네임"),
                    fieldWithPath("data.email").type(STRING).description("유저 이메일"),
                    fieldWithPath("data.profilePath").type(STRING).description("유저 프로필 사진 파일 경로")
                )
            )
        )
        .andDo(print());
  }
}