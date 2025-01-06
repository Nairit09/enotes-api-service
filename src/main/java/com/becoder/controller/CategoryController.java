package com.becoder.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.becoder.dto.CategoryDto;
import com.becoder.dto.CategoryResponse;
import com.becoder.entity.Category;
import com.becoder.service.CategoryService;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping("/save-category")
	public ResponseEntity<?> saveCategory(@RequestBody CategoryDto category) {
		category.setCreatedBy(1);
		category.setCreatedOn(new Date());
		category.setIsDeleted(false);
		Boolean saveCategory = categoryService.saveCategory(category);
		if (saveCategory) {
			return new ResponseEntity<>("saved success", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("not saved", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/category")
	public ResponseEntity<?> getAllCategory() {
		List<CategoryDto> allCategory = categoryService.getAllCategory();
		if (CollectionUtils.isEmpty(allCategory)) {
			return ResponseEntity.noContent().build();
		} else {

			return new ResponseEntity<>(allCategory, HttpStatus.OK);
		}
	}

	@GetMapping("/active-category")
	public ResponseEntity<?> getActiveCategory() {
		List<CategoryResponse> allCategory = categoryService.getActiveCategory();
		if (CollectionUtils.isEmpty(allCategory)) {
			return ResponseEntity.noContent().build();
		} else {

			return new ResponseEntity<>(allCategory, HttpStatus.OK);
		}

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getCategoryDetailsById(@PathVariable Integer id) {
		CategoryDto categoryDto = categoryService.getCategoryById(id);
		if (ObjectUtils.isEmpty(categoryDto)) {
			return new ResponseEntity<>("Category not found with Id=" + id, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(categoryDto, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategoryById(@PathVariable Integer id) {
		Boolean deleted = categoryService.deleteCategoryById(id);
		if (deleted) {
			return new ResponseEntity<>("Category deleted success", HttpStatus.OK);
		}
		return new ResponseEntity<>(deleted, HttpStatus.OK);
	}
}
