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
import com.becoder.repository.CategoryRepository;
import com.becoder.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private ModelMapper mapper;

	@Override
	public Boolean saveCategory(CategoryDto categoryDto) {
		Category category = mapper.map(categoryDto, Category.class);

		if (ObjectUtils.isEmpty(category.getId())) {
			category.setName(categoryDto.getName());
			category.setDescription(categoryDto.getDescription());
			category.setIsActive(categoryDto.getIsActive());
			category.setCreatedBy(0);
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
			category.setIsDeleted(existCategory.getIsDeleted());

			category.setUpdatedOn(new Date());
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
	public CategoryDto getCategoryById(Integer id) {

		Optional<Category> findByCategory = categoryRepo.findByIdAndIsDeletedFalse(id);
		if (findByCategory.isPresent()) {
			Category category = findByCategory.get();
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