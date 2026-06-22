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

    public int height() {
        return heightNode(root);
    }

    private int heightNode(Node node) {
        if (node == null) {
            return 0;
        }

        int leftHeight = heightNode(node.left);
        int rightHeight = heightNode(node.right);

        return Math.max(leftHeight, rightHeight) + 1;
    }

    public void headMap(String key) {
        headMapNode(root, key);
        System.out.println();
    }

    private void headMapNode(Node node, String key) {
        if (node == null) {
            return;
        }

        int cmp = node.key.compareTo(key);

        if (cmp < 0) {
            headMapNode(node.left, key);
            System.out.print("[" + node.key + " : " + node.value + "] ");
            headMapNode(node.right, key);
        }
        else {
            headMapNode(node.left, key);
        }
    }

    public void subMap(String fromKey, String toKey) {
        subMapNode(root, fromKey, toKey);
        System.out.println();
    }

    private void subMapNode(Node node, String fromKey, String toKey) {
        if (node == null) {
            return;
        }

        int fromCmp = node.key.compareTo(fromKey);
        int toCmp = node.key.compareTo(toKey);


        if (fromCmp < 0) {
            subMapNode(node.right, fromKey, toKey);
        }
        else if (toCmp > 0) {
            subMapNode(node.left, fromKey, toKey);
        }
        else {
            subMapNode(node.left, fromKey, toKey);
            System.out.print("[" + node.key + " : " + node.value + "] ");
            subMapNode(node.right, fromKey, toKey);
        }
    }

    public String ceilingKey(String key) {
        Node node = root;
        String temp = null;

        while (node != null) {
            int cmp = key.compareTo(node.key);

            if (cmp == 0) {
                return node.key;
            }

            if (cmp < 0) {
                temp = node.key;
                node = node.left;
            }
            else {
                node = node.right;
            }
        }

        return temp;
    }

    public String floorKey(String key) {
        Node node = root;
        String temp = null;

        while (node != null) {
            int cmp = key.compareTo(node.key);

            if (cmp == 0) {
                return node.key;
            }

            if (cmp > 0) {
                temp = node.key;
                node = node.right;
            }
            else {
                node = node.left;
            }
        }

        return temp;
    }
}
