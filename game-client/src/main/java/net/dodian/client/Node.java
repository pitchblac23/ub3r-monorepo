package net.dodian.client;

public class Node {

    public final void unlink() {
        if (next == null) {
        } else {
            next.prev = prev;
            prev.next = next;
            prev = null;
            next = null;
        }
    }

    public Node() {
    }

    public long id;
    public Node prev;
    public Node next;
}