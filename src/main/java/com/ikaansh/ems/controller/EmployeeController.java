package com.ikaansh.ems.controller;

import com.ikaansh.ems.dto.EmployeeRequest;
import com.ikaansh.ems.dto.EmployeeResponse;
import com.ikaansh.ems.service.EmployeeService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	private final EmployeeService service;

	public EmployeeController(EmployeeService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<EmployeeResponse> create(Authentication auth, @Valid @RequestBody EmployeeRequest request) {
		EmployeeResponse created = service.create(auth.getName(), request);
		return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
	}

	@GetMapping
	public Page<EmployeeResponse> list(Authentication auth, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String department) {
		return service.list(auth.getName(), page, size, department);
	}

	@GetMapping("/{id}")
	public EmployeeResponse get(Authentication auth, @PathVariable Long id) {
		return service.get(auth.getName(), id);
	}

	@PutMapping("/{id}")
	public EmployeeResponse update(Authentication auth, @PathVariable Long id,
			@Valid @RequestBody EmployeeRequest request) {
		return service.update(auth.getName(), id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
		service.delete(auth.getName(), id);
		return ResponseEntity.noContent().build();
	}
}
