package com.ikaansh.ems.repository;

import com.ikaansh.ems.entity.Employee;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Employee> findByOwnerIdAndDepartmentContainingIgnoreCase(Long ownerId, String department, Pageable pageable);
    Optional<Employee> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByOwnerIdAndEmailIgnoreCase(Long ownerId, String email);
    boolean existsByOwnerIdAndEmailIgnoreCaseAndIdNot(Long ownerId, String email, Long id);
}
