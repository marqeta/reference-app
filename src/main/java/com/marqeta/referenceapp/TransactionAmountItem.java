package com.marqeta.referenceapp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@Getter
@Setter
@ToString
@Component
public class TransactionAmountItem {
    private String txToken;
    private BigDecimal amount;

    public TransactionAmountItem() {

    }
    public TransactionAmountItem(String txToken, BigDecimal amount) {
        this.txToken = txToken;
        this.amount = amount;
    }

    public static void putValueInTransactionAmountMap(Map<String, TransactionAmountItem[]> map, String jitToken, TransactionAmountItem item) {
        map.compute(jitToken, (k, existingItems) -> {
            if (existingItems == null) {
                return new TransactionAmountItem[]{item};
            } else {
                int length = existingItems.length;
                TransactionAmountItem[] newArray = Arrays.copyOf(existingItems, length + 1);
                newArray[length] = item;
                return newArray;
            }
        });
    }

    public static BigDecimal getValueInTransactionAmountMap(Map<String, TransactionAmountItem[]> map, String jitToken, String txToken) {
        TransactionAmountItem[] items = map.get(jitToken);
        if (items != null) {
            for (TransactionAmountItem item : items) {
                if (item.getTxToken().equals(txToken)) {
                    return item.getAmount();
                }
            }
            // If the specific txToken is not present return the first available value (most likely the auth)
            return items[0].getAmount();
        }
        return null;
    }
}
