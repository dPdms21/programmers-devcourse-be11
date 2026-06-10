package part2;

/*
 * ============================================================
 *  카드 게임 (트럼프 카드 덱) - 코드로 구현한 규칙 설명
 * ============================================================
 *
 *  [구성 요소]
 *  1) Card2  : 카드 한 장을 표현하는 클래스
 *     - kind   : 무늬 (1=CLOVER, 2=HEART, 3=DIAMOND, 4=SPADE)
 *     - number : 숫자 (1~13, 1=A, 11=J, 12=Q, 13=K)
 *     - 무늬 종류는 4가지(KIND_MAX), 숫자는 13가지(NUM_MAX)
 *
 *  2) Deck   : 카드 52장(4무늬 x 13숫자)을 담는 한 벌의 덱
 *
 *  [게임 규칙 / 동작]
 *  1. 덱을 만들면 SPADE(4) -> DIAMOND(3) -> HEART(2) -> CLOVER(1) 순서로
 *     각 무늬마다 1~13번 카드가 차례로 채워져 총 52장이 생성된다.
 *
 *  2. 카드 뽑기(pick)
 *     - pick(index) : 지정한 위치의 카드를 꺼낸다.
 *     - pick()      : 0~51 사이 무작위 위치의 카드를 꺼낸다.
 *
 *  3. 섞기(shuffle)
 *     - 카드를 처음부터 끝까지 돌면서, 매번 무작위 위치의 카드와
 *       서로 자리를 맞바꿔(swap) 덱 전체를 무작위로 뒤섞는다.
 *
 *  [실행 흐름 - main]
 *  (1) 새 덱을 만든 뒤 0번 카드를 출력  -> 섞기 전이라 항상 같은 카드(SPADE A)
 *  (2) 덱을 섞은 뒤 0번 카드를 출력      -> 무작위로 바뀌어 실행할 때마다 달라짐
 *
 *  [학습 포인트]
 *  - 클래스/객체로 현실의 사물(카드, 덱)을 모델링하는 방법
 *  - 생성자 오버로딩(기본 생성자 / 매개변수 생성자)
 *  - 배열을 이용한 데이터 묶음 관리와 swap을 통한 셔플 알고리즘
 *  - toString() 오버라이딩으로 객체를 보기 좋게 출력하기
 * ============================================================
 */

import java.util.*;

class Card2 {
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

    public Card2() {
        this(SPADE, A);
    }

    public Card2(int kind, int num) {
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

class Deck {
    private static final Random random = new Random();
    final int CARD_NUM = 52;

    Card2[] cards = new Card2[CARD_NUM];

    public Deck() {
        int idx = 0;

        for (int kind=Card2.KIND_MAX; kind>0; kind--) {
            for (int num=1; num<=Card2.NUM_MAX; num++) {
                cards[idx] = new Card2(kind, num);
                idx++;
            }
        }
    }

    public Card2 pick(int idx) {
        return cards[idx];
    }

    public Card2 pick() {
        int idx = random.nextInt(CARD_NUM);

        return pick(idx);
    }

    public void shuffle() {
        for ( int i = 0; i < cards.length; i++ ) {
            int idx = random.nextInt(CARD_NUM);

            Card2 temp = cards[idx];
            cards[idx] = cards[i];
            cards[i] = temp;
        }
    }
}

public class G_card_play {
    public static void main(String[] args) {
        Deck deck = new Deck();

        Card2 card = deck.pick(0);
        System.out.println(card);

        deck.shuffle();

        card = deck.pick(0);
        System.out.println(card);
    }
}
