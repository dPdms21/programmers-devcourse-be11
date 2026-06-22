package part2;

import java.util.Random;

public class G_deck {
    private static final Random random = new Random();
    final int CARD_NUM = 52;

    G_card[] cards = new G_card[CARD_NUM];

    public G_deck() {
        int idx = 0;

        for (int kind=G_card.KIND_MAX; kind>0; kind--) {
            for (int num=1; num<=G_card.NUM_MAX; num++) {
                cards[idx] = new G_card(kind, num);
                idx++;
            }
        }
    }

    public G_card pick(int idx) {
        return cards[idx];
    }

    public G_card pick() {
        int idx = random.nextInt(CARD_NUM);

        return pick(idx);
    }

    public void shuffle() {
        for ( int i = 0; i < cards.length; i++ ) {
            int idx = random.nextInt(CARD_NUM);

            G_card temp = cards[idx];
            cards[idx] = cards[i];
            cards[i] = temp;
        }
    }
}