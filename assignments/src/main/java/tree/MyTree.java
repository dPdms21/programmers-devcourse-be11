package tree;

import java.util.*;

public class MyTree {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    public Node root;

    public void insert(int value) {
        root = insertNode(root, value);
    }

    private Node insertNode(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }

        if (value < node.value) {
            node.left = insertNode(node.left, value);
        }
        else if (value > node.value) {
            node.right = insertNode(node.right, value);
        }

        return node;
    }

    public void preOrder() {
        System.out.print("전위 순회: ");
        preOrder(root);
        System.out.println();
    }

    private void preOrder(Node node) {
        if (node == null) {
            return;
        }

        System.out.print(node.value + " ");
        preOrder(node.left);
        preOrder(node.right);
    }

    public void inOrder() {
        System.out.print("중위 순회: ");
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        if (node == null) {
            return;
        }

        inOrder(node.left);
        System.out.print(node.value + " ");
        inOrder(node.right);
    }

    public void postOrder() {
        System.out.print("후위 순회: ");
        postOrder(root);
        System.out.println();
    }

    private void postOrder(Node node) {
        if (node == null) {
            return;
        }

        postOrder(node.left);
        postOrder(node.right);
        System.out.print(node.value + " ");
    }

    public void levelOrder() {
        System.out.print("레벨 순회: ");

        if (root == null) {
            System.out.println();
            return;
        }

        Queue<Node> q = new LinkedList<>();
        q.offer(root);

        while (!q.isEmpty()) {
            Node node = q.poll();

            System.out.print(node.value + " ");

            if (node.left != null) {
                q.offer(node.left);
            }

            if (node.right != null) {
                q.offer(node.right);
            }
        }

        System.out.println();
    }

    public int height() {
        return heightNode(root);
    }

    private int heightNode(Node node) {
        if (node == null) {
            return 0;
        }

        int leftH = heightNode(node.left);
        int rightH = heightNode(node.right);

        return Math.max(leftH, rightH) + 1;
    }

    public int countN() {
        return countNodes(root);
    }

    private int countNodes(Node node) {
        if (node == null) {
            return 0;
        }

        return countNodes(node.left) + countNodes(node.right) + 1;
    }

    public int countL() {
        return countLeaves(root);
    }

    private int countLeaves(Node node) {
        if (node == null) {
            return 0;
        }

        if (node.left == null && node.right == null) {
            return 1;
        }

        return countLeaves(node.left) + countLeaves(node.right);
    }

    public boolean search(int value) {
        return searchNode(root, value);
    }

    private boolean searchNode(Node node, int value) {
        if (node == null) {
            return false;
        }

        if (value == node.value) {
            return true;
        }

        if (value < node.value) {
            return searchNode(node.left, value);
        }

        return searchNode(node.right, value);
    }

    public void iterativePreOrder() {
        System.out.print("전위 순회(반복): ");

        if (root == null) {
            System.out.println();
            return;
        }

        Stack<Node> s = new Stack<>();
        s.push(root);

        while (!s.isEmpty()) {
            Node node = s.pop();

            System.out.print(node.value + " ");

            if (node.right != null) {
                s.push(node.right);
            }

            if (node.left != null) {
                s.push(node.left);
            }
        }

        System.out.println();
    }

    public void iterativeInOrder() {
        System.out.print("중위 순회(반복): ");

        Stack<Node> s = new Stack<>();
        Node cur = root;

        while (cur != null || !s.isEmpty()) {
            while (cur != null) {
                s.push(cur);
                cur = cur.left;
            }

            cur = s.pop();
            System.out.print(cur.value + " ");

            cur = cur.right;
        }

        System.out.println();
    }
}
