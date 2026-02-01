package ru.hw.PetrushinNickolay.dto;

import ru.hw.PetrushinNickolay.model.entityes.History;
import ru.hw.PetrushinNickolay.model.enums.Status;

import java.time.LocalDate;
import java.util.List;

public class DocumentHistoryDTO {
    private Long id;
    private String uniqNumber;
    private String author;
    private String name;
    private Status status;
    private String initiator;
    private LocalDate createdDate;
    private LocalDate updateDate;
    private List<History> histories;

    public DocumentHistoryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqNumber() {
        return uniqNumber;
    }

    public void setUniqNumber(String uniqNumber) {
        this.uniqNumber = uniqNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }
}
