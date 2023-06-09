package com.dokidoki.userserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class OauthToken {
    private String access_token;
    private String token_type;
    private String scope;

    private String id_token;
}
