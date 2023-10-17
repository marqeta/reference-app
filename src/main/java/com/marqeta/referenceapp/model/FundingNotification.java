package com.marqeta.referenceapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FundingNotification {
    private List<Transaction> transactions = new ArrayList<>();
}
