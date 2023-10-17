package com.marqeta.referenceapp.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marqeta.referenceapp.TransactionAmountItem;
import com.marqeta.referenceapp.common.Constants;
import com.marqeta.referenceapp.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static com.marqeta.referenceapp.TransactionAmountItem.getValueInTransactionAmountMap;

@RestController
@Log4j2
public class WebhookController {
    private final Map<String, TransactionAmountItem[]> transactionAmountMap;
    private final ObjectMapper mapper;

    public WebhookController(Map<String, TransactionAmountItem[]> transactionAmountMap, ObjectMapper mapper) {
        this.transactionAmountMap = transactionAmountMap;
        this.mapper = mapper;
    }

    private BigDecimal getOriginalHoldAmount(JitFunding jitFunding, String originatingTransaction) {
        BigDecimal originalHold = null;
        if (jitFunding != null) {
            String jitToken = jitFunding.getToken();
            if (jitFunding.getOriginalJitFundingToken() != null) {
                jitToken = jitFunding.getOriginalJitFundingToken();
            }
            if (originatingTransaction != null && !originatingTransaction.trim().isEmpty()) {
                originalHold = getValueInTransactionAmountMap(transactionAmountMap, jitToken, originatingTransaction);
            }
        }
        return originalHold;
    }

    private void removeOriginalHoldAmount(JitFunding jitFunding, String originatingTransaction) {
        if (jitFunding != null) {
            String jitToken = jitFunding.getToken();
            if (jitFunding.getOriginalJitFundingToken() != null) {
                jitToken = jitFunding.getOriginalJitFundingToken();
            }
            if (originatingTransaction != null && !originatingTransaction.trim().isEmpty()) {
                log.info("All funding for jit token {} have settled", jitToken);
                transactionAmountMap.remove(jitToken + ":" + originatingTransaction);
            }
        }
    }

    @PostMapping("/webhook")
    public void webhookHandler(@RequestBody JsonNode body) throws JsonProcessingException {
        FundingNotification fundingNotification = mapper.treeToValue(body, FundingNotification.class);
        for (Transaction transaction : Objects.requireNonNull(fundingNotification).getTransactions()) {
            String type = transaction.getType();
            String state = transaction.getState();
            String txToken = transaction.getToken();

            GpaOrderBase gpaOrder;
            log.info("Received webhook event from Marqeta type: {} , state: {}, token: {}", type, state, txToken);
            gpaOrder = transaction.getGpaOrder();
            if (gpaOrder == null) {
                gpaOrder = transaction.getGpaOrderUnload();
            }
            JitFunding jitFunding = gpaOrder.getJitFunding();
            String jitMethod = jitFunding.getMethod();
            switch (type) {
                case Constants.AUTHORIZATION, Constants.AUTHORIZATION_INCREMENTAL:
                    log.info("Webhook event payload : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
                    BigDecimal originalHold = this.getOriginalHoldAmount(jitFunding, txToken);
                    if (state.equals(Constants.PENDING)) {
                        if (originalHold != null) {
                            log.info("Temporary hold of {} on the ledger balance stays for transaction {}", originalHold, txToken);
                        }
                    } else if (state.equals(Constants.DECLINED)) {
                        log.info("Transaction {}: Authorization was declined", txToken);
                        if (transaction.getGpaOrder() != null) {
                            GpaOrder.GatewayLog glog = transaction.getGpaOrder().getFunding().getGatewayLog();
                            log.info("Transaction {}: Gateway log object details {} ", txToken, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(glog));
                        }
                        if (originalHold != null) {
                            log.info("Remove a temporary hold of {} on ledger balance for transaction {}", originalHold, txToken);
                        }
                    }
                    break;
                case Constants.AUTHORIZATION_REVERSAL:
                    log.info("Webhook event payload : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
                    String originatingTransaction = transaction.getPrecedingRelatedTransactionToken();
                    originalHold = this.getOriginalHoldAmount(jitFunding, originatingTransaction);
                    if (state.equals(Constants.CLEARED)) {
                        if (originalHold != null) {
                            log.info("Remove a temporary hold of {} on ledger balance for transaction {}", originalHold, txToken);
                        }
                    }
                    break;
                case Constants.AUTHORIZATION_ADVICE:
                    log.info("Webhook event payload {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
                    if (state.equals(Constants.PENDING)) {
                        if (jitMethod != null && jitMethod.equalsIgnoreCase("pgfs.authorization.reversal")) {
                            originatingTransaction = transaction.getPrecedingRelatedTransactionToken();
                            originalHold = this.getOriginalHoldAmount(jitFunding, originatingTransaction);
                            if (originalHold != null) {
                                log.info("Originating transaction event is {} for transaction {} for which a hold of {} was placed", originatingTransaction, txToken, originalHold);
                                BigDecimal impactedAmount = transaction.getGpa().getImpactedAmount();
                                log.info("As a result of advice transaction {} from the original hold was reversed. Temporary hold adjusted to {}", impactedAmount.abs(), originalHold.add(impactedAmount));
                            }
                        }
                    }
                    break;
                case Constants.AUTHORIZATION_CLEARING, Constants.PINDEBIT:
                    log.info("Webhook event payload {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
                    Boolean forceCapture = false;
                    BigDecimal impactedAmount = transaction.getGpa().getImpactedAmount();
                    if (state.equals(Constants.COMPLETION)) {
                        if (jitMethod != null && jitMethod.equalsIgnoreCase("pgfs.force_capture")) {
                            log.info("Clearing {} represents a force capture for which an authorization event may not be present", txToken);
                            forceCapture = true;
                        }
                    }
                    if (!forceCapture) {
                        originatingTransaction = transaction.getPrecedingRelatedTransactionToken();
                        if (originatingTransaction != null && !originatingTransaction.trim().isEmpty()) {
                            log.info("Originating transaction event is {} for transaction {}", originatingTransaction, txToken);
                            originalHold = this.getOriginalHoldAmount(jitFunding, originatingTransaction);
                            if (originalHold != null) {
                                if (originalHold.compareTo(impactedAmount.abs()) == 1) {
                                    log.info("Clearing {} amount was less than the hold of the originating transaction", txToken);
                                    log.info("Temporary hold removed only for impacted_amount in clearing transaction {}. Remainder hold {} stays.", txToken, originalHold.subtract(impactedAmount.abs()));
                                    log.info("Subsequent clearing message(s) can be sent for remainder amount before authorization {} expiry ", originatingTransaction);
                                } else {
                                    if (originalHold.compareTo(impactedAmount.abs()) == -1) {
                                        log.info("Clearing {} amount was greater than the hold of the Originating transaction event amount", txToken);
                                        log.info("Ledger balance get affected only based on clearing amount in transaction {}", txToken);
                                    }
                                    log.info("Remove hold of {} on ledger balance", originalHold);
                                    this.removeOriginalHoldAmount(jitFunding, originatingTransaction);
                                }
                            }
                        }
                        log.info("Decrease ledger balance by {} for webhook {}", impactedAmount.abs(), txToken);
                    }
                    break;

                case Constants.REFUND:
                    log.info("Webhook event payload {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
                    impactedAmount = transaction.getGpa().getImpactedAmount();
                    if (state.equals(Constants.COMPLETION)) {
                        log.info("Increase Ledger balance by {} for webhook {}", impactedAmount.abs(), txToken);
                    }
                    break;
                default:
                    log.info("Type: {} State:{} Event not handled. Some webhooks are info only", type, state);
                    // log.info("Webhook event payload : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
                    // best practise is to log events not expected for latter review / add support
            }
        }
    }
}
