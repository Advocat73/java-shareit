package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import javax.validation.constraints.Past;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateAfterNowValidator.class)
@Past
public @interface DateAfterNow {
    String message() default "Дата начала бронирования вещи не может быть в прошлом";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}