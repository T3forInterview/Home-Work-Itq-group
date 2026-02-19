package ru.hw.PetrushinNickolay.batch.service;

import org.springframework.stereotype.Service;
import ru.hw.PetrushinNickolay.model.enums.Status;


public interface BatchService {
    int generationsDocuments();
    void processingBatch(Status status, int batchSize);
}
