package de.ait.platform.recipes.service;

import de.ait.platform.recipes.dto.ResponseArticle;
import de.ait.platform.recipes.dto.RequestArticle;
import de.ait.platform.recipes.entity.Recipe;
import de.ait.platform.recipes.exception.*;
import de.ait.platform.recipes.repository.ArticleRepository;
import de.ait.platform.categories.dto.CategoryResponse;
import de.ait.platform.categories.entity.Category;
import de.ait.platform.categories.exceptions.CategoryNotFound;
import de.ait.platform.categories.service.CategoryServiceImp;
import de.ait.platform.comments.entity.Comment;
import de.ait.platform.security.service.AuthService;
import de.ait.platform.users.dto.UserResponseDto;
import de.ait.platform.users.entity.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;


@AllArgsConstructor
@Service
public class ArticleServiceImp implements ArticleService {
    private final ArticleRepository repository;
    private final CategoryServiceImp categoryService;
    private final ModelMapper mapper;
    private final AuthService service;


    @Transactional
    @Override
    public List<ResponseArticle> findAll() {
        List<Recipe> list = repository.findAll();
        return list.stream()
                .map(article -> mapper.map(article, ResponseArticle.class))
                .toList();
    }

    @Transactional
    @Override
    public ResponseArticle findById(Long id) {
        Optional<Recipe> article = repository.findById(id);
        if (article.isPresent()) {
            return mapper.map(article.get(), ResponseArticle.class);
        }
        else {
            throw new ArticleNotFound("Article with id: " + id + " not found");
        }
    }

    @Transactional
    @Override
    public List<ResponseArticle> findByTitle(String title) {

        if (title == null || title.isEmpty()) {
            throw new FieldCannotBeNull("Article with title: " + title + " not found");
        }
        if (title.isBlank()){
            throw new FieldIsBlank("Title cannot be blank");
        }
        Predicate<Recipe> predicateByTitle =
                (title.equals("")) ? a-> true:  article -> article.getTitle().equalsIgnoreCase(title);
        List<Recipe> recipeList = repository.findAll().stream().filter(predicateByTitle).toList();
        System.out.println(recipeList);
        if (recipeList.isEmpty()) {
            throw new ArticleNotFound("Article with title: " + title + " not found");
        }
        return recipeList.stream().map(article -> mapper.map(article, ResponseArticle.class)).toList();
    }


    @Override
    public ResponseArticle createArticle(RequestArticle dto) {

        boolean isEmpty = repository.findAll()
                .stream()
                .filter((dto.getTitle().equals("")) ? a -> true : article -> article.getTitle().equalsIgnoreCase(dto.getTitle()))
                .toList()
                .isEmpty();

        if (!isEmpty){
            throw new FieldIsTaken("Title: " + dto.getTitle() + " is already taken");
        }
        if (dto.getContent() == null) {
            throw new FieldCannotBeNull("Content cannot be null");
        }
        if (dto.getContent().isBlank()) {
            throw new FieldIsBlank("Content cannot empty");
        }
        Recipe entity = mapper.map(dto, Recipe.class);
        UserResponseDto userDto = service.getAuthenticatedUser();
        Set<Category> categories = new HashSet<>();
        if (dto.getCategories() != null) {
            for (Long number: dto.getCategories()){
                try {
                    CategoryResponse categoryResponse = categoryService.findById(number);
                    Category category = mapper.map(categoryResponse, Category.class);
                    categories.add(category);
                }
                catch (CategoryNotFound e){
                    throw new CategoryNotFound("Category with id: " + number + " not found");
                }
            }
        }
        User user = mapper.map(userDto, User.class);
        entity.setCategories(categories);
        entity.setUser(user);
        repository.save(entity);
        return new ResponseArticle(entity.getId(),entity.getTitle(),entity.getContent(),
                entity.getPhoto(),entity.getUser().getUsername(),entity.getComments(), entity.getCategories());
    }

    @Transactional
    @Override
    public ResponseArticle updateArticle(Long id, RequestArticle dto) {
//        if (fingByTitle(dto.getTitle()).if) {
//            throw new FieldIsTaken("That title already exist");
//        }
        Recipe existingRecipe = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        if (dto.getComments() != null && !dto.getComments().isEmpty()) {
            existingRecipe.setComments(dto.getComments());
        }

        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            existingRecipe.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            existingRecipe.setContent(dto.getContent());
        }

        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            for (Long number : dto.getCategories()) {
                try {
                    CategoryResponse categoryResponse = categoryService.findById(number);
                    Category category = mapper.map(categoryResponse, Category.class);
                    existingRecipe.addCategory(category);
                }
                catch (CategoryNotFound e) {
                    throw new CategoryNotFound("Category with id: " + number + " not found");
                }
            }}
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            existingRecipe.setPhoto(dto.getPhoto());
        }

        Recipe updatedRecipe = repository.save(existingRecipe);

        return mapper.map(updatedRecipe, ResponseArticle.class);
    }


    @Transactional
    @Override
    public ResponseArticle deleteArticle(Long id) {
        Optional<Recipe> foundedArticle = repository.findById(id);
        if (foundedArticle.isPresent()){
            if (foundedArticle.get().getComments() != null && !foundedArticle.get().getComments().isEmpty()){
                for (Comment comment : foundedArticle.get().getComments()) {
                    comment.setRecipe(null);
                    comment.setUser(null);
                }
                foundedArticle.get().setComments(new HashSet<>());
                try {
                    repository.deleteById(id);
                } catch (Exception e) {
                    throw new RuntimeException("Error deleting article with id: " + id, e);
                }
            }
        }
        else {
            throw new ArticleNotFound("Article with id: " + id + "not found");
        }

        return mapper.map(foundedArticle, ResponseArticle.class);
    }
}