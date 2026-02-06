package org.example.springboot.projection;

import org.springframework.beans.factory.annotation.Value;

public interface EmployeeProjection {
    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
    @Value("#{target.position}")
    String getPosition();
    @Value("#{target.department.name}")
    String getDepartmentName();
}
