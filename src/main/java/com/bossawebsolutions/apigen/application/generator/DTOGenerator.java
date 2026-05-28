package com.bossawebsolutions.apigen.application.generator;

import com.bossawebsolutions.apigen.domain.model.EntityMeta;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class DTOGenerator implements CodeGenerator {

    @Override
    public Map<String, String> generate(EntityMeta entity, String basePackage) throws Exception {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");

        Template template = cfg.getTemplate("dto.ftl");

        Map<String, Object> data = new HashMap<>();
        data.put("package", basePackage + ".generated.dto");
        data.put("entityPackage", entity.getPackageName());
        data.put("entity", entity);

        StringWriter writer = new StringWriter();
        template.process(data, writer);

        Map<String, String> files = new HashMap<>();
        files.put("generated/dto/" + entity.getName() + "DTO.java", writer.toString());

        return files;
    }
}