package membermanagement4;

public enum Grade {
    NORMAL("일반", "기본 서비스"),
    VIP("VIP", "10% 할인 + 무료배송");

    private final String name;
    private final String benefit;

    Grade(String name, String benefit) {
        this.name = name;
        this.benefit = benefit;
    }

    public String getName() {
        return name;
    }

    public String getBenefit() {
        return benefit;
    }

    public static Grade from(int choice) {
        switch (choice) {
            case 1:
                return NORMAL;
            case 2:
                return VIP;
            default:
                return null;
        }
    }
}
