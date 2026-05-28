package com.bossawebsolutions.apigen.application.utils;

import com.bossawebsolutions.apigen.domain.Plan;
import org.springframework.stereotype.Component;

@Component
public class SystemUtils {

    public int machineLimit(Plan plan) {
        return switch (plan) {
            case ADMIN -> 9999;
            case SOLO -> 1;
            case SMALL -> 15;
            case FULL -> 50;
        };
    }

}
