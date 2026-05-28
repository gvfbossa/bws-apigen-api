package com.bossawebsolutions.apigen.domain.repository;

import com.bossawebsolutions.apigen.domain.entity.Machine;
import com.bossawebsolutions.apigen.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {

    List<Machine> findByUser(User user);

    Optional<Machine> findByUserAndMachineHash(User user, String machineHash);

    long countByUser(User user);

}