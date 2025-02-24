package de.ait.platform.recipes.exception;

public class ArticleNotFound extends RuntimeException {
    public ArticleNotFound(String message) {
        super(message);
    }
}
