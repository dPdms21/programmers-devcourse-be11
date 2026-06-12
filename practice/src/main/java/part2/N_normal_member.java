package part2;

public class N_normal_member extends N_member {
    public N_normal_member(String name, String email, String phone) {
        super(name, email, phone);
    }

    @Override
    public String getGrade() {
        return "일반";
    }

    @Override
    public String getBenefit() {
        return "기본 서비스";
    }
}
