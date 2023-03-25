package com.example.present.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.present.config.JwtProvider;
import com.example.present.entity.Employee;
import com.example.present.entity.JwtTokenResponse;
import com.example.present.entity.LoginRequest;
import com.example.present.repositories.EmployeeRepository;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtProvider tokenProvider;
	
	@Autowired
	private EmployeeRepository repository;
	
	@GetMapping
	public List<Employee> findAll(){
		return this.repository.findAll();
	}

	@PostMapping("/login")
	public ResponseEntity<JwtTokenResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateTokenUsingUserName(loginRequest.getUsername());

		return new ResponseEntity<>(new JwtTokenResponse(jwt), HttpStatus.OK);
		
	}
	
	@GetMapping("{id}")
	public Employee getById(@PathVariable Integer id){
		return this.repository.getReferenceById(id);
	}
	
	@PostMapping
	public Employee create(@RequestBody Employee employee) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
		return this.repository.save(employee);
	}
	
	@PutMapping("{id}")
	public Employee update(@PathVariable Integer id ,@RequestBody Employee employee ){
		Employee exits = this.repository.getReferenceById(id);
		BeanUtils.copyProperties(employee, exits,"id");
		return this.repository.saveAndFlush(exits);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable Integer id) {
		this.repository.deleteById(id);
	}
}
