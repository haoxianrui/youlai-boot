package com.youlai.system.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResult {

    private String accessToken;

    private String refreshToken;

    private Long expires;

}
