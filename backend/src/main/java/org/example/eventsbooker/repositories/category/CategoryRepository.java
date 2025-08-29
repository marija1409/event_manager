package org.example.eventsbooker.repositories.category;

import org.example.eventsbooker.entites.Category;
import org.example.eventsbooker.entites.dtos.CategoryDTO;

import java.util.List;

public interface CategoryRepository {
    Category addCategory(Category category);
    Category findCategoryByName(String name);
    Category findCategoryById(Long id);
    boolean deleteCategory(Category category);
    Category updateCategory(Long id, CategoryDTO categoryDTO);

    List<Category> getAllCategories(Integer page, Integer limit);

}
