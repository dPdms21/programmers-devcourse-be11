package tree;

public class Main {
    public static void main(String[] args) {
        MyTree tree = new MyTree();
        int[] values = {50, 30, 70, 20, 40, 60, 80};

        for (int value : values) {
            tree.insert(value);
        }

        // 재귀 순회
        tree.preOrder();              // 50 30 20 40 70 60 80
        tree.inOrder();               // 20 30 40 50 60 70 80
        tree.postOrder();             // 20 40 30 60 80 70 50

        // 레벨 순회
        tree.levelOrder();            // 50 30 70 20 40 60 80

        // 트리 정보
        System.out.println("트리 높이: " + tree.height());       // 3
        System.out.println("전체 노드 수: " + tree.countN());    // 7
        System.out.println("잎 노드 수: " + tree.countL());      // 4

        // 값 탐색
        System.out.println("60 탐색: " + tree.search(60));       // true
        System.out.println("90 탐색: " + tree.search(90));       // false

        // 반복문 순회
        tree.iterativePreOrder();     // 50 30 20 40 70 60 80
        tree.iterativeInOrder();      // 20 30 40 50 60 70 80
    }
}
