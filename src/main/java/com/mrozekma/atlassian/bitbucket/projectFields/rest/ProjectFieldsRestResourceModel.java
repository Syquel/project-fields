package com.mrozekma.atlassian.bitbucket.projectFields.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectFieldsRestResourceModel {

    @XmlElement(name = "value")
    private String message;

    @XmlElement(name = "n")
    private int n;

    public ProjectFieldsRestResourceModel() {
    }

    public ProjectFieldsRestResourceModel(String message, int n) {
        this.message = message;
        this.n = n;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}