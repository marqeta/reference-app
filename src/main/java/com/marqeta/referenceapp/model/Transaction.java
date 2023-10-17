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
public class Transaction {
    private String type;
    private String state;
    private String identifier;
    private String token;
    private String userToken;
    private String actingUserToken;
    private String cardToken;
    private String cardProductToken;
    private Gpa gpa;
    private GpaOrder gpaOrder;
    private GpaOrderUnload gpaOrderUnload;
    private String precedingRelatedTransactionToken;
    private Integer duration;
    private String createdTime;
    private String userTransactionTime;
    private String settlementDate;
    private Double requestAmount;
    private Double amount;
    private String currencyCode;
    private String approvalCode;
    private JsonNode response;
    private String network;
    private Integer acquirerFeeAmount;
    private JsonNode acquirer;
    private JsonNode user;
    private JsonNode card;
    private String issuerReceivedTime;
    private String issuerPaymentNode;
    private String networkReferenceId;
    private JsonNode cardAcceptor;
    private JsonNode pos;


    @Getter
    public static class Gpa {
        private String currencyCode;
        private BigDecimal ledgerBalance;
        private BigDecimal availableBalance;
        private BigDecimal creditBalance;
        private BigDecimal pendingCredits;
        private BigDecimal impactedAmount;
        private JsonNode balances;
    }
}
