package br.com.brasa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida CPF (11 digitos) ou CNPJ (14 digitos) pelos digitos verificadores.
 * Aceita o valor com ou sem mascara. Valor nulo e considerado valido (use @NotBlank junto).
 */
@Documented
@Constraint(validatedBy = CpfCnpjValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpj {

    String message() default "CPF/CNPJ invalido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
