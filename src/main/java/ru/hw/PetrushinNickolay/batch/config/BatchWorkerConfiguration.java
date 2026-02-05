package ru.hw.PetrushinNickolay.batch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.hw.PetrushinNickolay.batch.service.BatchService;
import ru.hw.PetrushinNickolay.batch.service.ConditionalOnHasDocuments;
import ru.hw.PetrushinNickolay.batch.utility.ApproveWorker;
import ru.hw.PetrushinNickolay.batch.utility.SubmitWorker;
import ru.hw.PetrushinNickolay.model.enums.Status;

@Configuration
@EnableScheduling
public class BatchWorkerConfiguration {
    @Value("${app.document.batch-size}")
    private int batchSize;

    @Bean
    @ConditionalOnHasDocuments(status = Status.DRAFT)
    public SubmitWorker submitWorker(BatchService service) {
        return new SubmitWorker(service, batchSize);
    }

    @Bean
    @ConditionalOnHasDocuments(status = Status.SUBMITTED)
    public ApproveWorker approveWorker(BatchService service) {
        return new ApproveWorker(service,  batchSize);
    }
}
