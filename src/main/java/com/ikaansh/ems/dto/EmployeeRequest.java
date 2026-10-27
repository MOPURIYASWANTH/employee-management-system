package com.ikaansh.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EmployeeRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 190) String email,
    @NotBlank @Size(max = 80) String role,
    @NotBlank @Size(max = 100) String department,
    @NotNull LocalDate hireDate
) {}
