package org.example.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.EmployeeDto;
import org.example.springboot.entity.Employee;
import org.example.springboot.projection.EmployeeProjection;
import org.example.springboot.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(employeeDto));
    }

    @GetMapping("/{id}")
    public EmployeeProjection getEmployee(@PathVariable UUID id) {
        return employeeService.getEmployee(id);
    }

    @GetMapping
    public List<EmployeeProjection> getAllEmployee() {
        return employeeService.getAllEmployee();
    }

    @PatchMapping("/{id}")
    public Employee updateEmployee(@RequestBody EmployeeDto employeeDto, @PathVariable UUID id) {
            return employeeService.updateEmployee(id, employeeDto);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteEmployee(@PathVariable UUID id) {
        return employeeService.deleteEmployee(id);
    }
}
