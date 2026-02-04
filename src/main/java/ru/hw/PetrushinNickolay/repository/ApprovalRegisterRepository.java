package ru.hw.PetrushinNickolay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hw.PetrushinNickolay.model.entityes.ApprovalRegister;

import java.util.List;

@Repository
public interface ApprovalRegisterRepository extends JpaRepository<ApprovalRegister, Long> {
    @Query("SELECT ar FROM ApprovalRegister ar WHERE ar.document.id = :id")
    List<ApprovalRegister> findAllByDocumentId(Long id);
}
