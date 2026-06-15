package treemap;

public class MyTreeMap {
    public class Node {
        public String key;
        public Integer value;
        public Node left;
        public Node right;

        public Node(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size = 0;

    public void put(String key, Integer value) {
        root = putNode(root, key, value);
    }

    private Node putNode(Node node, String key, Integer value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int cmp =  key.compareTo(node.key);

        if (cmp < 0) {
            node.left = putNode(node.left, key, value);
        }
        else if (cmp > 0) {
            node.right = putNode(node.right, key, value);
        }
        else {
            node.value = value;
        }

        return node;
    }

    public Integer get(String key) {
        Node node = root;

        while (node != null) {
            int  cmp = key.compareTo(node.key);

            if (cmp < 0) {
                node = node.left;
            }
            else if (cmp > 0) {
                node = node.right;
            }
            else {
                return node.value;
            }
        }

        return null;
    }

    public void printSorted() {
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        if (node == null) {
            return;
        }

        inOrder(node.left);
        System.out.print("[" + node.key + "=" + node.value + "] ");
        inOrder(node.right);
    }

    public int size() {
        return size;
    }

    public boolean containsKey(String key) {
        Node node = root;

        while (node != null) {
            int cmp =  key.compareTo(node.key);

            if (cmp < 0) {
                node = node.left;
            }
            else if (cmp > 0) {
                node = node.right;
            }
            else {
                return true;
            }
        }

        return false;
    }

    public String firstKey() {
        if (root == null) {
            return null;
        }

        Node node = root;

        while (node.left != null) {
            node = node.left;
        }

        return node.key;
    }

    public String lastKey() {
        if (root == null) {
            return null;
        }

        Node node = root;

        while (node.right != null) {
            node = node.right;
        }

        return node.key;
    }

    public Integer remove(String key) {
        Integer value = get(key);

        if (value == null) {
            return null;
        }

        root = removeNode(root, key);
        size--;

        return value;
    }

    private Node removeNode(Node node, String key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = removeNode(node.left, key);
        }
        else if (cmp > 0) {
            node.right = removeNode(node.right, key);
        }
        else {
            if (node.left == null) {
                return node.right;
            }

            if (node.right == null) {
                return node.left;
            }

            Node temp =  node.right;

            while (temp.left != null) {
                temp = temp.left;
            }

            node.key = temp.key;
            node.value = temp.value;
            node.right = removeNode(node.right, temp.key);
        }

        return node;
    }
}
