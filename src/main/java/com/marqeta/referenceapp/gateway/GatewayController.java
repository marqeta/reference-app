package com.marqeta.referenceapp.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marqeta.referenceapp.common.Constants;
import com.marqeta.referenceapp.model.FundingRequest;
import com.marqeta.referenceapp.model.FundingResponse;
import com.marqeta.referenceapp.model.JitFunding;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Log4j2
public class GatewayController {
    // Map to maintain the transaction amount per transaction token between gateway and webhook handler
    private final ObjectMapper mapper;
    private static Map<String, Boolean> settings;


    public GatewayController(ObjectMapper mapper, Map<String, Boolean> settingsMap) {
        this.mapper = mapper;
        this.settings = settingsMap;
    }

    @PostMapping("/gateway")
    public ResponseEntity gatewayHandler(@RequestBody JsonNode request) throws IOException, InterruptedException {
        log.info("Received gateway request from Marqeta {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));

        FundingRequest fundingRequest = mapper.treeToValue(request, FundingRequest.class);
        String type = fundingRequest.getType();
        String state = fundingRequest.getState();
        String txToken = fundingRequest.getToken();
        JitFunding jitFunding = fundingRequest.getGpaOrder().getJitFunding();
        BigDecimal amount = jitFunding.getAmount();
        String jitToken = jitFunding.getToken();
        if (jitFunding.getOriginalJitFundingToken() != null) {
            jitToken = jitFunding.getOriginalJitFundingToken();
        }

        log.info("Transaction: {}, Type: {}, State: {}", txToken, type, state);
        log.info("Amount presented for approval or denial in the jit_funding object {}", amount);

        FundingResponse fundingResponse = new FundingResponse(jitFunding);
        if (!GatewayService.shouldApprove()) {
            log.info("Declining funding request {}. No changes to any ledger balances for transaction", txToken);
            return new ResponseEntity<>(fundingResponse, HttpStatus.PAYMENT_REQUIRED);
        }

        if (settings.get(Constants.DELAY)) {
            log.info("Simulating a delay of 4 seconds before sending response..");
            // Simulate artificial delay in sending the funding response. Marqeta by default waits only for 3 seconds
            TimeUnit.SECONDS.sleep(4);
        }

        log.info("Approving funding request {} with 200 OK and payload " + "\n{}", txToken, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fundingResponse));

        GatewayService.adjustBalances(type, state, jitToken, txToken, amount);

        return new ResponseEntity<>(fundingResponse, HttpStatus.OK);
    }
}
