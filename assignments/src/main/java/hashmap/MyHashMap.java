package hashmap;

public class MyHashMap {
    static class Node {
        String key;
        Integer value;
        Node next;

        Node(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node[] buckets;
    private int capacity = 16;
    private int size = 0;

    public MyHashMap() {
        buckets = new Node[capacity];
    }

    private int getIndex(String key) {
        return Math.abs(key.hashCode() % capacity);
    }

    public void put(String key, Integer value) {
        int idx = getIndex(key);
        Node cur = buckets[idx];

        while (cur != null) {
            if (cur.key.equals(key)) {
                cur.value = value;
                return;
            }

            cur = cur.next;
        }

        Node node = new Node(key, value);
        node.next = buckets[idx];
        buckets[idx] = node;
        size++;
    }

    public Integer get(String key) {
        int idx = getIndex(key);
        Node cur = buckets[idx];

        while (cur != null) {
            if (cur.key.equals(key)) {
                return cur.value;
            }

            cur = cur.next;
        }

        return null;
    }

    public int size() {
        return size;
    }

    public boolean containsKey(String key) {
        int idx = getIndex(key);
        Node cur = buckets[idx];

        while (cur != null) {
            if (cur.key.equals(key)) {
                return true;
            }

            cur = cur.next;
        }

        return false;
    }

    public Integer remove(String key) {
        int idx = getIndex(key);
        Node cur = buckets[idx];
        Node prev = null;

        while (cur != null) {
            if (cur.key.equals(key)) {
                if (prev == null) {
                    buckets[idx] = cur.next;
                }
                else {
                    prev.next = cur.next;
                }

                size--;
                return cur.value;
            }

            prev = cur;
            cur = cur.next;
        }

        return null;
    }
}
