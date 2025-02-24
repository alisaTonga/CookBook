package de.ait.platform.recipes.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.ait.platform.categories.entity.Category;
import de.ait.platform.comments.entity.Comment;
import de.ait.platform.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
@Table(name="recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "photo")
    private String photo;

    @ManyToMany
    @JoinTable(
            name = "categories_articles",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
//    @JsonManagedReference
    private Set<Category> categories;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    //@JsonIgnore
    private Set<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @JsonProperty("user")
    public String getUserUsername() {
        return user.getUsername();
    }

    public void addCategory(Category category) {
        categories.add(category);
    }
    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
