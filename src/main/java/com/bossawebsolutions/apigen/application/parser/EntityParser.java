package com.bossawebsolutions.apigen.application.parser;

import com.bossawebsolutions.apigen.domain.model.EntityMeta;
import com.bossawebsolutions.apigen.domain.model.FieldMeta;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EntityParser {

    public EntityMeta parse(File file) throws Exception {

        CompilationUnit cu = StaticJavaParser.parse(file);

        String source = Files.readString(file.toPath());

        ClassOrInterfaceDeclaration clazz =
                cu.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();

        EntityMeta entity = new EntityMeta();
        entity.setName(clazz.getNameAsString());
        entity.setSource(source);

        List<FieldMeta> fields = new ArrayList<>();

        for (FieldDeclaration field : clazz.getFields()) {

            String type = field.getElementType().asString();
            String name = field.getVariable(0).getNameAsString();

            FieldMeta meta = new FieldMeta();
            meta.setName(name);
            meta.setType(type);

            fields.add(meta);

            if (field.isAnnotationPresent("Id")) {
                entity.setIdField(name);
                entity.setIdType(type);
            }
        }

        entity.setFields(fields);

        return entity;
    }

}