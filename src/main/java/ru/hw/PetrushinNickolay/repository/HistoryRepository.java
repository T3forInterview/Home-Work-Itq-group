package ru.hw.PetrushinNickolay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hw.PetrushinNickolay.model.entityes.History;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    Optional<History> findById(Long id);
    @Query("SELECT h FROM History h WHERE h.document.id = :id")
    List<History> findAllByDocumentId(Long id);
}
