package nbu.dbProject.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class ValidationUtil {

    private static final Logger logger = LogManager.getLogger(ValidationUtil.class);
    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = validatorFactory.getValidator();

    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed:\n");
            for (ConstraintViolation<T> violation : violations) {
                sb.append(" - ").append(violation.getPropertyPath())
                        .append(": ").append(violation.getMessage()).append("\n");
                logger.warn("Validation error - {}: {}",
                        violation.getPropertyPath(), violation.getMessage());
            }
            throw new IllegalArgumentException(sb.toString());
        }
        logger.debug("Validation successful for {}", object.getClass().getSimpleName());
    }

    public static <T> boolean isValid(T object) {
        try {
            validate(object);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}