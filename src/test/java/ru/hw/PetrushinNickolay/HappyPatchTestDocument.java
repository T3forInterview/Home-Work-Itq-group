package ru.hw.PetrushinNickolay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.hw.PetrushinNickolay.model.entityes.ApprovalRegister;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.entityes.History;
import ru.hw.PetrushinNickolay.model.enums.Action;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.repository.ApprovalRegisterRepository;
import ru.hw.PetrushinNickolay.repository.DocumentRepository;
import ru.hw.PetrushinNickolay.repository.HistoryRepository;
import ru.hw.PetrushinNickolay.service.DocumentService;

import java.util.List;

@SpringBootTest
@Transactional
public class HappyPatchTestDocument {
    @Autowired
    private DocumentService service;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ApprovalRegisterRepository  approvalRegisterRepository;

    @BeforeEach
    void setOn() {
        documentRepository.deleteAll();
        historyRepository.deleteAll();
        approvalRegisterRepository.deleteAll();
    }


    @Test
    void testCreateDocument() {
        RequestDocument request = new RequestDocument();
        request.setAuthor("Тестовый автор");
        request.setName("Создание документа для теста");
        request.setInitiator("Test");

        Document document = service.createDocument(request);

        Assertions.assertNotNull(document);
        Assertions.assertNotNull(document.getId());
        Assertions.assertNotNull(document.getUniqNumber());
        Assertions.assertEquals("Тестовый автор", document.getAuthor());
        Assertions.assertEquals("Создание документа для теста", document.getName());
        Assertions.assertEquals(Status.DRAFT, document.getStatus());
        Assertions.assertEquals("Test", document.getInitiator());
        Assertions.assertNotNull(document.getCreatedDate());

        List<Document> documents = service.findAll();
        Assertions.assertEquals(1, documents.size());

    }

    @Test
    void testSubmitDocument() {
        RequestDocument requestDocument = new RequestDocument();
        requestDocument.setAuthor("Тестовый автор");
        requestDocument.setName("Создание документа для теста");
        requestDocument.setInitiator("Test");

        ChangeRequest changeRequest = new ChangeRequest();
        changeRequest.setInitiator("Тест согласования");
        changeRequest.setComment("Создание документа в статусе На согласовании");

        Document document = service.createDocument(requestDocument);
        service.submitDocument(document.getId(), changeRequest);

        List<History> historyList = historyRepository.findAllByDocumentId(document.getId());
        Assertions.assertEquals(1, historyList);
        Assertions.assertEquals(Action.SUBMIT, historyList.get(0).getAction());
        Assertions.assertEquals("Создание документа в статусе На согласовании", historyList.get(0).getComment());

    }

    @Test
    void testApproveDocument() {
        RequestDocument requestDocument = new RequestDocument();
        requestDocument.setAuthor("Тестовый автор");
        requestDocument.setName("Создание документа для теста");
        requestDocument.setInitiator("Test");

        ChangeRequest changeRequest = new ChangeRequest();
        changeRequest.setInitiator("Тест согласования");
        changeRequest.setComment("Создание документа в статусе Утвержден");

        Document document = service.createDocument(requestDocument);
        service.submitDocument(document.getId(), changeRequest);
        service.approveDocument(document.getId(), changeRequest);

        List<ApprovalRegister> approvalList = approvalRegisterRepository.findAllByDocumentId(document.getId());
        Assertions.assertEquals(1, approvalList);
        Assertions.assertEquals(Status.APPROVED, approvalList.get(0).getDocument().getStatus());

        List<History> historyList = historyRepository.findAllByDocumentId(document.getId());
        Assertions.assertEquals(1, historyList);
        Assertions.assertEquals(Action.APPROVE, historyList.get(0).getAction());
        Assertions.assertEquals("Создание документа в статусе Утвержден", historyList.get(0).getComment());

    }

}
