package com.ikaansh.ems.service;

import com.ikaansh.ems.dto.EmployeeRequest;
import com.ikaansh.ems.dto.EmployeeResponse;
import com.ikaansh.ems.entity.AppUser;
import com.ikaansh.ems.entity.Employee;
import com.ikaansh.ems.exception.ConflictException;
import com.ikaansh.ems.exception.ResourceNotFoundException;
import com.ikaansh.ems.repository.EmployeeRepository;
import com.ikaansh.ems.repository.UserRepository;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private final EmployeeRepository employees; private final UserRepository users;
    public EmployeeService(EmployeeRepository employees, UserRepository users) { this.employees = employees; this.users = users; }
    @Transactional public EmployeeResponse create(String email, EmployeeRequest request) {
        AppUser owner = owner(email); String employeeEmail = normalize(request.email());
        if (employees.existsByOwnerIdAndEmailIgnoreCase(owner.getId(), employeeEmail)) throw new ConflictException("An employee with this email already exists in your records.");
        return EmployeeResponse.from(employees.save(new Employee(owner, request.name().trim(), employeeEmail, request.role().trim(), request.department().trim(), request.hireDate())));
    }
    @Transactional(readOnly = true) public Page<EmployeeResponse> list(String email, int page, int size, String department) {
        if (page < 0) throw new IllegalArgumentException("page must be zero or greater.");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be between 1 and 100.");
        Long ownerId = owner(email).getId(); PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Employee> result = department == null || department.isBlank() ? employees.findByOwnerId(ownerId, pageable) : employees.findByOwnerIdAndDepartmentContainingIgnoreCase(ownerId, department.trim(), pageable);
        return result.map(EmployeeResponse::from);
    }
    @Transactional(readOnly = true) public EmployeeResponse get(String email, Long id) { return EmployeeResponse.from(findOwned(email, id)); }
    @Transactional public EmployeeResponse update(String email, Long id, EmployeeRequest request) {
        Employee employee = findOwned(email, id); String normalized = normalize(request.email());
        if (employees.existsByOwnerIdAndEmailIgnoreCaseAndIdNot(employee.getOwner().getId(), normalized, id)) throw new ConflictException("Another employee record already uses this email.");
        employee.update(request.name().trim(), normalized, request.role().trim(), request.department().trim(), request.hireDate());
        return EmployeeResponse.from(employee);
    }
    @Transactional public void delete(String email, Long id) { employees.delete(findOwned(email, id)); }
    private Employee findOwned(String email, Long id) { return employees.findByIdAndOwnerId(id, owner(email).getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found.")); }
    private AppUser owner(String email) { return users.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists.")); }
    private String normalize(String value) { return value.trim().toLowerCase(Locale.ROOT); }
}
