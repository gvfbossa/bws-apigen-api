package com.bossawebsolutions.apigen.application.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Service
public class TemplateService {

    private final Configuration cfg;

    public TemplateService() {

        cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                "/templates"
        );
    }

    public String render(String templateName, Map<String, Object> data) throws Exception {

        Template template = cfg.getTemplate(templateName);

        StringWriter writer = new StringWriter();

        template.process(data, writer);

        return writer.toString();
    }
}