package ru.hw.PetrushinNickolay.model.request;


public class RequestDocument {
    private String author;
    private String name;
    private String initiator;

    public RequestDocument() {
    }

    public RequestDocument(String author, String name, String initiator) {
        this.author = author;
        this.name = name;
        this.initiator = initiator;
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

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }
}
