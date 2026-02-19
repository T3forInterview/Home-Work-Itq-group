package ru.hw.PetrushinNickolay.batch.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hw.PetrushinNickolay.batch.service.BatchService;
import ru.hw.PetrushinNickolay.batch.utility.DocumentGeneratorUtility;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.service.DocumentService;

import java.util.List;

@Service
public class BatchServiceImpl implements BatchService {
    private DocumentService service;
    private DocumentGeneratorUtility documentGeneratorUtility;
    private static final Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);

    public BatchServiceImpl(DocumentService service, DocumentGeneratorUtility documentGeneratorUtility) {
        this.service = service;
        this.documentGeneratorUtility = documentGeneratorUtility;
    }

    @Override
    public int generationsDocuments() {
        return documentGeneratorUtility.generationDocument();
    }

    @Override
    public void processingBatch(Status status, int batchSize) {
        ChangeRequest request = new ChangeRequest();
        request.setInitiator("Фоновая обработка");
        request.setComment(null);
        List<Document> batch;
        do {
            batch = service.findByFilters(status, null, null, null);
            try {
                if (batch.isEmpty()) {
                    return;
                }
                List<Document> documents = batch.size() > batchSize
                        ? batch.subList(0, batchSize) : batch;
                if (status == Status.DRAFT) {
                    logger.info("Поступило документов {} на согласование", documents.size());
                    documents.stream().forEach(doc -> service.submitListDocument(doc.getId(), request));
                } else {
                    logger.info("Поступило документов {} на утверждение", documents.size());
                    documents.stream().forEach(doc -> service.approveListDocument(doc.getId(), request));
                }
            } catch (Exception e) {
                logger.error("Произошла ошибка при фоновой обработке");
            }
        } while (batch.size() > batchSize);
    }
}
