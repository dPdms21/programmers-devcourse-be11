package part3;

public enum B_day_2 {
    SUNDAY("Holiday"),
    MONDAY("Workday"),
    TUESDAY("Workday");

    // 필드
    private String desc;

    // 생성자
    B_day_2(String desc) {
        this.desc = desc;
    }

    // 메서드
    public String getDesc() {
        return desc;
    }
}
