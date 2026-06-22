package part3;

public class B_vip_member implements B_member {
    private String name, email, phone;

    public B_vip_member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
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
        return "VIP";
    }

    @Override
    public String getBenefit() {
        return "10%할인 + 무료배송";
    }

    @Override
    public void update(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
