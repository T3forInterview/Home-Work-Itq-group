package ru.hw.PetrushinNickolay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hw.PetrushinNickolay.batch.service.BatchService;
import ru.hw.PetrushinNickolay.dto.ConcurrentApproveDTO;
import ru.hw.PetrushinNickolay.dto.DocumentHistoryDTO;
import ru.hw.PetrushinNickolay.dto.DocumentResponseDTO;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.model.request.ChangeRequest;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.service.DocumentService;
import ru.hw.PetrushinNickolay.service.TestConcurrentApprove;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Document API", description = "API для работы с документами")
@Validated
@RequestMapping("/document")
public class DocumentController {
    private DocumentService service;
    private TestConcurrentApprove concurrentService;
    private BatchService batchService;
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    public DocumentController(DocumentService service, TestConcurrentApprove concurrentService,
                              BatchService batchService) {
        this.service = service;
        this.concurrentService = concurrentService;
        this.batchService = batchService;
    }

    @Operation(summary = "Создать документ",
            description = "Создаёт документ в статусе DRAFT. Номер генерируется автоматически")
    @PostMapping
    public ResponseEntity<Document> createDocument(@Valid @RequestBody RequestDocument request) {
        logger.info("Создан документ: author={}, name={}, initiator={}",
                request.getAuthor(), request.getName(), request.getInitiator());
        Document document = service.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @Operation(summary = "Получить документ с историей",
            description = "Возвращает документ по ID вместе с историей изменений")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentHistoryDTO> getDocumentWithHistory(@PathVariable @NotNull Long id) {
        logger.info("Получен документ с историей, id={}", id);
        DocumentHistoryDTO response = service.getDocumentWithHistory(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить список документов",
            description = "Возвращает страницу документов с возможностью сортировки")
    @GetMapping
    public ResponseEntity<Page<Document>> getAllDocuments(@RequestParam List<Long> ids,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        logger.info("Получить список документов, номер страницы={}, размер страницы={}, " +
                "направление сортировки={}", page, size, sortDir);
        Page<Document> documents = service.getListDocumentsByListId(ids, page, size, sortBy, sortDir);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Отправить документы на согласование",
            description = "Переводит документы из статуса DRAFT в SUBMITTED. Максимум 1000 документов")
    @PostMapping("/submit")
    public ResponseEntity<List<DocumentResponseDTO>> submitDocuments(List<Long> ids,
            @Valid @RequestBody ChangeRequest request) {
        logger.info("Отправлены документы на согласование, количество документов={}, инициатор={} " +
                        "комментарий={}", ids.size(), request.getInitiator(), request.getComment());
        List<DocumentResponseDTO> response = service.getSubmitDocumentList(ids, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Отправить документы на утверждение",
            description = "Переводит документы из статуса SUBMITTED в APPROVED. Максимум 1000 документов")
    @PostMapping("/approve")
    public ResponseEntity<List<DocumentResponseDTO>> approveDocuments(List<Long> ids,
            @Valid @RequestBody ChangeRequest request) {
        logger.info("Утверждены документы, количество документов={}, инициатор={}, комментарий={} ",
                ids.size(), request.getInitiator(), request.getInitiator());
        List<DocumentResponseDTO> response = service.getApproveDocumentList(ids, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Поиск документов",
            description = "Поиск документов по фильтрам: статус, автор, период дат создания")
    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchDocuments(
            @RequestParam(required = false) Status status, @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        logger.info("Найдены документы: со статусом={}, автором={}, Дата создания Начало={}, " +
                        "Дата создания Окончание={}", status, author, fromDate, toDate);
        List<Document> documents = service.findByFilters(status, author, fromDate, toDate);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Тест конкурентного утверждения",
            description = "Запускает параллельные попытки утвердить один документ")
    @PostMapping("/{documentId}/concurrent")
    public ResponseEntity<ConcurrentApproveDTO> testConcurrentApproval(@PathVariable @NotNull Long documentId,
            @RequestParam @Min(1) @Max(100) int threads, @RequestParam @Min(1) @Max(1000) int attempts) {
        logger.info("Попытка утвердить документ: номер документа={}, количество потоков={}, " +
                        "количество попыток={}", documentId, threads, attempts);
        ConcurrentApproveDTO approveDTO = concurrentService.testApprove(documentId, threads, attempts);
        return ResponseEntity.ok(approveDTO);
    }

    @Operation(summary = "Создать документы с помощью утилиты генерации",
            description = "читает параметр N из файла params-batch.json и создает N файлов")
    @PostMapping("/generate")
    public ResponseEntity<String> generateDocuments() {
        int count = batchService.generationsDocuments();
        String response = "Успешно создано " + count + " документов";
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Запустить SUBMIT worker",
            description = "Запустить фоновый процесс отправки документов на согласование")
    @PostMapping("/generate/submit")
    public ResponseEntity<Void> submitWorker(@Value("${app.document.batch-size}") int batchSize) {
        batchService.processingBatch(Status.SUBMITTED, batchSize);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Запустить APPROVE worker",
            description = "Запустить фоновый процесс отправки документов на утверждение")
    @PostMapping("/generate/approve")
    public ResponseEntity<Void> approveWorker(@Value("${app.document.batch-size}") int batchSize) {
        batchService.processingBatch(Status.APPROVED, batchSize);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
