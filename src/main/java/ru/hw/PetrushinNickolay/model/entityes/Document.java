package ru.hw.PetrushinNickolay.model.entityes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.hw.PetrushinNickolay.model.enums.Status;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uniq_number", unique = true, nullable = false)
    private String uniqNumber;
    @Column(name = "author", nullable = false)
    private String author;
    @Column(name = "name", nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @Column(name = "initiator", nullable = false)
    private String initiator;
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDate createdDate;
    @Column(name = "update_date")
    private LocalDate updateDate;

    public Document() {
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

    public void setUniqNumber() {
        this.uniqNumber = UUID.randomUUID().toString();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id) && Objects.equals(uniqNumber, document.uniqNumber) && Objects.equals(author, document.author) && Objects.equals(name, document.name) && Objects.equals(createdDate, document.createdDate) && Objects.equals(updateDate, document.updateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uniqNumber, author, name, createdDate, updateDate);
    }
}
