package ru.hw.PetrushinNickolay.service;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import ru.hw.PetrushinNickolay.dto.DocumentHistoryDTO;
import ru.hw.PetrushinNickolay.dto.DocumentResponseDTO;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;

import java.time.LocalDate;
import java.util.List;


public interface DocumentService {
    Document createDocument(RequestDocument requestDocument);
    List<Document> findAll();
    DocumentHistoryDTO getDocumentWithHistory(Long id);
    List<DocumentResponseDTO> getSubmitDocumentList(List<Long> list, ChangeRequest request);
    List<DocumentResponseDTO> getApproveDocumentList(List<Long> list, ChangeRequest request);
    List<Document> findByFilters(Status status, String author, LocalDate from, LocalDate to);
    DocumentResponseDTO submitListDocument(Long id, ChangeRequest request);
    DocumentResponseDTO approveListDocument(Long id, ChangeRequest request);
    Page<Document> getListDocumentsByListId(List<Long> ids, int page, int size, String sortBy, String sortDir);
    Document approveDocument(Long id, ChangeRequest request);
    Document submitDocument(Long id, ChangeRequest request);

}
