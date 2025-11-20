package com.vivek.bookms.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for ISBN validation
 * Supports both ISBN-10 and ISBN-13 formats
 */
@Documented
@Constraint(validatedBy = ISBNValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidISBN {
    String message() default "Invalid ISBN format. Must be a valid ISBN-10 or ISBN-13";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow null values to be considered valid
     */
    boolean allowNull() default true;
    
    /**
     * Specify which ISBN formats to accept
     */
    ISBNFormat[] formats() default {ISBNFormat.ISBN_10, ISBNFormat.ISBN_13};
    
    enum ISBNFormat {
        ISBN_10("^(?:ISBN(?:-10)?:?\\s*)?(?=[0-9X]{10}$|(?=(?:[0-9]+[-\\s])*[0-9X]$)[0-9]{1,5}[-\\s]?[0-9]+[-\\s]?[0-9]+[-\\s]?[0-9X]$).*$"),
        ISBN_13("^(?:ISBN(?:-13)?:?\\s*)?(?=97[89][0-9]{10}$|(?=(?:[0-9]+[-\\s])*[0-9]$)(?:[0-9]+[-\\s]?){3}[0-9]$).*$");
        
        private final String pattern;
        
        ISBNFormat(String pattern) {
            this.pattern = pattern;
        }
        
        public String getPattern() {
            return pattern;
        }
    }
}