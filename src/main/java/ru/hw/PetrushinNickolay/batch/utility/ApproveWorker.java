package ru.hw.PetrushinNickolay.batch.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hw.PetrushinNickolay.batch.service.BatchService;
import ru.hw.PetrushinNickolay.model.enums.Status;


@Component
public class ApproveWorker {
    private BatchService service;
    private int batchSize;

    public ApproveWorker(BatchService service, @Value("${app.document.batch-size}") int batchSize) {
        this.service = service;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedRateString = "${submit.worker.delay}")
    public void processSubmitBatch() {
        service.processingBatch(Status.SUBMITTED, batchSize);
    }

}
