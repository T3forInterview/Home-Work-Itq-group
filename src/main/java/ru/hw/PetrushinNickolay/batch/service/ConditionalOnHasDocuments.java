package ru.hw.PetrushinNickolay.batch.service;

import org.springframework.context.annotation.Conditional;
import ru.hw.PetrushinNickolay.batch.service.impl.HasDocumentCondition;
import ru.hw.PetrushinNickolay.model.enums.Status;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(HasDocumentCondition.class)
public @interface ConditionalOnHasDocuments {
    Status status();
}
