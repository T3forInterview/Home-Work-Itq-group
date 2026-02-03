package ru.hw.PetrushinNickolay.batch.service;

import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;


public interface BatchService {
    void processingBatch(Status status, int batchSize);
}
