package com.bossawebsolutions.apigen.application.dto;

import com.bossawebsolutions.apigen.domain.model.EntityMeta;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateRequest {

    private List<EntityMeta> entities;
    private String basePackage;

}