package ru.practicum.shareit.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateAfterNowValidator implements ConstraintValidator<DateAfterNow, LocalDateTime> {
    private LocalDateTime currentTime;

    @Override
    public void initialize(DateAfterNow constraintAnnotation) {
        currentTime = LocalDateTime.now();
    }

    @Override
    public boolean isValid(LocalDateTime valueTime, ConstraintValidatorContext context) {
        return !valueTime.isBefore(currentTime);
    }
}
