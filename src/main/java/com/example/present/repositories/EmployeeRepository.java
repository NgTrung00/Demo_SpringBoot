package com.example.present.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.present.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	Employee findByName(String username);
}
