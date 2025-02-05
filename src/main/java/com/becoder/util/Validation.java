package com.becoder.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.becoder.dto.CategoryDto;
import com.becoder.dto.TodoDto;
import com.becoder.dto.TodoDto.StatusDto;
import com.becoder.dto.UserRequest;
import com.becoder.enums.TodoStatus;
import com.becoder.exception.ExistDataException;
import com.becoder.exception.ResourceNotFoundException;
import com.becoder.exception.ValidationException;
import com.becoder.repository.RoleRepository;
import com.becoder.repository.UserRepository;

@Component
public class Validation {

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private UserRepository userRepo;

	public void categoryValidation(CategoryDto categoryDto) {

		Map<String, Object> error = new LinkedHashMap<>();

		if (ObjectUtils.isEmpty(categoryDto)) {
			throw new IllegalArgumentException("category Object/JSON shouldn't be null or empty");
		} else {

			// validation name field
			if (ObjectUtils.isEmpty(categoryDto.getName())) {
				error.put("name", "name field is empty or null");
			} else {
				if (categoryDto.getName().length() < 3) {
					error.put("name", "name length min 10");
				}
				if (categoryDto.getName().length() > 100) {
					error.put("name", "name length max 100");
				}
			}

			// validation description
			if (ObjectUtils.isEmpty(categoryDto.getDescription())) {
				error.put("description", "description field is empty or null");
			}

			// validation isActive
			if (ObjectUtils.isEmpty(categoryDto.getIsActive())) {
				error.put("isActive", "isActive field is empty or null");
			} else {
				if (categoryDto.getIsActive() != Boolean.TRUE.booleanValue()
						&& categoryDto.getIsActive() != Boolean.FALSE.booleanValue()) {
					error.put("isActive", "invalid value isActive field ");
				}
			}
		}

		if (!error.isEmpty()) {
			throw new ValidationException(error);
		}
	}

	public void todoValidation(TodoDto todoDto) throws Exception {

		StatusDto reqStatus = todoDto.getStatus();
		Boolean statusFound = false;
		for (TodoStatus st : TodoStatus.values()) {
			if (st.getId().equals(reqStatus.getId())) {
				statusFound = true;
			}
		}
		if (!statusFound) {
			throw new ResourceNotFoundException("invalid status");
		}
	}

	public void userValidation(UserRequest userDto) throws Exception {
		// Validate first name
		if (!StringUtils.hasText(userDto.getFirstName())) {
			throw new IllegalArgumentException("First name is invalid");
		}

		// Validate last name
		if (!StringUtils.hasText(userDto.getLastName())) {
			throw new IllegalArgumentException("Last name is invalid");
		}

		// Validate email
		if (!StringUtils.hasText(userDto.getEmail()) || !userDto.getEmail().matches(Constants.EMAIL_REGEX)) {
			throw new IllegalArgumentException("Email ID is invalid");
		} else {
			Boolean existEmail = userRepo.existsByEmail(userDto.getEmail());
			if (existEmail) {
				throw new ExistDataException("Email already exist");
			}
		}
		// Validate mobile number
		if (!StringUtils.hasText(userDto.getMobNo()) || !userDto.getMobNo().matches(Constants.MOBNO_REGEX)) {
			throw new IllegalArgumentException("Mobile number is invalid");
		}

		// Validate roles
		if (ObjectUtils.isEmpty(userDto.getRoles())) {
			throw new IllegalArgumentException("Role is invalid");
		} else {
			// Get all valid role IDs from the database
			List<Integer> validRoleIds = roleRepo.findAll().stream().map(r -> r.getId()).toList();

			// Find invalid role IDs from the request
			List<Integer> invalidRoleIds = userDto.getRoles().stream().map(r -> r.getId())
					.filter(roleId -> !validRoleIds.contains(roleId)).toList();

			// If there are any invalid roles, throw an exception
			if (!invalidRoleIds.isEmpty()) {
				throw new IllegalArgumentException("Invalid roles: " + invalidRoleIds);
			}
		}
	}

}