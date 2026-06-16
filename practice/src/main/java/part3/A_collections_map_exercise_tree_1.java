package part3;

class MyTree {
    static class Node {
        int value;
        Node left;
        Node right;

        Node(int value) {
            this.value = value;
        }
    }

    private Node root;

    // 값 넣기
    public void insert(int value) {
        root = insertNode(root, value);
    }

    private Node insertNode(Node node, int value) {
        if ( node == null ) {
            return new Node(value);
        }

        // 작으면 왼쪽
        if ( value < node.value ) {
            node.left = insertNode(node.left, value);
        } else if ( value > node.value ) {
            node.right = insertNode(node.right, value);
        }

        return node;
    }

    /*
     * 세 가지 순회(traversal) : 모든 노드를 빠짐없이 한 번씩 방문하는 방법
     *
     * 한 노드에서 할 일은 딱 3가지 - (L)왼쪽 가기, (V)자기자신 출력, (R)오른쪽 가기
     * 이 3개의 "순서"를 어떻게 두느냐에 따라 이름이 갈림
     *
     *   - 전위(preorder)  : V -> L -> R   (나를 먼저 찍고 내려감)
     *   - 중위(inorder)   : L -> V -> R   (왼쪽 다 보고 -> 나 -> 오른쪽)
     *   - 후위(postorder) : L -> R -> V   (자식 다 본 뒤 마지막에 나)
     *
     * 핵심 원리: 한 노드에 도착하면 "그 노드를 뿌리로 하는 작은 트리"에 대해
     *           또 같은 규칙을 반복한다(재귀). null을 만나면 멈추고 되돌아옴
     *
     * 아래 main의 입력 { 50, 30, 70, 20, 40, 60, 80 }이 만드는 트리:
     *
     *              50
     *            /    \
     *          30      70
     *         /  \    /  \
     *       20   40  60   80
     *
     *   전위 : 50 30 20 40 70 60 80   (뿌리부터 위에서 아래로 훑는 느낌)
     *   중위 : 20 30 40 50 60 70 80   (왼->나->오 라서 항상 "정렬된 순서"로 나옴)
     *   후위 : 20 40 30 60 80 70 50   (자식부터 처리 -> 삭제/메모리 해제에 어울림)
     *
     * 중위순회가 정렬되는 이유:
     *   왼쪽엔 항상 나보다 작은 값, 오른쪽엔 큰 값이 있으니
     *   "왼쪽 -> 나 -> 오른쪽" 순서로 찍으면 저절로 오름차순 정렬이 됨
     */

    // 세 가지 순회

    // 전위 : 루트 -> 왼 -> 오
    public void preOrder() {
        System.out.print("[전위] ");
        preOrder(root);
        System.out.println();
    }

    private void preOrder(Node node) {
        // 기저 조건
        if ( node == null ) return;

        System.out.print(node.value + " "); // 루트 먼저
        preOrder(node.left);
        preOrder(node.right);
    }

    // 중위 : 왼 -> 루트 -> 오
    public void inOrder() {
        System.out.print("[중위] ");
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        if ( node == null ) return;

        inOrder(node.left);
        System.out.print(node.value + " ");
        inOrder(node.right);
    }

    // 후위 : 왼 -> 오 -> 루트
    public void postOrder() {
        System.out.print("[후위] ");
        postOrder(root);
        System.out.println();
    }

    private void postOrder(Node node) {
        if ( node == null ) {
            return;
        }

        postOrder(node.left);
        postOrder(node.right);
        System.out.print(node.value + " ");
    }
}

public class A_collections_map_exercise_tree_1 {
    public static void main(String[] args) {
        MyTree tree = new MyTree();
        int[] values = { 50, 30, 70, 20, 40, 60, 80 };

        for (int value : values) {
            tree.insert(value);
        }

        tree.preOrder();
        tree.inOrder();
        tree.postOrder();
    }
}
