package de.ait.platform.categories.dto;

import de.ait.platform.recipes.entity.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO for category response")
public class CategoryResponse {

    @Schema(description = "Unique identifier of the category", example = "1", required = true)
    private Long id;

    @Schema(description = "Name of the category", example = "Technology", required = true)
    private String name;

    @Schema(description = "List of articles associated with the category")
    private List<Recipe> recipes;

    public CategoryResponse(String category) {
    }
}
