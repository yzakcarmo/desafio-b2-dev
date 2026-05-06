package com.yzakcarmo.desafiob2dev.strategy;

import java.util.List;

public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    private ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.errors = errors;
        this.warnings = warnings;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, List.of(), List.of());
    }

    public static ValidationResult okWithWarnings(List<String> warnings) {
        return new ValidationResult(true, List.of(), warnings);
    }

    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors, List.of());
    }

    public static ValidationResult failure(String error) {
        return new ValidationResult(false, List.of(error), List.of());
    }

    public boolean isValid() { return valid; }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
}