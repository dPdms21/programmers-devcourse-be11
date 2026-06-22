package membermanagement3;

public interface Member {
    String getName();
    String getEmail();
    String getPhone();
    String getGrade();
    String getBenefit();

    public void update(String name,  String email, String phone);

    default void printInfo() {
        System.out.println("[" + getGrade() + "] " + getName() + " / " + getEmail() + " / " + getPhone() + " (혜택: " + getBenefit() + ")");
    }
}
