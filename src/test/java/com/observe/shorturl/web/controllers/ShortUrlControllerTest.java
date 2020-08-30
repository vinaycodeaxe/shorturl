package com.observe.shorturl.web.controllers;

import com.observe.shorturl.dao.entity.Url;
import com.observe.shorturl.dao.repository.UrlRepository;
import com.observe.shorturl.dto.request.ShortUrlRequest;
import com.observe.shorturl.service.mapper.ShortUrlServiceMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @Value("${url.not.found.fallback}")
    private String notFoundUrl;

    private ShortUrlRequest fullShortUrlRequest;
    private ShortUrlRequest invalidUrlRequest;
    private String missingClientIdRequest;
    private String missingLongUrlRequest;
    private String randomKey;
    private ShortUrlRequest hitCountShortUrlRequest;

    @BeforeEach
    void setUp() {
        fullShortUrlRequest = ShortUrlRequest
                .builder()
                .clientId(1L)
                .longUrl("http://www.google.com")
                .build();

        invalidUrlRequest = ShortUrlRequest
                .builder()
                .clientId(1L)
                .longUrl("google")
                .build();
        missingClientIdRequest = "{\n" +
                "  \"longUrl\": \"www.google.com\"\n" +
                "}";

        missingLongUrlRequest = "{\n" +
                "  \"clientId\": 1\n" +
                "}";
        randomKey = "this_this_random";

        hitCountShortUrlRequest = ShortUrlRequest
                .builder()
                .clientId(2L)
                .longUrl("http://www.google.com")
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

    @Test
    void Payload_Containing_Missing_client_id_Expect_400() throws Exception {

        mockMvc.perform(post("/api/v1/shorturl")
                .content(missingClientIdRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Payload_Containing_Missing_Long_Url_Expect_400() throws Exception {

        mockMvc.perform(post("/api/v1/shorturl")
                .content(missingLongUrlRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Get_Long_URL_Expect_307() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/shorturl")
                .content(JsonUtil.getJsonFromObject(fullShortUrlRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String shortUrl = mvcResult.getResponse().getContentAsString();
        assertThat(shortUrl).matches("^" + baseUrl + "........");
        String key = shortUrl.substring(shortUrl.length() - 8);

        mockMvc.perform(get("/{shortUrlKey}", key))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl(ShortUrlServiceMapper.
                        generateLongUrl(fullShortUrlRequest.getLongUrl())));

    }

    @Test
    void Get_Long_URL_URL_NOT_FOUND_Expect_307_REDIRECT_To_Error_Page() throws Exception {
        mockMvc.perform(get("/{shortUrlKey}", randomKey))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl(notFoundUrl));

    }

    @Test
    void Get_hit_count_expect_expect_200() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/shorturl")
                .content(JsonUtil.getJsonFromObject(hitCountShortUrlRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        String shortUrl = mvcResult.getResponse().getContentAsString();
        assertThat(shortUrl).matches("^" + baseUrl + "........");
        String key = shortUrl.substring(shortUrl.length() - 8);

        mockMvc.perform(get("/{shortUrlKey}", key))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl(ShortUrlServiceMapper.
                        generateLongUrl(fullShortUrlRequest.getLongUrl())));
        mockMvc.perform(get("/{shortUrlKey}", key))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl(ShortUrlServiceMapper.
                        generateLongUrl(fullShortUrlRequest.getLongUrl())));

        MvcResult hitCountResult = mockMvc.perform(get("/api/v1/hit/{shortUrlKey}", key))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(hitCountResult.getResponse().getContentAsString()).isEqualTo("2");


    }

    @Test
    void Get_hit_count_INVLAID_shortUrlKey_expect_400() throws Exception {
        mockMvc.perform(get("/api/v1/hit/{shortUrlKey}", randomKey))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


    }
}