package de.ait.platform.recipes.exception;

public class FieldIsTaken extends RuntimeException {
    public FieldIsTaken(String message) {
        super(message);
    }
}