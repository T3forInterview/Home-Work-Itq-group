package ru.hw.PetrushinNickolay.model.request;

public class ChangeRequest {
    private String initiator;
    private String comment;

    public ChangeRequest() {
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
