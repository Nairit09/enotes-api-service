package com.becoder.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import com.becoder.dto.CategoryDto;
import com.becoder.dto.CategoryResponse;
import com.becoder.endpoint.CategoryEndpoint;
import com.becoder.exception.ResourceNotFoundException;
import com.becoder.service.CategoryService;
import com.becoder.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CategoryController implements CategoryEndpoint {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

	@Override
	public ResponseEntity<?> saveCategory(CategoryDto category) {
		category.setCreatedBy(1);
		category.setCreatedOn(new Date());
		category.setIsDeleted(false);
		boolean saveCategory = categoryService.saveCategory(category);
		if (saveCategory) {
			return CommonUtil.createBuildResponseMessage("saved success", HttpStatus.CREATED);
		} else {
			return CommonUtil.createErrorResponseMessage("Category Not saved", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<?> getAllCategory() {
		List<CategoryDto> allCategory = categoryService.getAllCategory();
		if (CollectionUtils.isEmpty(allCategory)) {
			return ResponseEntity.noContent().build();
		} else {

			return new ResponseEntity<>(allCategory, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> getActiveCategory() {
		List<CategoryResponse> allCategory = categoryService.getActiveCategory();
		if (CollectionUtils.isEmpty(allCategory)) {
			return ResponseEntity.noContent().build();
		} else {

			return CommonUtil.createBuildResponse(allCategory, HttpStatus.OK);
		}

	}

	@Override
	public ResponseEntity<?> getCategoryDetailsById(Integer id) throws Exception {
		try {
			CategoryDto categoryDto = categoryService.getCategoryById(id);
			if (ObjectUtils.isEmpty(categoryDto)) {
				return new ResponseEntity<>("Category not found with Id=" + id, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(categoryDto, HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			log.error("Controller :: getCategoryDeatilsById ::", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> deleteCategoryById(Integer id) {
		boolean deleted = categoryService.deleteCategoryById(id);
		if (deleted) {
			return new ResponseEntity<>("Category deleted success", HttpStatus.OK);
		}
		return new ResponseEntity<>(deleted, HttpStatus.OK);
	}
}
