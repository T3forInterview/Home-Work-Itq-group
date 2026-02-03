package ru.hw.PetrushinNickolay.batch.service.impl;

import org.springframework.stereotype.Service;
import ru.hw.PetrushinNickolay.batch.service.BatchService;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.service.DocumentService;

import java.util.List;

@Service
public class BatchServiceImpl implements BatchService {
    private DocumentService service;

    public BatchServiceImpl(DocumentService service) {
        this.service = service;
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
                if (status == Status.DRAFT) {
                    if (!batch.isEmpty()) {
                        List<Document> documents = batch.size() > batchSize
                                ? batch.subList(0, batchSize) : batch;
                        documents.stream().forEach(doc -> service.submitListDocument(doc.getId(), request));
                    }
                } else {
                    if (!batch.isEmpty()) {
                        List<Document> documents = batch.size() > batchSize
                                ? batch.subList(0, batchSize) : batch;
                        documents.stream().forEach(doc -> service.approveListDocument(doc.getId(), request));
                    }
                }
            } catch (Exception e) {
                //запись в лог
            }
        } while (batch.size() > batchSize);
    }
}
