package com.observe.shorturl.web.controllers;

import com.observe.shorturl.dao.entity.Url;
import com.observe.shorturl.dao.repository.UrlRepository;
import com.observe.shorturl.dto.request.ShortUrlRequest;
import com.observe.shorturl.util.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UrlRepository urlRepository;
    @Value("${service.base.url}")
    private String baseUrl;

    private ShortUrlRequest fullShortUrlRequest;
    private ShortUrlRequest invalidUrlRequest;

    @BeforeEach
    void setUp() {
        fullShortUrlRequest = ShortUrlRequest
                .builder()
                .clientId(1L)
                .longUrl("www.google.com")
                .build();

        invalidUrlRequest = ShortUrlRequest
                .builder()
                .clientId(1L)
                .longUrl("google")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Post_Full_Payload_Expect200_with_Short_URl() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/shorturl")
                .content(JsonUtil.getJsonFromObject(fullShortUrlRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String shortUrl = mvcResult.getResponse().getContentAsString();
        assertThat(shortUrl).matches("^" + baseUrl + "........");
        String key = shortUrl.substring(shortUrl.length() - 8);
        Url url = urlRepository.findByShortUrlKey(key).orElse(null);
        assertThat(url)
                .isNotNull()
                .hasNoNullFieldsOrProperties();
        assertThat(url.getShortUrlKey()).isEqualTo(key);
        assertThat(url.getLongUrl()).isEqualTo(fullShortUrlRequest.getLongUrl());
        assertThat(url.getClientId()).isEqualTo(fullShortUrlRequest.getClientId());
    }

    @Test
    void Payload_Returning_Same_Url_if_hit_with_same_req_again() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/shorturl")
                .content(JsonUtil.getJsonFromObject(fullShortUrlRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String shortUrl = mvcResult.getResponse().getContentAsString();
        assertThat(shortUrl).matches("^" + baseUrl + "........");

        MvcResult mvcResult1 = mockMvc.perform(post("/api/v1/shorturl")
                .content(JsonUtil.getJsonFromObject(fullShortUrlRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String shortUrl1 = mvcResult1.getResponse().getContentAsString();

        assertThat(shortUrl).matches("^" + baseUrl + "........")
                .isEqualTo(shortUrl1);

    }


    @Test
    void Payload_Containing_Illegal_Value_Expect_400() throws Exception {

        mockMvc.perform(post("/api/v1/shorturl")
                .content(JsonUtil.getJsonFromObject(invalidUrlRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}