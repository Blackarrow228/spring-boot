package org.example.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Department;
import org.example.springboot.service.DepartmentService;
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
@RequestMapping("api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.createDepartment(department));
    }

    @GetMapping("/{id}")
    public Department getDepartment(@PathVariable UUID id) {
        return departmentService.getDepartment(id);
    }

    @GetMapping
    public List<Department> getAllDepartment() {
        return departmentService.getAllDepartment();
    }

    @PatchMapping("/{id}")
    public Department updateDepartment(@RequestBody Department department, @PathVariable UUID id) {
            return departmentService.updateDepartment(id, department);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteDepartment(@PathVariable UUID id) {
        return departmentService.deleteDepartment(id);
    }
}
