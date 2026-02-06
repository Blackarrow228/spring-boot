package org.example.springboot.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class EmployeeDto {
    private String firstName;
    private String lastName;
    private String position;
    private BigDecimal salary;
    private UUID departmentId;
}
