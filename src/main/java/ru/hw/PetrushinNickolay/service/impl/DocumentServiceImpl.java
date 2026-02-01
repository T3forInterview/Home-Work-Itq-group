package ru.hw.PetrushinNickolay.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hw.PetrushinNickolay.dto.DocumentHistoryDTO;
import ru.hw.PetrushinNickolay.dto.DocumentResponseDTO;
import ru.hw.PetrushinNickolay.exception.InvalidOperationException;
import ru.hw.PetrushinNickolay.exception.ResourceNotFoundException;
import ru.hw.PetrushinNickolay.model.entityes.ApprovalRegister;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.entityes.History;
import ru.hw.PetrushinNickolay.model.enums.Action;
import ru.hw.PetrushinNickolay.model.enums.ResponseStatus;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.repository.ApprovalRegisterRepository;
import ru.hw.PetrushinNickolay.repository.DocumentRepository;
import ru.hw.PetrushinNickolay.repository.HistoryRepository;
import ru.hw.PetrushinNickolay.service.DocumentService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private DocumentRepository documentRepository;
    private HistoryRepository historyRepository;
    private ApprovalRegisterRepository approvalRegisterRepository;

    public DocumentServiceImpl() {
    }

    public DocumentServiceImpl(DocumentRepository documentRepository, HistoryRepository historyRepository,
                               ApprovalRegisterRepository approvalRegisterRepository) {
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
        this.approvalRegisterRepository = approvalRegisterRepository;
    }

    @Override
    @Transactional
    public Document createDocument(RequestDocument requestDocument) {
        Document document = new Document();
        document.setUniqNumber();
        document.setAuthor(requestDocument.getAuthor());
        document.setName(requestDocument.getName());
        document.setStatus(Status.DRAFT);
        document.setInitiator(requestDocument.getInitiator());
        document.setCreatedDate(LocalDate.now());
        document.setUpdateDate(null);
        documentRepository.save(document);
        return document;
    }

    private Document getDocumentById(Long id) {
        return documentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Документ с данным id=" + id + " не найден"));
    }

    private List<History> getAllHistoryByDocumentId(Long id) {
        return historyRepository.findAllByDocumentId(id);
    }

    @Override
    public DocumentHistoryDTO getDocumentWithHistory(Long id) {
        Document document = getDocumentById(id);
        DocumentHistoryDTO documentDTO = new DocumentHistoryDTO();
        documentDTO.setId(document.getId());
        documentDTO.setUniqNumber(document.getUniqNumber());
        documentDTO.setAuthor(document.getAuthor());
        documentDTO.setStatus(document.getStatus());
        documentDTO.setStatus(document.getStatus());
        documentDTO.setInitiator(document.getInitiator());
        documentDTO.setCreatedDate(document.getCreatedDate());
        documentDTO.setUpdateDate(document.getUpdateDate());
        documentDTO.setHistories(getAllHistoryByDocumentId(id));
        return documentDTO;
    }

    @Override
    public List<DocumentResponseDTO> getSubmitDocumentList(List<Long> list, ChangeRequest request) {
        checkSizeList(list);
        return list.stream()
                .map(id -> submitDocument(id, request))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDTO> getApproveDocumentList(List<Long> list, ChangeRequest request) {
        checkSizeList(list);
        return list.stream()
                .map(id -> approveDocument(id, request))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentResponseDTO submitDocument(Long id, ChangeRequest request) {
        DocumentResponseDTO responseDTO = new DocumentResponseDTO();
        Document document = findDocumentById(id);
        if (!checkDocumentExists(document,responseDTO, id)) {
            return responseDTO;
        }
        updateDocument(document, request);
        if (document.getStatus() != Status.DRAFT) {
            responseDTO.setDocumentId(document.getId());
            responseDTO.setResponseStatus(ResponseStatus.кофликт.name());
            responseDTO.setMessage("Не найден документ в статусе черновик с данным id=" + document.getId());
            return responseDTO;
        }
        document.setStatus(Status.SUBMITTED);
        Document updated = documentRepository.save(document);

        History history = new History(updated, request.getInitiator(), LocalDate.now(), Action.SUBMIT, request.getComment());
        historyRepository.save(history);

        responseDTO.setDocumentId(document.getId());
        responseDTO.setResponseStatus(ResponseStatus.успешно.name());
        responseDTO.setMessage("Статус документ успешно переведен с Черновика на отправлен на Согласование");
        return responseDTO;
    }

    @Override
    @Transactional
    public DocumentResponseDTO approveDocument(Long id, ChangeRequest request) {
        DocumentResponseDTO responseDTO = new DocumentResponseDTO();
        Document document = findDocumentById(id);
        if (!checkDocumentExists(document,responseDTO, id)) {
            return responseDTO;
        }
        updateDocument(document, request);

        if (document.getStatus() != Status.SUBMITTED) {
            responseDTO.setDocumentId(document.getId());
            responseDTO.setResponseStatus(ResponseStatus.кофликт.name());
            responseDTO.setMessage("Не найден документ в статусе на согласовании с данным id=" + document.getId());
            return responseDTO;
        }
        document.setStatus(Status.APPROVED);
        Document updated = documentRepository.save(document);

        try {
            ApprovalRegister approvalRegister = new ApprovalRegister(updated, LocalDate.now());
            approvalRegisterRepository.save(approvalRegister);

            History history = new History(updated, request.getInitiator(), LocalDate.now(),
                    Action.APPROVE, request.getComment());
            historyRepository.save(history);
            responseDTO.setDocumentId(document.getId());
            responseDTO.setResponseStatus(ResponseStatus.успешно.name());
            responseDTO.setMessage("Статус документ успешно переведен с на отправлен на Согласование на Утвержден");
            return responseDTO;
        } catch (Exception e) {
            //запись в лог
            updated.setStatus(Status.SUBMITTED);
            updated.setUpdateDate(LocalDate.now());
            documentRepository.save(updated);
            responseDTO.setDocumentId(document.getId());
            responseDTO.setResponseStatus(ResponseStatus.ошибка_регистрации_в_реестре.name());
            responseDTO.setMessage("Утверждение документа было отменено");
            return responseDTO;
        }

    }

    @Override
    public Page<Document> getListDocumentsByListId(List<Long> ids, int page, int size, String sortBy,
                                                   String sortDir) {
        Sort sort = Sort.unsorted();
        if (sortBy != null) {
            sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return documentRepository.findAllByIdIn(ids, pageable);
    }

    private Document findDocumentById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    private boolean checkDocumentExists(Document document, DocumentResponseDTO responseDTO, Long id) {
        if (document == null) {
            responseDTO.setDocumentId(id);
            responseDTO.setResponseStatus(ResponseStatus.не_найдено.name());
            responseDTO.setMessage("Не найден документ с данным id=" + id);
            return false;
        }
        return true;
    }

    private void updateDocument(Document document, ChangeRequest request) {
        document.setInitiator(request.getInitiator());
        document.setUpdateDate(LocalDate.now());
    }

    private void checkSizeList(List<Long> listId) {
        if (listId.size() > 1000) {
            throw new InvalidOperationException("Список уникальных номеров документов не может быть больше 1000",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
