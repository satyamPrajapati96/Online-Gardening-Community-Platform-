package model;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String author;
    private String text;
    private Date createdAt;

    public Comment() {}

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
        this.createdAt = new Date();
    }

    public String getAuthor() { return author; }
    public String getText() { return text; }
    public Date getCreatedAt() { return createdAt; }
}


