package com.bossawebsolutions.apigen.application.generator;

import com.bossawebsolutions.apigen.domain.model.EntityMeta;

import java.util.Map;

public interface CodeGenerator {

    /**
     * Gera os arquivos de código para a entidade passada, usando o basePackage fornecido.
     * @param entity entidade a gerar
     * @param basePackage package base do projeto
     * @return Map onde chave = caminho do arquivo relativo, valor = conteúdo do arquivo
     * @throws Exception
     */
    Map<String, String> generate(EntityMeta entity, String basePackage) throws Exception;
}