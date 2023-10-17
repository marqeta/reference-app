package com.marqeta.referenceapp.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FundingRequest {
    private String type;
    private String state;
    private String token;
    private String userToken;
    private String actingUserToken;
    private String cardToken;
    private String cardProductToken;
    private JsonNode gpa;
    private GpaOrder gpaOrder;
    private String createdTime;
    private String userTransactionTime;
    private String settlementDate;
    private Double requestAmount;
    private Double amount;
    private JsonNode currencyConversion;
    private String currencyCode;
    private String network;
    private String subnetwork;
    private Integer acquirerFeeAmount;
    private JsonNode acquirer;
    private JsonNode user;
    private JsonNode card;
    private JsonNode fraud;
    private JsonNode cardholderAuthenticationData;
    private String issuerReceivedTime;
    private String issuerPaymentNode;
    private String networkReferenceId;
    private String acquirerReferenceData;
    private JsonNode cardAcceptor;
    private JsonNode pos;
    private JsonNode transactionMetadata;
}
