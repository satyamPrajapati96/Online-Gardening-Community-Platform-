package store;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import model.Comment;
import model.Post;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String STORE_FILE = "data/store.ser";

    private List<Post> posts = new ArrayList<>();

    // transient because AtomicLong is not needed to be serialized
    private transient AtomicLong idCounter;

    private static transient DataStore instance;

    private DataStore() {
        if (posts == null) posts = new ArrayList<>();
        idCounter = new AtomicLong(nextIdFromPosts());
    }

    private long nextIdFromPosts() {
        long max = 0;
        if (posts != null) {
            for (Post p : posts) {
                if (p != null && p.getId() > max) max = p.getId();
            }
        }
        return max + 1;
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            File f = new File(STORE_FILE);
            if (f.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                    Object obj = ois.readObject();
                    if (obj instanceof DataStore) {
                        instance = (DataStore) obj;
                    } else {
                        instance = new DataStore();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    instance = new DataStore();
                }
            } else {
                instance = new DataStore();
            }
            if (instance.idCounter == null) instance.idCounter = new AtomicLong(instance.nextIdFromPosts());
        }
        return instance;
    }

    public synchronized void save() {
        try {
            new File("data").mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE))) {
                oos.writeObject(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Post addPost(String author, String caption, String imageFilename) {
        long id = idCounter.getAndIncrement();
        Post p = new Post(id, author, caption, imageFilename);
        posts.add(0, p); // newest first
        save();
        return p;
    }

    public synchronized void addComment(long postId, Comment c) {
        for (Post p : posts) {
            if (p.getId() == postId) {
                p.addComment(c);
                save();
                return;
            }
        }
    }

    public synchronized List<Post> getPosts() {
        return posts;
    }

    // ensure idCounter restored on deserialization
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.idCounter = new AtomicLong(nextIdFromPosts());
    }
}


