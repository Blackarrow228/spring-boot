package org.example.springboot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Department;
import org.example.springboot.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Department createDepartment(Department department) {
        if (department.getId() != null) {
            throw new IllegalArgumentException("Выполните метод update");
        }
        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public Department getDepartment(UUID id) {
        return departmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Department с id: " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartment() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department updateDepartment(UUID id, Department department) {
        Department dep = departmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Department с id: " + id + " не найден"));
        if (!department.getName().isBlank()) {
            dep.setName(department.getName());
        }
        return dep;
    }

    @Transactional
    public Boolean deleteDepartment(UUID id) {
        departmentRepository.deleteById(id);
        return departmentRepository.existsById(id);
    }


}
