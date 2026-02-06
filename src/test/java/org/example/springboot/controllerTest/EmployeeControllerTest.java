package org.example.springboot.controllerTest;

import org.example.springboot.controller.EmployeeController;
import org.example.springboot.dto.EmployeeDto;
import org.example.springboot.entity.Employee;
import org.example.springboot.projection.EmployeeProjection;
import org.example.springboot.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
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

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    private UUID employeeId;
    private UUID departmentId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        departmentId = UUID.randomUUID();
    }

    @Test
    void createEmployeeTest() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("abs");
        employeeDto.setLastName("sba");
        employeeDto.setPosition("h");
        employeeDto.setSalary(new BigDecimal("50000"));
        employeeDto.setDepartmentId(departmentId);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("abs");
        employee.setLastName("sba");

        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(employee);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(employee)))
                .andDo(print());
    }

    @Test
    void getEmployeeTest() throws Exception {
        EmployeeProjection projection = new EmployeeProjection() {
            @Override
            public String getFullName() {
                return "abs sba";
            }

            @Override
            public String getPosition() {
                return "h";
            }

            @Override
            public String getDepartmentName() {
                return "pos";
            }
        };

        when(employeeService.getEmployee(employeeId)).thenReturn(projection);

        mockMvc.perform(get("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(projection)))
                .andDo(print());
    }

    @Test
    void getAllEmployee() throws Exception {
        List<EmployeeProjection> employeeProjections = List.of(new EmployeeProjection() {
            @Override
            public String getFullName() {
                return "abs sba";
            }

            @Override
            public String getPosition() {
                return "h";
            }

            @Override
            public String getDepartmentName() {
                return "pos";
            }
        }, new EmployeeProjection() {
            @Override
            public String getFullName() {
                return "jasdfg arg";
            }

            @Override
            public String getPosition() {
                return "bag";
            }

            @Override
            public String getDepartmentName() {
                return "rt";
            }
        });

        when(employeeService.getAllEmployee()).thenReturn(employeeProjections);

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(employeeProjections)))
                .andDo(print());
    }

    @Test
    void updateEmployee() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("gar");
        employeeDto.setDepartmentId(departmentId);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(employeeId);
        updatedEmployee.setFirstName("gar");

        when(employeeService.updateEmployee(any(UUID.class), any(EmployeeDto.class)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(patch("/api/v1/employee/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(updatedEmployee)))
                .andDo(print());
    }

    @Test
    void deleteEmployee() throws Exception {
        when(employeeService.deleteEmployee(employeeId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }
}