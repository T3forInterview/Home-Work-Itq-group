package ru.hw.PetrushinNickolay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hw.PetrushinNickolay.dto.ConcurrentApproveDTO;
import ru.hw.PetrushinNickolay.exception.InvalidOperationException;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.service.DocumentService;
import ru.hw.PetrushinNickolay.service.TestConcurrentApprove;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TestConcurrentApproveImpl implements TestConcurrentApprove {
    private DocumentService service;
    private static final Logger logger = LoggerFactory.getLogger(TestConcurrentApproveImpl.class);

    public TestConcurrentApproveImpl(DocumentService service) {
        this.service = service;
    }

    @Override
    public ConcurrentApproveDTO testApprove(Long id, int threads, int attempts) {
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger conflict = new AtomicInteger(0);
        AtomicInteger error = new AtomicInteger(0);
        AtomicReference<String> status = null;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<CompletableFuture<Void>> futures = IntStream.range(0, attempts)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    ChangeRequest request = new ChangeRequest();
                    request.setInitiator("testUser" + i);
                    request.setComment("testRequest" + i);

                    try {
                        Document document = service.approveDocument(id, request);
                        success.incrementAndGet();
                        status.set(document.getStatus().name());
                        logger.info("Успешно утверждено " + success.get() + " документов");
                    } catch (InvalidOperationException e) {
                        switch (e.getStatus()) {
                            case CONFLICT:
                                conflict.incrementAndGet();
                                logger.info("Утверждение прошло с статусом конфлик у " + conflict.get() + " документов");
                                break;
                            default:
                                error.incrementAndGet();
                                logger.info("Утверждение прошло с ошибками у " + error.get() + " документов");
                        }
                    } catch (Exception e) {
                        error.incrementAndGet();
                        logger.info("Возникли ошибки");
                    }
                }, executor))
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        return new ConcurrentApproveDTO(attempts, success.get(), conflict.get(), error.get(), status.get());
    }
}



