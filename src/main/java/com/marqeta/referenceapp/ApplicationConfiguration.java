package com.marqeta.referenceapp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.marqeta.referenceapp.common.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Log4j2
public class ApplicationConfiguration {
    private Map<String, TransactionAmountItem[]> transactionAmountMap;
    private ObjectMapper mapper;
    private Map<String, Boolean> settingsMap;

    @Bean
    public Map<String, TransactionAmountItem[]> getTransactionAmountMap() {
        if (transactionAmountMap == null) {
            transactionAmountMap = new ConcurrentHashMap<>();
        }
        return transactionAmountMap;
    }

    @Bean
    public ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
        }
        return mapper;
    }

    @Bean
    public Map<String, Boolean> getSettingsMap() {
        if (settingsMap == null) {
            settingsMap = new ConcurrentHashMap<>();
            settingsMap.put(Constants.DELAY, false);
            settingsMap.put(Constants.DECLINE, false);
        }
        return settingsMap;
    }
}
