package ru.hw.PetrushinNickolay.service.impl;

import org.springframework.stereotype.Service;
import ru.hw.PetrushinNickolay.dto.DocumentHistoryDTO;
import ru.hw.PetrushinNickolay.exception.ResourceNotFoundException;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.repository.DocumentRepository;
import ru.hw.PetrushinNickolay.service.DocumentService;

import java.time.LocalDate;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Document createDocument(RequestDocument requestDocument) {
        Document document = new Document();
        document.setUniqNumber();
        document.setAuthor(requestDocument.getAuthor());
        document.setName(requestDocument.getName());
        document.setStatus(Status.DRAFT);
        document.setInitiator(requestDocument.getInitiator());
        document.setCreatedDate(LocalDate.now());
        document.setUpdateDate(null);
        return document;
    }

    private Document getDocumentById(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Документ с данным id=" + id + " не найден"));
    }

    @Override
    public DocumentHistoryDTO getDocumentWithHistory(Long id) {
        return null;
    }

    @Override
    public List<Document> getDocumentList(List<Long> list) {
        return null;
    }
}
