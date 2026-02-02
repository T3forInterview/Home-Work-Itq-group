package ru.hw.PetrushinNickolay.service.impl;

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

                    } catch (InvalidOperationException e) {
                        switch (e.getStatus()) {
                            case CONFLICT:
                                conflict.incrementAndGet();
                                break;
                            default:
                                error.incrementAndGet();
                        }
                    } catch (Exception e) {
                        error.incrementAndGet();
                    }
                }, executor))
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        return new ConcurrentApproveDTO(attempts, success.get(), conflict.get(), error.get(), status.get());
    }
}



