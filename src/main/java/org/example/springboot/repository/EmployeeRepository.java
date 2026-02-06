package org.example.springboot.repository;

import org.example.springboot.entity.Employee;
import org.example.springboot.projection.EmployeeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<EmployeeProjection> findProjectionById(UUID id);
    List<EmployeeProjection> findAllProjectedBy();

}
