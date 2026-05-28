package ${package};

public record ${entity.name}DTO(

<#list entity.fields as field>
    ${field.type} ${field.name}<#if field_has_next>,</#if>
</#list>

) {}