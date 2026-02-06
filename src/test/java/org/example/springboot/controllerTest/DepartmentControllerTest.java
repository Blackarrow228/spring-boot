package org.example.springboot.controllerTest;

import org.example.springboot.controller.DepartmentController;
import org.example.springboot.dto.EmployeeDto;
import org.example.springboot.entity.Department;
import org.example.springboot.entity.Employee;
import org.example.springboot.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DepartmentService departmentService;

    private UUID departmentId;

    @BeforeEach
    void setUp() {
        departmentId = UUID.randomUUID();
    }

    @Test
    void createDepartmentTest() throws Exception {
        Department department = new Department();
        department.setName("h");

        Department savedDepartment = new Department();
        savedDepartment.setId(departmentId);
        savedDepartment.setName("h");

        when(departmentService.createDepartment(any(Department.class))).thenReturn(savedDepartment);

        mockMvc.perform(post("/api/v1/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(savedDepartment)))
                .andDo(print());
    }

    @Test
    void getDepartmentTest() throws Exception {
        Department department = new Department();
        department.setId(departmentId);
        department.setName("h");

        when(departmentService.getDepartment(departmentId)).thenReturn(department);

        mockMvc.perform(get("/api/v1/department/{id}", departmentId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(department)))
                .andDo(print());
    }

    @Test
    void getAllDepartment() throws Exception {
        Department department = new Department();
        Department department1 = new Department();
        department.setId(departmentId);
        department.setName("h");
        department1.setId(departmentId);
        department1.setName("f");
        List<Department> departmentList = List.of(department, department1);

        when(departmentService.getAllDepartment()).thenReturn(departmentList);

        mockMvc.perform(get("/api/v1/department"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(departmentList)))
                .andDo(print());
    }

    @Test
    void updateDepartment() throws Exception {
        Department department = new Department();
        department.setName("f");

        Department updatedDepartment = new Department();
        updatedDepartment.setId(departmentId);
        updatedDepartment.setName("f");

        when(departmentService.updateDepartment(any(UUID.class), any(Department.class)))
                .thenReturn(updatedDepartment);

        mockMvc.perform(patch("/api/v1/department/{id}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(updatedDepartment)))
                .andDo(print());
    }

    @Test
    void deleteEmployee() throws Exception {
        when(departmentService.deleteDepartment(departmentId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/department/{id}", departmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }
}
