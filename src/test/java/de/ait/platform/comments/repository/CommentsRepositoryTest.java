package de.ait.platform.comments.repository;
import de.ait.platform.recipes.entity.Recipe;
import de.ait.platform.recipes.repository.ArticleRepository;
import de.ait.platform.comments.entity.Comment;
import de.ait.platform.users.entity.User;
import de.ait.platform.users.reposittory.UserRepository;
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
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommentsRepositoryTest {
    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

@Test
public void CommentsRepository_Save_ReturnSavedComment() {
    Recipe recipe = Recipe
            .builder()
            .title("My Article")
            .content("My Content")
            .photo("")
            .build();
    User user = User.builder()
            .username("exampleUser")
            .email("exampleUser@gmail.com")
            .password("qwerty007")
            .build();
    Comment comment = Comment.builder()
            .recipe(recipe).user(user)
            .text("Test comment")
            .build();
    Comment savedComment = commentsRepository.save(comment);
    System.out.println(savedComment.getText());
    Assertions.assertThat(savedComment).isNotNull();
    Assertions.assertThat(savedComment.getId()).isGreaterThan(0);
}

    @Test
    public void CommentsRepository_FindById_ReturnComment() {
        Recipe recipe = Recipe.builder()
                .title("Sample Article")
                .content("Article content")
                .photo("photo_url")
                .build();
        User user = User.builder()
                .username("test_user")
                .email("test@example.com")
                .password("password")
                .build();
        Comment comment = Comment.builder()
                .text("Sample Comment")
                .user(user)
                .recipe(recipe)
                .build();
        Comment savedComment = commentsRepository.save(comment);
        Optional<Comment> returnedComment = commentsRepository.findById(savedComment.getId());
        Assertions.assertThat(returnedComment).isPresent();
        Assertions.assertThat(returnedComment.get().getText()).isEqualTo("Sample Comment");
    }
    @Test
    public void CommentsRepository_UpdateComment_ReturnUpdatedComment() {
        Recipe recipe = Recipe.builder()
                .title("Sample Article")
                .content("Article content")
                .photo("photo_url")
                .build();

        User user = User.builder()
                .username("test_user")
                .email("test@example.com")
                .password("password")
                .build();

        Comment comment = Comment.builder()
                .text("Original Comment")
                .user(user)
                .recipe(recipe)
                .build();

        Comment savedComment = commentsRepository.save(comment);
        Comment commentToUpdate = commentsRepository.findById(savedComment.getId()).get();
        commentToUpdate.setText("Updated Comment");
        Comment updatedComment = commentsRepository.save(commentToUpdate);
        Assertions.assertThat(updatedComment.getText()).isEqualTo("Updated Comment");
    }

    @Test
    public void CommentsRepository_DeleteComment_ReturnCommentIsEmpty() {
        Recipe recipe = Recipe.builder()
                .title("Sample Article")
                .content("Article content")
                .photo("photo_url")
                .build();

        User user = User.builder()
                .username("test_user")
                .email("test@example.com")
                .password("password")
                .build();

        Comment comment = Comment.builder()
                .text("Sample Comment")
                .user(user)
                .recipe(recipe)
                .build();

        Comment savedComment = commentsRepository.save(comment);
        commentsRepository.deleteById(savedComment.getId());
        Optional<Comment> deletedComment = commentsRepository.findById(savedComment.getId());
        Assertions.assertThat(deletedComment).isEmpty();
    }

    @Test
    public void CommentsRepository_FindAll_ReturnListOfComments() {
        Recipe recipe = Recipe.builder()
                .title("Sample Article")
                .content("Article content")
                .photo("photo_url")
                .build();

        User user = User.builder()
                .username("test_user")
                .email("test@example.com")
                .password("password")
                .build();

        Comment comment1 = Comment.builder()
                .text("First Comment")
                .user(user)
                .recipe(recipe)
                .build();
        Recipe savedRecipe = articleRepository.save(recipe);
        User savedUser = userRepository.save(user);
        Comment comment2 = Comment.builder()
                .text("Second Comment")
                .user(user)
                .recipe(recipe)
                .build();

        commentsRepository.save(comment1);
        commentsRepository.save(comment2);
        List<Comment> commentsList = commentsRepository.findAll();
        Assertions.assertThat(commentsList).isNotNull();
        Assertions.assertThat(commentsList.size()).isEqualTo(2);
    }
}