package de.ait.platform.recipes.repository;

import de.ait.platform.recipes.entity.Recipe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class RecipeRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;
    @Test
    public void ArticleRepository_Save_ReturnArticle() {
        //Arrange
        Recipe recipe = Recipe
                .builder()
                .title("My Article")
                .content("My Content")
                .photo("")
                .build();
        //Act
        Recipe savedRecipe = articleRepository.save(recipe);
        //Assert
        Assertions.assertThat(savedRecipe).isNotNull();
        Assertions.assertThat(savedRecipe.getId()).isGreaterThan(0);
    }

    @Test
    public void ArticleRepository_FindAll_ReturnListArticle() {
        //Arrange
        Recipe recipe = Recipe
                .builder()
                .title("My Article")
                .content("My Content")
                .photo("")
                .build();

        Recipe recipe2 = Recipe
                .builder()
                .title("My Article2")
                .content("My Content2")
                .photo("")
                .build();
        //Act
        articleRepository.save(recipe);
        articleRepository.save(recipe2);
        List<Recipe> recipeList = articleRepository.findAll();

        //Assert
        Assertions.assertThat(recipeList).isNotNull();
        Assertions.assertThat(recipeList.size()).isEqualTo(2);
    }

    @Test
    public void ArticleRepository_FindById_ReturnArticle() {
        //Arrange
        Recipe recipe = Recipe
                .builder()
                .title("My Article")
                .content("My Content")
                .photo("")
                .build();

        Recipe recipe2 = Recipe
                .builder()
                .title("My Article2")
                .content("My Content2")
                .photo("")
                .build();
        //Act
        articleRepository.save(recipe);
        articleRepository.save(recipe2);
        Recipe recipeList = articleRepository.findById(recipe2.getId()).get();

        //Assert
        Assertions.assertThat(recipeList).isNotNull();

    }
    @Test
    public void ArticleRepository_FindByTitle_ReturnArticle() {
        //Arrange
        Recipe recipe = Recipe
                .builder()
                .title("My Article")
                .content("My Content")
                .photo("")
                .build();

        Recipe recipe2 = Recipe
                .builder()
                .title("My Article2")
                .content("My Content2")
                .photo("")
                .build();
        //Act
        articleRepository.save(recipe);
        articleRepository.save(recipe2);
        Recipe foundRecipe = articleRepository.findByTitle(recipe2.getTitle());

        //Assert
        Assertions.assertThat(foundRecipe).isNotNull();

    }
    @Test
    public void ArticleRepository_Update_ReturnArticle() {
        //Arrange
        Recipe recipe = Recipe
                .builder()
                .title("My Article")
                .content("My Content")
                .photo("")
                .build();

        Recipe recipe2 = Recipe
                .builder()
                .title("My Article2")
                .content("My Content2")
                .photo("")
                .build();
        //Act
        articleRepository.save(recipe);
        articleRepository.save(recipe2);
        Recipe foundRecipe = articleRepository.findById(recipe2.getId()).get();
        foundRecipe.setContent("New Content");
        Recipe saved = articleRepository.save(foundRecipe);
        //Assert
        Assertions.assertThat(saved.getContent()).isNotNull();
        Assertions.assertThat(saved.getTitle()).isNotNull();

    }

    @Test
    public void ArticleRepository_DeleteById_ReturnArticle() {
        //Arrange
        Recipe recipe2 = Recipe
                .builder()
                .title("My Article2")
                .content("My Content2")
                .photo("")
                .build();
        //Act
        articleRepository.save(recipe2);
        articleRepository.deleteById(recipe2.getId());
        Optional<Recipe> articleList = articleRepository.findById(recipe2.getId());

        //Assert
        Assertions.assertThat(articleList).isEmpty();

    }
}