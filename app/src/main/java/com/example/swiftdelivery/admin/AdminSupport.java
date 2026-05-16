package com.example.swiftdelivery.admin;

public class AdminSupport {

    private String id;
    private String UID;
    private String name;
    private String email;
    private String subject;
    private String question;

    AdminSupport() {}

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public AdminSupport(String id, String UID, String name, String email, String subject, String question) {
        this.id = id;
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.question = question;
    }
}
