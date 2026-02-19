package ru.hw.PetrushinNickolay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.hw.PetrushinNickolay.batch.service.impl.BatchServiceImpl;
import ru.hw.PetrushinNickolay.batch.utility.DocumentGeneratorUtility;
import ru.hw.PetrushinNickolay.dto.DocumentResponseDTO;
import ru.hw.PetrushinNickolay.exception.InvalidOperationException;
import ru.hw.PetrushinNickolay.model.entityes.ApprovalRegister;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.entityes.History;
import ru.hw.PetrushinNickolay.model.enums.ResponseStatus;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.repository.ApprovalRegisterRepository;
import ru.hw.PetrushinNickolay.repository.DocumentRepository;
import ru.hw.PetrushinNickolay.repository.HistoryRepository;
import ru.hw.PetrushinNickolay.service.DocumentService;
import ru.hw.PetrushinNickolay.service.impl.DocumentServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class HappyPatchTestBatch {
    @Mock
    private DocumentService documentService;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ApprovalRegisterRepository  approvalRegisterRepository;
    @Mock
    private HistoryRepository historyRepository;
    @InjectMocks
    private DocumentGeneratorUtility generatorUtility;
    @InjectMocks
    private BatchServiceImpl batchService;
    @InjectMocks
    private DocumentServiceImpl documentServiceImpl;
    private DocumentResponseDTO documentResponseDTO;

    private Document draft;
    private Document draft1;
    private Document submit;
    private Document submit1;
    private ChangeRequest changeRequest;
    private int batchSize;



    @BeforeEach
    void setUp() {
        draft = createDocument(1L, "Test draft1", "Document1", Status.DRAFT);
        draft1 = createDocument(2L, "Test draft2", "Document2", Status.DRAFT);
        submit = createDocument(3L, "Test submit1", "Document3", Status.SUBMITTED);
        submit1 = createDocument(4L, "Test submit2", "Document4", Status.SUBMITTED);
        changeRequest = new ChangeRequest();
        changeRequest.setInitiator("Фоновая обработка");
        batchSize = 20;
        batchService = new BatchServiceImpl(documentService, generatorUtility);
    }

    @Test
    void testGenerationDocument() {
        DocumentGeneratorUtility utility = Mockito.spy(generatorUtility);
        Mockito.doReturn(batchSize).when(utility).generationDocument();

        BatchServiceImpl serviceSpy = new BatchServiceImpl(documentService, utility);

        int result = serviceSpy.generationsDocuments();

        Assertions.assertEquals(batchSize, result);
        Mockito.verify(utility, Mockito.times(1)).generationDocument();
    }

    @Test
    void testBatchSubmit() {
        Status status = Status.DRAFT;
        changeRequest.setComment("Тест отправки на согласование");
        List<Document> draftDocuments = Arrays.asList(draft, draft1);

        Mockito.when(documentService.findByFilters(Mockito.eq(status), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(draftDocuments).thenReturn(List.of());

        DocumentResponseDTO successDTO = new DocumentResponseDTO();
        successDTO.setResponseStatus(ResponseStatus.успешно.name());
        successDTO.setDocumentId(1L);

        DocumentResponseDTO successDTO2 = new DocumentResponseDTO();
        successDTO2.setResponseStatus(ResponseStatus.успешно.name());
        successDTO2.setDocumentId(2L);


        Mockito.when(documentService.submitListDocument(Mockito.eq(1L), Mockito.any(ChangeRequest.class)))
                .thenReturn(successDTO);
        Mockito.when(documentService.submitListDocument(Mockito.eq(2L), Mockito.any(ChangeRequest.class)))
                .thenReturn(successDTO2);

        batchService.processingBatch(status, batchSize);

        Mockito.verify(documentService, Mockito.times(1))
                .submitListDocument(Mockito.eq(1L), Mockito.any(ChangeRequest.class));
        Mockito.verify(documentService, Mockito.times(1))
                .submitListDocument(Mockito.eq(2L), Mockito.any(ChangeRequest.class));
    }

    @Test
    void testApproveBatch() {
        Status status = Status.SUBMITTED;
        changeRequest.setComment("Тест отправки на утверждение");
        DocumentResponseDTO responseSuccess = new DocumentResponseDTO();
        responseSuccess.setDocumentId(submit.getId());
        responseSuccess.setResponseStatus(ResponseStatus.успешно.name());
        List<Document> submitDocument = Arrays.asList(submit, submit1);


        Mockito.when(documentRepository.findById(submit.getId())).thenReturn(Optional.of(submit));
        Mockito.when(documentRepository.save(Mockito.any(Document.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(approvalRegisterRepository.save(Mockito.any(ApprovalRegister.class)))
                .thenThrow(new RuntimeException("Какая-то ошибка"));

        documentResponseDTO = documentServiceImpl.approveListDocument(submit.getId(), changeRequest);

        Assertions.assertNotNull(documentResponseDTO);
        Assertions.assertEquals(ResponseStatus.ошибка_регистрации_в_реестре.name(),
                documentResponseDTO.getResponseStatus());
        Assertions.assertEquals(submit.getId(), documentResponseDTO.getDocumentId());
        Assertions.assertEquals("Утверждение документа было отменено",
                documentResponseDTO.getMessage());

        Assertions.assertEquals(Status.SUBMITTED, submit.getStatus());
        Mockito.verify(documentRepository, Mockito.times(2)).save(Mockito.any(Document.class));

        Mockito.verify(historyRepository, Mockito.never()).save(Mockito.any(History.class));

        Mockito.verify(approvalRegisterRepository, Mockito.times(1))
                .save(Mockito.any(ApprovalRegister.class));
    }

    @Test
    void testApproveRollback() {
        Long id = 10L;
        draft.setStatus(Status.SUBMITTED);
        changeRequest.setComment("Откат утверждения по при ошибки записи в регистр");
        Document approveDraft = draft;
        Document approveSubmit = submit;
        Document approveSubmit1 = submit1;
        approveDraft.setStatus(Status.APPROVED);
        approveSubmit.setStatus(Status.APPROVED);
        approveSubmit1.setStatus(Status.APPROVED);

        Mockito.when(documentService.approveDocument(id, changeRequest))
                .thenThrow(new InvalidOperationException("Документ с данным id=" + id + " не найден",
                        HttpStatus.NOT_FOUND));
        Mockito.when(documentService.approveDocument(draft.getId(), changeRequest)).thenReturn(approveDraft);
        Mockito.when(documentService.approveDocument(draft1.getId(), changeRequest))
                .thenThrow(new InvalidOperationException("Статус документа должен быть На согласовании",
                        HttpStatus.CONFLICT));


        Document result = documentService.approveDocument(draft.getId(), changeRequest);
        Assertions.assertEquals(approveDraft, result);

        Assertions.assertThrows(InvalidOperationException.class, () -> {
            documentService.approveDocument(id, changeRequest);});

        Assertions.assertThrows(InvalidOperationException.class, ()-> {
            documentService.approveDocument(draft1.getId(), changeRequest);});

    }



    private Document createDocument(Long id, String author,  String name, Status status) {
        Document document = new Document();
        document.setId(id);
        document.setUniqNumber();
        document.setAuthor(author);
        document.setName(name);
        document.setStatus(status);
        document.setInitiator("Test creator");
        document.setCreatedDate(LocalDate.now());
        return document;
    }



}
