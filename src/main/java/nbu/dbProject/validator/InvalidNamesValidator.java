package nbu.dbProject.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.TreeSet;

public class InvalidNamesValidator implements ConstraintValidator<InvalidNames, String> {
    private final Set<String> reservedWord = new TreeSet<>();
    @Override
    public void initialize(InvalidNames constraintAnnotation) {
        reservedWord.addAll(Set.of("Firm", "Company"));
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return reservedWord.stream().noneMatch(value::contains);
    }
}