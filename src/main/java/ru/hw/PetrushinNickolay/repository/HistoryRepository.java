package ru.hw.PetrushinNickolay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hw.PetrushinNickolay.model.entityes.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
}
