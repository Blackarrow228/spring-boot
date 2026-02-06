package org.example.springboot.repositoryTest;

import jakarta.transaction.Transactional;
import org.example.springboot.entity.Department;
import org.example.springboot.entity.Employee;
import org.example.springboot.projection.EmployeeProjection;
import org.example.springboot.repository.DepartmentRepository;
import org.example.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;
    private Employee employee;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setName("h");
        department = departmentRepository.save(department);

        employee = new Employee();
        employee.setFirstName("abs");
        employee.setLastName("sba");
        employee.setPosition("ga");
        employee.setSalary(new BigDecimal("50000"));
        employee.setDepartment(department);
        employee = employeeRepository.save(employee);
    }

    @Test
    void saveEmployee() {
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("sad");
        newEmployee.setLastName("dfg");
        newEmployee.setPosition("er");
        newEmployee.setSalary(new BigDecimal("70000"));
        newEmployee.setDepartment(department);

        Employee saved = employeeRepository.save(newEmployee);
        print(saved);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("sad");
        assertThat(saved.getDepartment().getId()).isEqualTo(department.getId());
    }

    private void print(Employee saved) {
        System.out.println("id: " + saved.getId());
        System.out.println("FirstName: " + saved.getFirstName());
        System.out.println("LastName: " + saved.getLastName());
        System.out.println("Position: " + saved.getPosition());
        System.out.println("Salary: " + saved.getSalary());
        System.out.println("DepartmentName: " + saved.getDepartment().getName());
    }

    @Test
    void findById() {
        Optional<EmployeeProjection> found = employeeRepository.findProjectionById(employee.getId());
        if (found.isPresent()) {
            System.out.println("FullName: " + found.get().getFullName());
            System.out.println("Position: " + found.get().getPosition());
            System.out.println("DepartmentName: " + found.get().getDepartmentName());
        }

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("abs sba");
        assertThat(found.get().getPosition()).isEqualTo("ga");
        assertThat(found.get().getDepartmentName()).isEqualTo("h");
    }

    @Test
    void findAll() {
        Employee employee2 = new Employee();
        employee2.setFirstName("sad");
        employee2.setLastName("dfg");
        employee2.setPosition("er");
        employee2.setSalary(new BigDecimal("70000"));
        employee2.setDepartment(department);
        employeeRepository.save(employee2);

        List<EmployeeProjection> employees = employeeRepository.findAllProjectedBy();

        for (EmployeeProjection projection : employees) {
            System.out.println("FullName: " + projection.getFullName());
            System.out.println("Position: " + projection.getPosition());
            System.out.println("DepartmentName: " + projection.getDepartmentName());
        }

        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(EmployeeProjection::getFullName)
                .containsExactlyInAnyOrder("abs sba", "sad dfg");
    }

    @Test
    void deleteById() {
        UUID employeeId = employee.getId();

        employeeRepository.deleteById(employeeId);

        Optional<Employee> found = employeeRepository.findById(employeeId);
        assertThat(found).isEmpty();
    }
}