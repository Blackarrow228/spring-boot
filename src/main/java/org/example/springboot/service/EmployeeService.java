package org.example.springboot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.EmployeeDto;
import org.example.springboot.entity.Department;
import org.example.springboot.entity.Employee;
import org.example.springboot.projection.EmployeeProjection;
import org.example.springboot.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    @Transactional
    public Employee createEmployee(EmployeeDto employeeDto) {
        Department department = departmentService.getDepartment(employeeDto.getDepartmentId());
        Employee employee = new Employee();
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setPosition(employeeDto.getPosition());
        employee.setSalary(employeeDto.getSalary());
        employee.setDepartment(department);
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeProjection getEmployee(UUID id) {
        return employeeRepository.findProjectionById(id).orElseThrow(() -> new EntityNotFoundException("Employee с id: " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<EmployeeProjection> getAllEmployee() {
        return employeeRepository.findAllProjectedBy();
    }

    @Transactional
    public Employee updateEmployee(UUID id, EmployeeDto employeeDto) {
        Employee emp = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee с id: " + id + " не найден"));
        if (employeeDto.getDepartmentId() != null) {
            Department department = departmentService.getDepartment(employeeDto.getDepartmentId());
            emp.setDepartment(department);
        }
        if (employeeDto.getSalary() != null) {
            emp.setSalary(employeeDto.getSalary());
        }
        if (employeeDto.getFirstName() != null && !employeeDto.getFirstName().isBlank()) {
            emp.setFirstName(employeeDto.getFirstName());
        }
        if (employeeDto.getLastName() != null && !employeeDto.getLastName().isBlank()) {
            emp.setLastName(employeeDto.getLastName());
        }
        if (employeeDto.getPosition() != null && !employeeDto.getPosition().isBlank()) {
            emp.setPosition(employeeDto.getPosition());
        }
        return emp;
    }

    @Transactional
    public Boolean deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
        return employeeRepository.existsById(id);
    }
}
