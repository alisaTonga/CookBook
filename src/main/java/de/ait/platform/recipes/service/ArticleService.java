package de.ait.platform.recipes.service;

import de.ait.platform.recipes.dto.ResponseArticle;
import de.ait.platform.recipes.dto.RequestArticle;

import java.util.List;

public interface ArticleService {
    List<ResponseArticle> findAll();
    ResponseArticle findById(Long id);
    List<ResponseArticle> findByTitle(String title);
    ResponseArticle createArticle(RequestArticle article);
    ResponseArticle updateArticle(Long id, RequestArticle article);
    ResponseArticle deleteArticle(Long id);

}
