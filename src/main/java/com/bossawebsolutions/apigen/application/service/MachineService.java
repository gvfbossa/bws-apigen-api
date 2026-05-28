package com.bossawebsolutions.apigen.application.service;

import com.bossawebsolutions.apigen.domain.entity.Machine;
import com.bossawebsolutions.apigen.domain.entity.User;
import com.bossawebsolutions.apigen.domain.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;

    public Optional<Machine> findByUserAndMachineHash(User user, String machineHash) {
        return machineRepository.findByUserAndMachineHash(user, machineHash);
    }

    public long countMachinesByUser(User user) {
        return machineRepository.countByUser(user);
    }

    public Machine registerMachine(User user, String machineHash) {
        Machine machine = new Machine();
        machine.setUser(user);
        machine.setMachineHash(machineHash);

        return machineRepository.save(machine);
    }

}