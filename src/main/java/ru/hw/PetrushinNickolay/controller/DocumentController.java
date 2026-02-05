package ru.hw.PetrushinNickolay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.request.RequestDocument;
import ru.hw.PetrushinNickolay.service.DocumentService;

@RestController
@RequestMapping("/document")
public class DocumentController {
    private DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody RequestDocument request) {
        Document document = service.createDocument(request);
        return ResponseEntity.ok(document);
    }


}
