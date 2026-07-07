package javaboard;

public class ContentDTO {
    private int id;
    private String userId;
    private String content;
    private String created;

    public ContentDTO(int id, String userId, String content, String created) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getCreated() {
        return created;
    }
}