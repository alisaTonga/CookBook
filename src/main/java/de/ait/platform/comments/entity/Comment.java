package de.ait.platform.comments.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.ait.platform.recipes.entity.Recipe;
import de.ait.platform.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @JsonProperty("user")
    public String getUserUsername() {
        return user.getUsername();
    }

    @ManyToOne
    @JoinColumn(name = "article_id")
    @JsonBackReference
    private Recipe recipe;

    public Comment(Long id, String text, User user, Recipe recipe) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.recipe = recipe;
    }
}