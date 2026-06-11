package part2;

public class G_card {
    static final int CLOVER = 1;
    static final int HEART = 2;
    static final int DIAMOND = 3;
    static final int SPADE = 4;

    static final int A = 1;
    static final int J = 11;
    static final int Q = 12;
    static final int K = 13;

    static final int KIND_MAX = 4;
    static final int NUM_MAX = 13;

    int kind;
    int num;

    public G_card() {
        this(SPADE, A);
    }

    public G_card(int kind, int num) {
        this.kind = kind;
        this.num = num;
    }

    @Override
    public String toString() {
        String[] kinds = { "", "CLOVER", "HEART", "DIAMOND", "SPADE" };
        String numbers = "0123456789XJQK";

        return "kind : " + kinds[kind] + ", number : " + numbers.charAt(num);
    }
}