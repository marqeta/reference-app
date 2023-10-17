package com.marqeta.referenceapp;

import com.marqeta.referenceapp.common.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Log4j2

public class SettingsController {
    private static Map<String, Boolean> settings;

    public SettingsController(Map<String, Boolean> settingsMap) {
        this.settings = settingsMap;
    }

    @PostMapping("/settings")
    public void setDelay(@RequestParam(required = false) String delay, @RequestParam(required = false) String decline) {
        settings.put(Constants.DELAY, Boolean.parseBoolean(delay));
        settings.put(Constants.DECLINE, Boolean.parseBoolean(decline));
    }

}
