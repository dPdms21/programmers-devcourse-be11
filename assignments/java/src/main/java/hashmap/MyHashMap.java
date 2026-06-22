package hashmap;

public class MyHashMap {
    static class Node {
        String key;
        String value;
        Node next;

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node[] buckets;
    private int capacity = 16;
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75;

    public MyHashMap() {
        buckets = new Node[capacity];
    }

    private int getIndex(String key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    public void put(String key, String value) {
        int idx = getIndex(key);
        Node cur = buckets[idx];

        while (cur != null) {
            if (cur.key.equals(key)) {
                cur.value = value;
                return;
            }

            cur = cur.next;
        }

        resize();

        idx = getIndex(key);

        Node node = new Node(key, value);
        node.next = buckets[idx];
        buckets[idx] = node;
        size++;
    }

    public String get(String key) {
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

    public String remove(String key) {
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

    public void resize() {
        if (size < capacity * LOAD_FACTOR) {
            return;
        }

        Node[] oldB = buckets;

        capacity *= 2;
        buckets = new Node[capacity];

        for (Node n : oldB) {
            Node cur = n;

            while (cur != null) {
                Node next = cur.next;

                int idx = getIndex(cur.key);

                cur.next = buckets[idx];
                buckets[idx] = cur;

                cur = next;
            }
        }
    }

    public String[] keySet() {
        String[] keys = new String[size];
        int idx = 0;

        for (Node n : buckets) {
            Node cur = n;

            while (cur != null) {
                keys[idx] = cur.key;
                idx++;

                cur = cur.next;
            }
        }

        return keys;
    }

    public String[] values() {
        String[] values = new String[size];
        int idx = 0;

        for (Node n : buckets) {
            Node cur = n;

            while (cur != null) {
                values[idx] = cur.value;
                idx++;

                cur = cur.next;
            }
        }

        return values;
    }
}
