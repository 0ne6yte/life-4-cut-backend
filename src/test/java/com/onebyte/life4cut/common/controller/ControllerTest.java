package com.onebyte.life4cut.common.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.onebyte.life4cut.config.ImagePathSerializer;
import com.onebyte.life4cut.config.TestS3EnvConfiguration;
import com.onebyte.life4cut.config.TestSecurityConfiguration;
import com.onebyte.life4cut.config.WebConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

@AutoConfigureRestDocs
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class, SpringExtension.class})
@ImportAutoConfiguration({
  TestSecurityConfiguration.class,
  TestS3EnvConfiguration.class,
  WebConfiguration.class,
  ImagePathSerializer.class
})
public abstract class ControllerTest {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  protected Object asParsedJson(Object obj) throws JsonProcessingException {
    String json = new ObjectMapper().writeValueAsString(obj);
    return JsonPath.read(json, "$");
  }

  protected ResultActions performGet(String uri) throws Exception {
    return mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON));
  }

  protected ResultActions performGet(String uri, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(get(uri).accept(MediaType.APPLICATION_JSON_VALUE).params(params));
  }

  protected ResultActions performPost(String uri) throws Exception {
    return mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON));
  }

  protected ResultActions performPost(String uri, Object body) throws Exception {
    return mockMvc.perform(
        post(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }

  protected ResultActions performPut(String uri, Object body) throws Exception {
    return mockMvc.perform(
        put(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }

  protected ResultActions performDelete(String uri, Object body) throws Exception {
    return mockMvc.perform(
        delete(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }
}
