package ru.hw.PetrushinNickolay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hw.PetrushinNickolay.model.entityes.ApprovalRegister;

@Repository
public interface ApprovalRegisterRepository extends JpaRepository<ApprovalRegister, Long> {
}
