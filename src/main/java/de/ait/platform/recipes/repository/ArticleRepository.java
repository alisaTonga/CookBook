package de.ait.platform.recipes.repository;

import de.ait.platform.recipes.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Recipe, Long> {
Recipe findByTitle(String title);

}
