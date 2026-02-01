package ru.hw.PetrushinNickolay.service;

import org.springframework.data.domain.Page;
import ru.hw.PetrushinNickolay.dto.DocumentHistoryDTO;
import ru.hw.PetrushinNickolay.dto.DocumentResponseDTO;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;

import java.util.List;


public interface DocumentService {
    Document createDocument(RequestDocument requestDocument);
    DocumentHistoryDTO getDocumentWithHistory(Long id);
    List<DocumentResponseDTO> getSubmitDocumentList(List<Long> list, ChangeRequest request);
    List<DocumentResponseDTO> getApproveDocumentList(List<Long> list, ChangeRequest request);

    DocumentResponseDTO submitDocument(Long id, ChangeRequest request);
    DocumentResponseDTO approveDocument(Long id, ChangeRequest request);
    Page<Document> getListDocumentsByListId(List<Long> ids, int page, int size, String sortBy, String sortDir);

}
