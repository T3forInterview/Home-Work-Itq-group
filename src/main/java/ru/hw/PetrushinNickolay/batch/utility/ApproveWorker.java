package ru.hw.PetrushinNickolay.batch.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hw.PetrushinNickolay.batch.service.BatchService;
import ru.hw.PetrushinNickolay.model.enums.Status;


@Component
public class ApproveWorker {
    private BatchService service;
    private int batchSize;
    private static final Logger logger = LoggerFactory.getLogger(ApproveWorker.class);

    public ApproveWorker(BatchService service, @Value("${app.document.batch-size}") int batchSize) {
        this.service = service;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedRateString = "${submit.worker.delay}")
    public void processSubmitBatch() {
        long start = System.currentTimeMillis();
        service.processingBatch(Status.SUBMITTED, batchSize);
        long end = System.currentTimeMillis();
        logger.info("Время выполнения пакетной операции согласования {} миллисекунд", end - start);
    }

}
