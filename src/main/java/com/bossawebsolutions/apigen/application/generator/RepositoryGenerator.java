package com.bossawebsolutions.apigen.application.generator;

import com.bossawebsolutions.apigen.domain.model.EntityMeta;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class RepositoryGenerator implements CodeGenerator {

    @Override
    public Map<String, String> generate(EntityMeta entity, String basePackage) throws Exception {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");

        Template template = cfg.getTemplate("repository.ftl");

        Map<String, Object> data = new HashMap<>();
        data.put("package", basePackage + ".generated");
        data.put("entity", entity);
        data.put("entityPackage", entity.getPackageName());

        StringWriter writer = new StringWriter();
        template.process(data, writer);

        Map<String, String> files = new HashMap<>();
        files.put("generated/repository/" + entity.getName() + "Repository.java", writer.toString());

        return files;
    }
}