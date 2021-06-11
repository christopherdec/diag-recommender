package com.example.computerconfiguration.base;

import com.example.computerconfiguration.domain.computer.Computer;
import com.example.computerconfiguration.repository.ComputerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Spring component that stores the computer configurations base,
 * which is used for the construction of the similarity table.
 * Updates are invoked from the service class
 */
@Component
public class ComputerBase {

    @Autowired
    ComputerRepository computerRepository;

    public static List<Computer> base = ComputerLoader.getInstance().getArtificialConfigurations();

    public static Optional<Computer> contains(Computer computer) {
        Computer found = null;
        for(Computer entry : base) {
            if (computer.equals(entry)) {
                found = entry;
            }
        }
        return Optional.ofNullable(found);
    }

    @Bean
    public void setup() {
        if(computerRepository.findAll().isEmpty()) {
            computerRepository.saveAll(base);
        }
        update();
    }

    public void update() {
        base = computerRepository.findAll();
    }
}
