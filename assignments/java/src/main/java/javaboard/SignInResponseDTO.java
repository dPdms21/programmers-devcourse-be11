package javaboard;

public class SignInResponseDTO {
    private boolean status;
    private String userId, name;

    public SignInResponseDTO(boolean status, String userId, String name) {
        this.status = status;
        this.userId = userId;
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
