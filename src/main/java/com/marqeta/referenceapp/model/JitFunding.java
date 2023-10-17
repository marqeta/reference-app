package com.marqeta.referenceapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JitFunding {
    private String token;
    private String method;
    private String userToken;
    private String actingUserToken;
    private BigDecimal amount;
    private String originalJitFundingToken;
}
