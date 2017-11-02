package info.mentorme.hootmentor.Dialog;

/**
 * Created by psun on 2017-10-31.
 */

class DialogTree {
    private Node head;
    private Node current;

    DialogTree(Node aHead) {
        this.head = aHead;
    }

    void reset() {
        current = null;
    }

    // From userTalk, advance to the next node & return the response of that node.
    String botTalk(String userTalk) {
        Node next = nextNode(userTalk);
        if (next == null) {
            return "END"; // TODO CHANGE this back to null
        }

        current = next;
        return current.botTalk(userTalk);
    }

    private Node nextNode(String userTalk) {
        if (current == null) {
            return head;
        } else {
            return current.next(userTalk);
        }
    }
}
