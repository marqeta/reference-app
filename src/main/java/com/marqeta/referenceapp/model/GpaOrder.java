package com.marqeta.referenceapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GpaOrder extends GpaOrderBase {
    private String userToken;
    private String currencyCode;
}
