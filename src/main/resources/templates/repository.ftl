package ${package}.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ${entityPackage}.${entity.name};

public interface ${entity.name}Repository extends JpaRepository<${entity.name}, ${entity.idType}> {

}