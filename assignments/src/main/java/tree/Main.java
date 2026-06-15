package tree;

public class Main {
    public static void main(String[] args) {
        MyTree tree = new MyTree();
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        for (int v : values) tree.insert(v);

        tree.preOrder();    // 전위: 50 30 20 40 70 60 80
        tree.inOrder();     // 중위: 20 30 40 50 60 70 80   ← 정렬되어 나옴!
        tree.postOrder();   // 후위: 20 40 30 60 80 70 50
    }
}
