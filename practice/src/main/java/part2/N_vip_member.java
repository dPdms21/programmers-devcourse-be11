package part2;

public class N_vip_member extends N_member {
    public N_vip_member(String name, String email, String phone) {
        super(name, email, phone);
    }

    @Override
    public String getGrade() {
        return "VIP";
    }

    @Override
    public String getBenefit() {
        return "10%할인 + 무료배송";
    }
}
