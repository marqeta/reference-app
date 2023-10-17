package com.marqeta.referenceapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionAmountItemTest {
    private TransactionAmountItem transactionAmountItem;
    private String txToken = "testTxToken";
    private BigDecimal amount = BigDecimal.valueOf(100.0);

    @BeforeEach
    void setUp() {
        transactionAmountItem = new TransactionAmountItem(txToken, amount);
    }

    @Test
    void testConstructor() {
        assertNotNull(transactionAmountItem);
        assertEquals(txToken, transactionAmountItem.getTxToken());
        assertEquals(amount, transactionAmountItem.getAmount());
    }

    @Test
    void testPutValueInTransactionAmountMap() {
        Map<String, TransactionAmountItem[]> map = new HashMap<>();
        String jitToken = "testJitToken";

        TransactionAmountItem.putValueInTransactionAmountMap(map, jitToken, transactionAmountItem);

        assertTrue(map.containsKey(jitToken));
        assertEquals(1, map.get(jitToken).length);
        assertEquals(txToken, map.get(jitToken)[0].getTxToken());
        assertEquals(amount, map.get(jitToken)[0].getAmount());
    }

    @Test
    void testGetValueInTransactionAmountMap() {
        Map<String, TransactionAmountItem[]> map = new HashMap<>();
        String jitToken = "testJitToken";

        assertNull(TransactionAmountItem.getValueInTransactionAmountMap(map, jitToken, txToken));

        TransactionAmountItem.putValueInTransactionAmountMap(map, jitToken, transactionAmountItem);
        assertEquals(amount, TransactionAmountItem.getValueInTransactionAmountMap(map, jitToken, txToken));
    }

    @Test
    void testGetValueInTransactionAmountMapReturnsFirstItemIfTxTokenNotFound() {
        Map<String, TransactionAmountItem[]> map = new HashMap<>();
        String jitToken = "testJitToken";
        String notPresentTxToken = "notpresentTxToken";

        TransactionAmountItem.putValueInTransactionAmountMap(map, jitToken, transactionAmountItem);
        assertEquals(amount, TransactionAmountItem.getValueInTransactionAmountMap(map, jitToken, notPresentTxToken));
        assertEquals(null, TransactionAmountItem.getValueInTransactionAmountMap(map, "notpresentjittoken", notPresentTxToken));
    }
}
