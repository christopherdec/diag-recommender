package com.example.computerconfiguration.repository;

import com.example.computerconfiguration.domain.computer.Computer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComputerRepository extends JpaRepository<Computer, Long> {

}
