package part3;

public interface B_member {
    String getName();
    String getEmail();
    String getPhone();
    String getGrade();
    String getBenefit();
    void update(String name, String email, String phone);

    default void printInfo() {
        System.out.println("[" + getGrade() + "] " + getName() + " / "
                + getEmail() + " / " + getPhone() + " (혜택: " + getBenefit() + ")");
    }
}
