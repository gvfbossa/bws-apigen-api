package ${package}.controller;

import ${package}.service.${entity.name}Service;
import ${package}.dto.${entity.name}DTO;
import ${package}.mapper.${entity.name}Mapper;
import ${entityPackage}.${entity.name};

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${basePath}")
@RequiredArgsConstructor
public class ${entity.name}Controller {

    private final ${entity.name}Service service;

    @GetMapping
    public List<${entity.name}DTO> listAll() {
        return service.listAll()
                .stream()
                .map(${entity.name}Mapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ${entity.name}DTO getById(@PathVariable ${entity.idType} id) {
        ${entity.name} entity = service.findById(id);
        return ${entity.name}Mapper.toDTO(entity);
    }

    @PostMapping
    public ${entity.name}DTO create(@RequestBody ${entity.name}DTO dto) {
        ${entity.name} entity = ${entity.name}Mapper.toEntity(dto);
        entity = service.create(entity);
        return ${entity.name}Mapper.toDTO(entity);
    }

    @PutMapping("/{id}")
    public ${entity.name}DTO update(@PathVariable ${entity.idType} id,
                                    @RequestBody ${entity.name}DTO dto) {

        ${entity.name} entity = ${entity.name}Mapper.toEntity(dto);
        entity = service.update(id, entity);

        return ${entity.name}Mapper.toDTO(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable ${entity.idType} id) {
        service.delete(id);
    }
}