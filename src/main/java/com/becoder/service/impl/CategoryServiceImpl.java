package com.becoder.service.impl;

import java.util.Date;
import java.util.List;

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
		category.setName(categoryDto.getName());
		category.setDescription(categoryDto.getDescription());
		category.setIsActive(categoryDto.getIsActive());
		category.setCreatedBy(0);
		category.setCreatedOn(new Date());

		category.setIsDeleted(false);
		category.setUpdatedBy(null);
		category.setUpdatedOn(null);

		Category saveCategory = categoryRepo.save(category);
		if (ObjectUtils.isEmpty(saveCategory)) {
			return false;
		}
		return true;
	}

	@Override
	public List<CategoryDto> getAllCategory() {
		List<Category> categories = categoryRepo.findAll();
		List<CategoryDto> list = categories.stream().map(cat -> mapper.map(cat, CategoryDto.class)).toList();
		return list;

	}

	@Override
	public List<CategoryResponse> getActiveCategory() {
		List<Category> categories = categoryRepo.findByIsActiveTrue();
		List<CategoryResponse> list = categories.stream().map(cat -> mapper.map(cat, CategoryResponse.class)).toList();
		return list;

	}

}
