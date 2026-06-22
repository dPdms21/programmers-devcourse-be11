package membermanagement4;

public class GradeMember implements Member {
    private String name;
    private String email;
    private String phone;
    private final Grade grade;

    public GradeMember(String name, String email, String phone, Grade grade) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.grade = grade;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getGrade() {
        return grade.getName();
    }

    @Override
    public String getBenefit() {
        return grade.getBenefit();
    }

    @Override
    public void update(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}