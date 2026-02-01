package ru.hw.PetrushinNickolay.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findById(Long id);
    @Query("SELECT d FROM Document d WHERE (:status IS NULL OR d.status = :status) AND " +
            "(:author IS NULL OR LOWER(d.author) LIKE CONCAT('%', LOWER(:author), '%')) AND " +
            "(:from IS NULL OR d.createdDate >= :from) AND (:to IS NULL OR d.createdDate <= :to)")
    List<Document> findByFilters(@Param("status") Status status, @Param("author") String author,
                                 @Param("from") LocalDate from, @Param("to") LocalDate to);
    @Query("SELECT d FROM Document d WHERE d.id IN :ids")
    Page<Document> findAllByIdIn(@Param("ids") List<Long> ids, Pageable pageable);
}
