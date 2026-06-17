package part3;

public enum B_price_plan {
    LITE(10),
    BASIC(20),
    PREMIUM(30);

    private int capacity;

    B_price_plan(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    // 사용자가 고른 번호(1~3)로 요금제 찾기
    public static B_price_plan from(int choice) {
        switch (choice) {
            case 1: return LITE;
            case 2: return BASIC;
            case 3: return PREMIUM;
            default: return null;
        }
    }
}
