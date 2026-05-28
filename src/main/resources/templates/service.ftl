package ${package};

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import ${repositoryPackage}.${entity.name}Repository;
import ${entityPackage}.${entity.name};

@Service
@RequiredArgsConstructor
public class ${entity.name}Service {

    private final ${entity.name}Repository repository;

    public List<${entity.name}> listAll() {
        return repository.findAll();
    }

    public ${entity.name} findById(${entity.idType} id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("${entity.name} not found"));
    }

    public ${entity.name} create(${entity.name} entity) {
        return repository.save(entity);
    }

    public ${entity.name} update(${entity.idType} id, ${entity.name} newEntity) {

        ${entity.name} entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("${entity.name} not found"));

<#list entity.fields as field>
<#if field.name != entity.idField>
        entity.set${field.name?cap_first}(newEntity.get${field.name?cap_first}());
</#if>
</#list>

        return repository.save(entity);
    }

    public void delete(${entity.idType} id) {
        repository.deleteById(id);
    }
}