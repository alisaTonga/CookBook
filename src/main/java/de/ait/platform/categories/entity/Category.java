package de.ait.platform.categories.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.ait.platform.recipes.entity.Recipe;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable( name="categories_articles",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="article_id")
    )
    @JsonBackReference
    private List<Recipe> recipes = new ArrayList<>();

    public List<Recipe> addArticle(Recipe recipe) {
        recipes.add(recipe);
        return recipes;
    }
}
