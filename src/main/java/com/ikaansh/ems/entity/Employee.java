package com.ikaansh.ems.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "employees", uniqueConstraints = @UniqueConstraint(name = "uk_employees_owner_email", columnNames = {"owner_id", "email"}), indexes = @Index(name = "idx_employees_owner", columnList = "owner_id"))
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_employees_owner"))
    private AppUser owner;
    @Column(nullable = false, length = 100) private String name;
    @Column(nullable = false, length = 190) private String email;
    @Column(nullable = false, length = 80) private String role;
    @Column(nullable = false, length = 100) private String department;
    @Column(name = "hire_date", nullable = false) private LocalDate hireDate;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected Employee() {}
    public Employee(AppUser owner, String name, String email, String role, String department, LocalDate hireDate) {
        this.owner = owner; update(name, email, role, department, hireDate);
    }
    public void update(String name, String email, String role, String department, LocalDate hireDate) {
        this.name = name; this.email = email; this.role = role; this.department = department; this.hireDate = hireDate; this.updatedAt = Instant.now();
    }
    @PrePersist void beforeInsert() { Instant now = Instant.now(); if (createdAt == null) createdAt = now; if (updatedAt == null) updatedAt = now; }
    @PreUpdate void beforeUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public AppUser getOwner() { return owner; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public LocalDate getHireDate() { return hireDate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
