package ru.hw.PetrushinNickolay.model.entityes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import ru.hw.PetrushinNickolay.model.enums.Action;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @JsonIgnore
    private Document document;
    @Column(name = "initiator", nullable = false)
    @NotNull
    private String initiator;
    @Column(name = "transfer_date", nullable = false)
    @NotNull
    private LocalDate transferDate;
    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;
    @Column(name = "comment", nullable = true)
    private String comment;

    public History() {
    }

    public History(Document document, String initiator, LocalDate transferDate, Action action, String comment) {
        this.document = document;
        this.initiator = initiator;
        this.transferDate = transferDate;
        this.action = action;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        History history = (History) o;
        return Objects.equals(id, history.id) && Objects.equals(document, history.document)
                && Objects.equals(initiator, history.initiator)
                && Objects.equals(transferDate, history.transferDate)
                && action == history.action && Objects.equals(comment, history.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, document, initiator, transferDate, action, comment);
    }
}
