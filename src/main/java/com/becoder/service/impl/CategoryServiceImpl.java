package com.becoder.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.becoder.dto.CategoryDto;
import com.becoder.dto.CategoryResponse;
import com.becoder.entity.Category;
import com.becoder.exception.ExistDataException;
import com.becoder.exception.ResourceNotFoundException;
import com.becoder.repository.CategoryRepository;
import com.becoder.service.CategoryService;
import com.becoder.util.Validation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private Validation validation;

	@Override
	public Boolean saveCategory(CategoryDto categoryDto) {

		// Validation Checking
		validation.categoryValidation(categoryDto);

		Boolean exist = categoryRepo.existsByName(categoryDto.getName().trim());
		if (exist) {
			throw new ExistDataException("Category already exists");
		}

		Category category = mapper.map(categoryDto, Category.class);

		if (ObjectUtils.isEmpty(category.getId())) {

			category.setCreatedOn(new Date());

		} else {
			updateCategory(category);
		}
		Category saveCategory = categoryRepo.save(category);
		if (ObjectUtils.isEmpty(saveCategory)) {
			return false;
		}
		return true;
	}

	private void updateCategory(Category category) {
		Optional<Category> findById = categoryRepo.findById(category.getId());
		if (findById.isPresent()) {
			Category existCategory = findById.get();
			category.setCreatedBy(existCategory.getCreatedBy());
			category.setCreatedOn(existCategory.getCreatedOn());

			category.setUpdatedBy(1);
		}
	}

	@Override
	public List<CategoryDto> getAllCategory() {
		List<Category> categories = categoryRepo.findByIsDeletedFalse();
		List<CategoryDto> list = categories.stream().map(cat -> mapper.map(cat, CategoryDto.class)).toList();
		return list;

	}

	@Override
	public List<CategoryResponse> getActiveCategory() {
		List<Category> categories = categoryRepo.findByIsActiveTrueAndIsDeletedFalse();
		List<CategoryResponse> list = categories.stream().map(cat -> mapper.map(cat, CategoryResponse.class)).toList();
		return list;

	}

	@Override
	public CategoryDto getCategoryById(Integer id) throws Exception {

		Category category = categoryRepo.findByIdAndIsDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id=" + id));
		if (!ObjectUtils.isEmpty(category)) {
			return mapper.map(category, CategoryDto.class);

		}
		return null;

	}

	@Override
	public Boolean deleteCategoryById(Integer id) {
		Optional<Category> findById = categoryRepo.findById(id);

		if (findById.isPresent()) {
			Category category = findById.get();
			category.setIsDeleted(true);
			categoryRepo.save(category);
			return true;
		}
		return false;
	}
}