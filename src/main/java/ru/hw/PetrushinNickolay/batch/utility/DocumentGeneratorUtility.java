package ru.hw.PetrushinNickolay.batch.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.hw.PetrushinNickolay.exception.InvalidOperationException;
import ru.hw.PetrushinNickolay.model.entityes.Document;
import ru.hw.PetrushinNickolay.model.enums.Status;
import ru.hw.PetrushinNickolay.repository.DocumentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.IntStream;

@Component
public class DocumentGeneratorUtility {
    private DocumentRepository repository;

    public DocumentGeneratorUtility(DocumentRepository repository) {
        this.repository = repository;
    }

    private static final String FILE_JSON = "params.json";

    public int generationDocument() {
        int n = readFromFile();
        IntStream.range(0, n).forEach(i -> {
            String author = "Generator" + i;
            String name = "Evgeniy" + i;
            String initiator = "api";
            Document document = new Document();
            document.setUniqNumber();
            document.setAuthor(author);
            document.setName(name);
            document.setStatus(Status.DRAFT);
            document.setInitiator(initiator);
            document.setCreatedDate(LocalDate.now());

            try {
                repository.save(document);
            } catch (Exception e) {
                throw new InvalidOperationException("Успешно создано " + i + " документов. Возникла ошибка "
                        + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
        return n;
    }

    private int readFromFile() {
        try {
            String json = Files.readString(Paths.get(FILE_JSON));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            if (root.has("N")) {
                int n = root.get("N").asInt();
                if (n > 0) {
                    return n;
                } else {
                    throw new InvalidOperationException("число N должно быть больше нуля", HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new InvalidOperationException("В файле param.json нет поля N", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            throw new InvalidOperationException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
