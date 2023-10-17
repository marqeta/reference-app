package com.marqeta.referenceapp.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GpaOrderBase {
    private String token;
    private BigDecimal amount;
    private String transactionToken;
    private String state;

    private String fundingSourceToken;
    private JitFunding jitFunding;

    private Funding funding;
    @Getter
    public static class GatewayLog {
        private String orderNumber;
        private String transactionId;
        private String message;
        private Integer duration;
        private Boolean timedOut;
        private JsonNode response;

    }
    @Getter
    public static class Funding {
        private BigDecimal amount;
        private JsonNode source;
        private GatewayLog gatewayLog;
    }

}
