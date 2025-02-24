package de.ait.platform.categories.service;

import de.ait.platform.recipes.dto.ResponseArticle;
import de.ait.platform.recipes.entity.Recipe;
import de.ait.platform.categories.dto.CategoryRequest;
import de.ait.platform.categories.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<Recipe> addArticleToCategory(Long articleId, Long categoryId);
    List<CategoryResponse> findAll();
    CategoryResponse findById(Long id);
    List<CategoryResponse> findByName(String name);
    CategoryResponse delete(Long id);
    CategoryResponse save(CategoryRequest categoryDTO);
    CategoryResponse update(Long id, CategoryRequest categoryDTO);
    List<ResponseArticle> findArticleInCategories( String title);
    List<ResponseArticle> findArticleInCategory(String name, String title);
}
