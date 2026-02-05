package ru.hw.PetrushinNickolay.batch.service.impl;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import ru.hw.PetrushinNickolay.batch.service.ConditionalOnHasDocuments;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.repository.DocumentRepository;

import java.util.Map;

public class HasDocumentCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> map = metadata.getAnnotationAttributes(ConditionalOnHasDocuments.class.getName());

        if (map == null) {
            return false;
        }

        Status status = (Status) (map.get("status"));

        try {
            Environment environment = context.getEnvironment();
            String dataSourceUrl = environment.getProperty("spring.datasource.url");
            BeanFactory beanFactory = context.getBeanFactory();
            if (beanFactory != null) {
                DocumentRepository repository = beanFactory.getBean(DocumentRepository.class);
                return repository.countByStatus(status) > 0;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}

