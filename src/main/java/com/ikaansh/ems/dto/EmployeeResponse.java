package com.ikaansh.ems.dto;

import com.ikaansh.ems.entity.Employee;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeResponse(Long id, String name, String email, String role, String department, LocalDate hireDate, Instant createdAt, Instant updatedAt) {
    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(e.getId(), e.getName(), e.getEmail(), e.getRole(), e.getDepartment(), e.getHireDate(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
