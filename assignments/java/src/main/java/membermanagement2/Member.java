package membermanagement2;

public abstract class Member {
    protected String name;
    protected String email;
    protected String phone;

    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public abstract String getGrade();
    public abstract String getBenefit();

    public void printInfo() {
        System.out.println("[" + getGrade() + "] " + name + " / " + email + " / " + phone + " (혜택: " + getBenefit() + ")");
    }

    public void update(String name,  String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
