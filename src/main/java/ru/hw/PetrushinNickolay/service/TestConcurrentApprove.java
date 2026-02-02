package ru.hw.PetrushinNickolay.service;

import ru.hw.PetrushinNickolay.dto.ConcurrentApproveDTO;

public interface TestConcurrentApprove {
    ConcurrentApproveDTO testApprove(Long id, int threads, int attempts);
}
