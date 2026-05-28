package ${package}.mapper;

import ${entityPackage}.${entity.name};
import ${package}.dto.${entity.name}DTO;

public class ${entity.name}Mapper {

    public static ${entity.name}DTO toDTO(${entity.name} entity) {
        return new ${entity.name}DTO(
<#list entity.fields as field>
            entity.get${field.name?cap_first}()<#if field_has_next>,</#if>
</#list>
        );
    }

    public static ${entity.name} toEntity(${entity.name}DTO dto) {

        ${entity.name} entity = new ${entity.name}();

<#list entity.fields as field>
<#if field.name != entity.idField>
        entity.set${field.name?cap_first}(dto.${field.name}());
</#if>
</#list>

        return entity;
    }

}