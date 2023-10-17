package com.marqeta.referenceapp.gateway;

import com.marqeta.referenceapp.common.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GatewayServiceTest {
    @MockBean
    private Map<String, BigDecimal> transactionAmountMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAffectAvailableBalance() {
        assertTrue(GatewayService.shouldAffectAvailableBalance(Constants.PINDEBIT, Constants.COMPLETION));
        assertTrue(GatewayService.shouldAffectAvailableBalance(Constants.AUTHORIZATION, Constants.PENDING));
    }

    @Test
    void shouldAffectAvailableBalance_WhenTypeIsNotPinDebitAndStateIsNotCompletion_ReturnsFalse() {
        assertFalse(GatewayService.shouldAffectAvailableBalance("SOMETYPE", "SOMESTATE"));
    }

    @Test
    void shouldApprove() {
        assertTrue(GatewayService.shouldApprove());
    }

}
