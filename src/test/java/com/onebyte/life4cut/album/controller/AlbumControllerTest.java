package com.onebyte.life4cut.album.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.onebyte.life4cut.album.controller.dto.CreatePictureRequest;
import com.onebyte.life4cut.album.controller.dto.UpdatePictureRequest;
import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.repository.dto.UserDetailResult;
import com.onebyte.life4cut.album.service.AlbumService;
import com.onebyte.life4cut.album.service.dto.AlbumInfo;
import com.onebyte.life4cut.common.annotation.WithCustomMockUser;
import com.onebyte.life4cut.common.controller.ControllerTest;
import com.onebyte.life4cut.common.vo.ImagePath;
import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import com.onebyte.life4cut.picture.service.PictureService;
import com.onebyte.life4cut.picture.service.dto.PictureDetailInSlot;
import com.onebyte.life4cut.pictureTag.domain.vo.PictureTagName;
import com.onebyte.life4cut.pictureTag.service.PictureTagService;
import com.onebyte.life4cut.slot.domain.vo.SlotLayout;
import com.onebyte.life4cut.slot.domain.vo.SlotLocation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest extends ControllerTest {

  private final String API_TAG = "ALBUM API";

  @MockBean private PictureService pictureService;

  @MockBean private PictureTagService pictureTagService;

  @MockBean private AlbumService albumService;

  private PictureTagFixtureFactory pictureTagFixtureFactory = new PictureTagFixtureFactory();

  @Nested
  @WithCustomMockUser
  class UploadPicture {

    @Test
    @DisplayName("사진을 업로드한다")
    void uploadPicture() throws Exception {
      // given
      Long slotId = 1L;
      String content = "content";
      List<String> tags = List.of("tag1", "tag2");
      LocalDate picturedAt = LocalDate.of(2021, 1, 1);
      CreatePictureRequest request = new CreatePictureRequest(slotId, content, tags, picturedAt);

      MockMultipartFile image =
          new MockMultipartFile(
              "image", "original-name.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());
      MockMultipartFile data =
          new MockMultipartFile(
              "data",
              "",
              MediaType.APPLICATION_JSON_VALUE,
              objectMapper.writeValueAsString(request).getBytes());

      when(pictureService.createInSlot(any(), any(), any(), any(), any(), any(), any()))
          .thenReturn(100L);

      // when
      ResultActions result =
          mockMvc.perform(
              multipart("/api/v1/albums/{albumId}/pictures", 1L).file(image).file(data));

      // then
      result
          .andExpect(status().isOk())
          .andExpect(jsonPath("data.id", equalTo(100)))
          .andDo(
              document(
                  "{class_name}/{method_name}",
                  resource(
                      ResourceSnippetParameters.builder()
                          .tag(API_TAG)
                          .description("사진을 업로드한다")
                          .summary("사진을 업로드한다")
                          .pathParameters(
                              parameterWithName("albumId")
                                  .description("앨범 아이디")
                                  .type(SimpleType.NUMBER))
                          .responseFields(
                              fieldWithPath("message").type(STRING).description("응답 메시지"),
                              fieldWithPath("data.id").type(NUMBER).description("사진 아이디"))
                          .requestSchema(Schema.schema("CreatePictureRequest"))
                          .responseSchema(Schema.schema("CreatePictureResponse"))
                          .build()),
                  requestPartFields(
                      "data",
                      fieldWithPath("slotId").type(NUMBER).description("슬롯 아이디"),
                      fieldWithPath("content").type(JsonFieldType.STRING).description("사진 내용"),
                      fieldWithPath("tags[]")
                          .type(JsonFieldType.ARRAY)
                          .description("사진 태그 목록")
                          .attributes(Attributes.key("itemType").value(JsonFieldType.STRING)),
                      fieldWithPath("picturedAt").description("사진 찍은 날짜")),
                  requestPartBody("image")));
    }
  }

  @Nested
  @WithCustomMockUser
  class SearchTags {

    @Test
    @DisplayName("태그를 검색한다")
    void searchTags() throws Exception {
      // given
      String keyword = "keyword";
      when(pictureTagService.searchTags(any(), any(), any()))
          .thenReturn(
              List.of(
                  pictureTagFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", 1L);
                        builder.set("name", PictureTagName.of("tag1"));
                      }),
                  pictureTagFixtureFactory.make(
                      (entity, builder) -> {
                        builder.set("id", 2L);
                        builder.set("name", PictureTagName.of("tag2"));
                      })));

      // when
      ResultActions result =
          mockMvc.perform(get("/api/v1/albums/{albumId}/tags", 1L).param("keyword", keyword));

      // then
      result
          .andExpect(status().isOk())
          .andDo(
              document(
                  "{class_name}/{method_name}",
                  resource(
                      ResourceSnippetParameters.builder()
                          .tag(API_TAG)
                          .description("태그를 검색한다")
                          .summary("태그를 검색한다")
                          .pathParameters(
                              parameterWithName("albumId")
                                  .description("앨범 아이디")
                                  .type(SimpleType.NUMBER))
                          .queryParameters(
                              parameterWithName("keyword")
                                  .description("검색어")
                                  .type(SimpleType.STRING)
                                  .optional())
                          .responseFields(
                              fieldWithPath("message").type(STRING).description("응답 메시지"),
                              fieldWithPath("data.tags[]")
                                  .type(JsonFieldType.ARRAY)
                                  .description("태그 목록"),
                              fieldWithPath("data.tags[].id").type(NUMBER).description("태그 아이디"),
                              fieldWithPath("data.tags[].name").type(STRING).description("태그 이름"))
                          .responseSchema(Schema.schema("SearchTagResponse"))
                          .build())));
    }
  }

  @Nested
  @WithCustomMockUser
  class UpdatePicture {

    @Test
    @DisplayName("사진을 수정한다")
    void updatePicture() throws Exception {
      // given
      Long pictureId = 1L;
      String content = "content";
      List<String> tags = List.of("tag1", "tag2");
      LocalDate picturedAt = LocalDate.of(2021, 1, 1);
      UpdatePictureRequest request = new UpdatePictureRequest(content, tags, picturedAt);

      MockMultipartFile image =
          new MockMultipartFile(
              "image", "original-name.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());
      MockMultipartFile data =
          new MockMultipartFile(
              "data",
              "",
              MediaType.APPLICATION_JSON_VALUE,
              objectMapper.writeValueAsString(request).getBytes());

      doNothing()
          .when(pictureService)
          .updatePicture(any(), any(), any(), any(), any(), any(), any());

      // when
      ResultActions result =
          mockMvc.perform(
              ((MockMultipartHttpServletRequestBuilder)
                      MockMvcRequestBuilders.multipart(
                              HttpMethod.PATCH,
                              "/api/v1/albums/{albumId}/pictures/{pictureId}",
                              1L,
                              pictureId)
                          .requestAttr(
                              RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
                              "/api/v1/albums/{albumId}/pictures/{pictureId}"))
                  .file(image)
                  .file(data));

      // then
      result
          .andExpect(status().isOk())
          .andDo(
              document(
                  "{class_name}/{method_name}",
                  resource(
                      ResourceSnippetParameters.builder()
                          .tag(API_TAG)
                          .description("사진을 수정한다")
                          .summary("사진을 수정한다")
                          .pathParameters(
                              parameterWithName("albumId")
                                  .description("앨범 아이디")
                                  .type(SimpleType.NUMBER),
                              parameterWithName("pictureId")
                                  .description("사진 아이디")
                                  .type(SimpleType.NUMBER))
                          .requestSchema(Schema.schema("UpdatePictureRequest"))
                          .responseSchema(Schema.schema("EmptyResponse"))
                          .build()),
                  requestPartFields(
                      "data",
                      fieldWithPath("content")
                          .type(JsonFieldType.STRING)
                          .description("사진 내용")
                          .optional(),
                      fieldWithPath("tags[]")
                          .type(JsonFieldType.ARRAY)
                          .description("사진 태그 목록")
                          .attributes(Attributes.key("itemType").value(JsonFieldType.STRING))
                          .optional(),
                      fieldWithPath("picturedAt").description("사진 찍은 날짜").optional()),
                  requestPartBody("image")));
    }
  }

  @Nested
  @WithCustomMockUser
  class GetPicturesInSlot {

    @Test
    @DisplayName("앨범내 사진을 페이지단위로 조회한다")
    void getPicturesInSlot() throws Exception {
      // given
      Long albumId = 1L;

      when(pictureService.getPicturesInSlotByAlbum(any(), any()))
          .thenReturn(
              List.of(
                  new PictureDetailInSlot(
                      1L,
                      1L,
                      SlotLayout.FAT_HORIZONTAL,
                      SlotLocation.LEFT,
                      Optional.of(
                          new PictureDetailResult(
                              1L,
                              "content",
                              ImagePath.of("path"),
                              LocalDateTime.of(2023, 10, 15, 0, 14, 15),
                              "tag1,tag2")))));

      // when
      ResultActions result = mockMvc.perform(get("/api/v1/albums/{albumId}/pictures", albumId));

      // then
      result
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("OK"))
          .andExpect(jsonPath("$.data.pictures[0].slots[0].pictureId").value(1))
          .andExpect(
              jsonPath("$.data.pictures[0].slots[0].path")
                  .value("https://test-bucket.s3.ap-northeast-2.amazonaws.com/path"))
          .andExpect(jsonPath("$.data.pictures[0].slots[0].content").value("content"))
          .andExpect(jsonPath("$.data.pictures[0].slots[0].layout").value("FAT_HORIZONTAL"))
          .andExpect(jsonPath("$.data.pictures[0].slots[0].location").value("LEFT"))
          .andExpect(
              jsonPath("$.data.pictures[0].slots[0].picturedAt").value("2023-10-15T00:14:15"))
          .andExpect(jsonPath("$.data.pictures[0].slots[0].tagNames[0]").value("tag1"))
          .andExpect(jsonPath("$.data.pictures[0].slots[0].tagNames[1]").value("tag2"))
          .andDo(
              document(
                  "{class_name}/{method_name}",
                  preprocessRequest(prettyPrint()),
                  preprocessResponse(prettyPrint()),
                  resource(
                      ResourceSnippetParameters.builder()
                          .tag(API_TAG)
                          .description("앨범내 페이지별 사진 목록을 조회한다")
                          .summary("앨범내 페이지별 사진 목록을 조회한다")
                          .pathParameters(
                              parameterWithName("albumId")
                                  .description("앨범 아이디")
                                  .type(SimpleType.NUMBER))
                          .responseFields(
                              fieldWithPath("message").type(STRING).description("응답 메시지"),
                              fieldWithPath("data.pictures[]")
                                  .type(JsonFieldType.ARRAY)
                                  .description("페이지 목록"),
                              fieldWithPath("data.pictures[].slots[]")
                                  .type(JsonFieldType.ARRAY)
                                  .description("페이지 내 슬롯 목록"),
                              fieldWithPath("data.pictures[].slots[].pictureId")
                                  .type(NUMBER)
                                  .description("사진 아이디"),
                              fieldWithPath("data.pictures[].slots[].path")
                                  .type(STRING)
                                  .description("사진 경로"),
                              fieldWithPath("data.pictures[].slots[].content")
                                  .type(STRING)
                                  .description("사진 내용"),
                              fieldWithPath("data.pictures[].slots[].layout")
                                  .type(STRING)
                                  .description("사진 레이아웃"),
                              fieldWithPath("data.pictures[].slots[].location")
                                  .type(STRING)
                                  .description("사진 위치"),
                              fieldWithPath("data.pictures[].slots[].picturedAt")
                                  .type(STRING)
                                  .description("사진 찍은 날짜"),
                              fieldWithPath("data.pictures[].slots[].tagNames[]")
                                  .type(JsonFieldType.ARRAY)
                                  .description("사진 태그 목록")
                                  .attributes(
                                      Attributes.key("itemType").value(JsonFieldType.STRING)))
                          .responseSchema(Schema.schema("GetPictureInSlotResponse"))
                          .build())));
    }
  }

  @Nested
  @WithCustomMockUser
  class GetMyRoleInAlbum {

    @Test
    @DisplayName("앨범에 대한 내 권한을 조회한다")
    void getMyRoleInAlbum() throws Exception {
      // given
      Long albumId = 1L;

      when(albumService.getRoleInAlbum(any(), any())).thenReturn(UserAlbumRole.HOST);

      // when
      ResultActions result = mockMvc.perform(get("/api/v1/albums/{albumId}/roles/me", albumId));

      // then
      result
          .andExpect(status().isOk())
          .andDo(
              document(
                  "{class_name}/{method_name}",
                  preprocessRequest(prettyPrint()),
                  preprocessResponse(prettyPrint()),
                  resource(
                      ResourceSnippetParameters.builder()
                          .tag(API_TAG)
                          .description("앨범에 대한 내 권한을 조회한다")
                          .summary("앨범에 대한 내 권한을 조회한다")
                          .pathParameters(
                              parameterWithName("albumId")
                                  .description("앨범 아이디")
                                  .type(SimpleType.NUMBER))
                          .responseFields(
                              fieldWithPath("message").type(STRING).description("응답 메시지"),
                              fieldWithPath("data.role").type(STRING).description("앨범에 대한 내 권한"))
                          .responseSchema(Schema.schema("GetMyRoleInAlbumResponse"))
                          .build())));
    }
  }

  @Nested
  @WithCustomMockUser
  class GetAlbumInfo {

    @Test
    @DisplayName("앨범에 대한 세부 정보를 조회한다")
    void getAlbumInfo() throws Exception {
      // given
      Long albumId = 1L;
      when(albumService.getAlbumInfo(any(), any()))
          .thenReturn(
              new AlbumInfo(
                  1L,
                  "앨범명",
                  List.of(new UserDetailResult(1L, "http://~~", "codingDog", UserAlbumRole.HOST))));

      // when
      ResultActions result = mockMvc.perform(get("/api/v1/albums/{albumId}", albumId));

      // then
      result
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("OK"))
          .andExpect(jsonPath("$.data.albumId").value(1))
          .andExpect(jsonPath("$.data.albumName").value("앨범명"))
          .andExpect(jsonPath("$.data.sharedCount").value(1))
          .andExpect(jsonPath("$.data.sharedUsers[0].id").value(1))
          .andExpect(jsonPath("$.data.sharedUsers[0].profilePath").value("http://~~"))
          .andExpect(jsonPath("$.data.sharedUsers[0].nickname").value("codingDog"))
          .andExpect(jsonPath("$.data.sharedUsers[0].role").value("HOST"))
          .andDo(
              document(
                  "{class_name}/{method_name}",
                  preprocessRequest(prettyPrint()),
                  preprocessResponse(prettyPrint()),
                  resource(
                      ResourceSnippetParameters.builder()
                          .tag(API_TAG)
                          .description("앨범명, 공유하고 있는 유저 등 앨범 세부 내용을 조회한다")
                          .summary("앨범 세부 내용을 조회한다")
                          .pathParameters(
                              parameterWithName("albumId")
                                  .description("앨범 아이디")
                                  .type(SimpleType.NUMBER))
                          .responseFields(
                              fieldWithPath("message").type(STRING).description("응답 메시지"),
                              fieldWithPath("data.albumId").type(NUMBER).description("앨범 id"),
                              fieldWithPath("data.albumName").type(STRING).description("앨범명"),
                              fieldWithPath("data.sharedCount")
                                  .type(NUMBER)
                                  .description("앨범 공유자 수"),
                              fieldWithPath("data.sharedUsers[]")
                                  .type(JsonFieldType.ARRAY)
                                  .description("앨범 공유자 목록"),
                              fieldWithPath("data.sharedUsers[].id")
                                  .type(NUMBER)
                                  .description("유저 id"),
                              fieldWithPath("data.sharedUsers[].profilePath")
                                  .type(STRING)
                                  .description("프로필 이미지 경로"),
                              fieldWithPath("data.sharedUsers[].nickname")
                                  .type(STRING)
                                  .description("닉네임"),
                              fieldWithPath("data.sharedUsers[].role")
                                  .type(STRING)
                                  .description("앨범에 대한 권한"))
                          .responseSchema(Schema.schema("GetAlbumInfoResponse"))
                          .build())));
    }
  }
}
