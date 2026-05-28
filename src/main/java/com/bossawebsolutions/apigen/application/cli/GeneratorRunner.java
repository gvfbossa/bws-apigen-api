package com.bossawebsolutions.apigen.application.cli;

import com.bossawebsolutions.apigen.application.generator.CodeGenerator;
import com.bossawebsolutions.apigen.domain.model.EntityMeta;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GeneratorRunner {

    private final List<CodeGenerator> generators;

    public GeneratorRunner(List<CodeGenerator> generators) {
        this.generators = generators;
    }

    /**
     * Executa a geração de código para uma lista de entidades usando o basePackage especificado.
     */
    public byte[] run(List<EntityMeta> entities, String basePackage) throws Exception {

        System.out.println("🚀 BWS ApiGen");

        if (entities == null || entities.isEmpty()) {
            System.out.println("No entities received.");
            return new byte[0];
        }

        Map<String, String> files = new HashMap<>();

        for (EntityMeta entity : entities) {

            if (!entity.hasGetters()) {
                System.out.println("⚠ Warning: Entity " + entity.getName() +
                        " may not have getters/setters.");
            }

            System.out.println("Processing entity: " + entity.getName());

            for (CodeGenerator generator : generators) {
                Map<String, String> generated = generator.generate(entity, basePackage);
                if (generated != null) {
                    files.putAll(generated);
                }
            }
        }

        return zip(files);
    }

    private byte[] zip(Map<String, String> files) throws Exception {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(baos)) {

            for (Map.Entry<String, String> entry : files.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue().getBytes());
                zip.closeEntry();
            }

            zip.finish();
            return baos.toByteArray();
        }
    }
}