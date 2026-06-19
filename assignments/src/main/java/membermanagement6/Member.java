package membermanagement6;

public interface Member {
    String getName();
    String getEmail();
    String getPhone();
    String getGrade();
    String getBenefit();

    default void printInfo() {
        System.out.println("[" + getGrade() + "] " + getName() + " / " + getEmail() + " / " + getPhone() + " (혜택: " + getBenefit() + ")");
    }
}
