package com.observe.shorturl.dto.request;

import com.observe.shorturl.constant.ApplicationConstants;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortUrlRequest {
    @NotEmpty
    @Pattern(regexp = ApplicationConstants.URL_REGEX,message = "is Invalid, Please Provide a Valid URL")
    private String longUrl;

    @NotNull
    private Long clientId;
}
