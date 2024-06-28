package com.georgebanin.validator;

import com.georgebanin.exceptions.ObjectNotValidException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.Produces;


import java.util.Set;
import java.util.stream.Collectors;

@Dependent
public class ObjectsValidator<T> {

    @Inject
    Validator validator;

    public void validate(T objectToValidate) throws ObjectNotValidException {
        // Validate the given object using the configured Validator.
        Set<ConstraintViolation<T>> violationSet = validator.validate(objectToValidate);

        // If there are constraint violations, collect error messages and throw an exception.
        if (!violationSet.isEmpty()) {
            String errorMessages = violationSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("/ "));
            throw new ObjectNotValidException(errorMessages);
        }
    }
}
