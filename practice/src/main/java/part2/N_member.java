package part2;

public abstract class N_member {
    protected String name;
    protected String email;
    protected String phone;

    // 생성자
    public N_member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getter
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    // 메서드
    public abstract String getGrade();
    public abstract String getBenefit();

    public void printInfo() {
        System.out.println("[" + getGrade() + "] " + name + " / " + email
                + " / " + phone + " (혜택: " + getBenefit() + ")");
    }

    // 정보 수정
    public void update(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
