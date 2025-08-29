package org.example.eventsbooker.services;

import org.example.eventsbooker.entites.Category;
import org.example.eventsbooker.entites.dtos.CategoryDTO;
import org.example.eventsbooker.repositories.category.CategoryRepository;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;


    public Category addCategory(CategoryDTO categoryDTO){
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        Category existingCategory = findCategoryByName(categoryDTO.getName());

        if (existingCategory != null) {
            throw new WebApplicationException("Category already exists", Response.Status.CONFLICT);
        }
        return categoryRepository.addCategory(category);
    }
    public Category findCategoryByName(String name){
        return categoryRepository.findCategoryByName(name);
    }

    public Category findCategoryById(Long id){
        return categoryRepository.findCategoryById(id);
    }

    public boolean deleteCategory(Category category){
        return categoryRepository.deleteCategory(category);
    }
    public Category updateCategory(Long id, CategoryDTO categoryDTO){
        return categoryRepository.updateCategory(id, categoryDTO);
    }

    public List<Category> getAllCategories(Integer page, Integer limit){
        return categoryRepository.getAllCategories(page, limit);
    }



}
