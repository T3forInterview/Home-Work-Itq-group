package ru.hw.PetrushinNickolay.service;

import ru.hw.PetrushinNickolay.dto.DocumentHistoryDTO;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;

import java.util.List;


public interface DocumentService {
    Document createDocument(RequestDocument requestDocument);
    DocumentHistoryDTO getDocumentWithHistory(Long id);
    List<Document> getDocumentList(List<Long> list);
}
