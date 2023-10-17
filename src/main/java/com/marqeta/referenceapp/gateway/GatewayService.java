package com.marqeta.referenceapp.gateway;

import com.marqeta.referenceapp.TransactionAmountItem;
import com.marqeta.referenceapp.common.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Log4j2
@Service
public class GatewayService {
    public static Map<String, TransactionAmountItem[]> transactionAmountMap;

    private static Map<String, Boolean> settingsMap;

    @Autowired
    public GatewayService(Map<String, TransactionAmountItem[]> transactionAmountMap, Map<String, Boolean> settingsMap) {
        this.transactionAmountMap = transactionAmountMap;
        this.settingsMap = settingsMap;
    }

    public static boolean shouldAffectAvailableBalance(String type, String state) {
        boolean pindebit = type.equals(Constants.PINDEBIT) && state.equals(Constants.COMPLETION);
        boolean pendingAuth = type.equals(Constants.AUTHORIZATION) && state.equals(Constants.PENDING);
        boolean pendingIncremental = type.equals(Constants.AUTHORIZATION_INCREMENTAL) && state.equals(Constants.PENDING);
        return pendingAuth || pindebit || pendingIncremental;
    }

    public static boolean shouldApprove() {
        // Your custom business logic goes here whether to allow or deny the funding request

        // For the purpose of this app the settings are controlled by the shared map
        return !settingsMap.get(Constants.DECLINE);
    }

    public static void adjustBalances(String type, String state, String jitToken, String txToken, BigDecimal amount) {
        if (GatewayService.shouldAffectAvailableBalance(type, state)) {
            log.info("For transaction {} place a temporary hold of {} on the ledger balance", txToken, amount);
            if (type.equals(Constants.AUTHORIZATION_INCREMENTAL)) {
                log.info(" This hold is in addition to the existing hold for jit funding {} ", jitToken);
            }
            TransactionAmountItem.putValueInTransactionAmountMap(transactionAmountMap, jitToken, new TransactionAmountItem(txToken, amount));
        }
    }
}
