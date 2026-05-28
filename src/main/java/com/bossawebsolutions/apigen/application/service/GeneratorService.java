package com.bossawebsolutions.apigen.application.service;

import com.bossawebsolutions.apigen.application.cli.GeneratorRunner;
import com.bossawebsolutions.apigen.application.generator.*;
import com.bossawebsolutions.apigen.domain.model.EntityMeta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratorService {

    public byte[] generate(List<EntityMeta> entities, String basePackage) throws Exception {

        List<CodeGenerator> generators = List.of(
                new DTOGenerator(),
                new MapperGenerator(),
                new RepositoryGenerator(),
                new ServiceGenerator(),
                new ControllerGenerator()
        );

        GeneratorRunner runner = new GeneratorRunner(generators);

        return runner.run(entities, basePackage);
    }

}