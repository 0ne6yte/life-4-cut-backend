package com.onebyte.life4cut.album.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.onebyte.life4cut.album.controller.dto.CreatePictureRequest;
import com.onebyte.life4cut.common.annotation.WithCustomMockUser;
import com.onebyte.life4cut.common.controller.ControllerTest;
import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import com.onebyte.life4cut.picture.service.PictureService;
import com.onebyte.life4cut.pictureTag.service.PictureTagService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest extends ControllerTest {

  @MockBean private PictureService pictureService;

  @MockBean private PictureTagService pictureTagService;

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
                          .description("사진을 업로드한다")
                          .summary("사진을 업로드한다")
                          .pathParameters(
                              parameterWithName("albumId")
                                  .description("앨범 아이디")
                                  .type(SimpleType.NUMBER))
                          .responseFields(
                              fieldWithPath("message").type(STRING).description("응답 메시지"),
                              fieldWithPath("data.id").type(NUMBER).description("사진 아이디"))
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
                          .build())));
    }
  }
}
