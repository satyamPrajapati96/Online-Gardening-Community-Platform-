package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String author;
    private String caption;
    private String imageFilename; // relative path under uploads/
    private Date createdAt;
    private List<Comment> comments = new ArrayList<>();

    public Post() {}

    public Post(long id, String author, String caption, String imageFilename) {
        this.id = id;
        this.author = author;
        this.caption = caption;
        this.imageFilename = imageFilename;
        this.createdAt = new Date();
    }

    public long getId() { return id; }
    public String getAuthor() { return author; }
    public String getCaption() { return caption; }
    public String getImageFilename() { return imageFilename; }
    public Date getCreatedAt() { return createdAt; }
    public List<Comment> getComments() { return comments; }

    public void addComment(Comment c) { comments.add(c); }
}
