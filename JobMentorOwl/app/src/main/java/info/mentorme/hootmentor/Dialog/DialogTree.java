package info.mentorme.hootmentor.Dialog;

/**
 * Created by psun on 2017-10-31.
 */

public class DialogTree {
    private Node head;
    private Node current;

    public DialogTree(Node aHead) {
        this.head = aHead;
    }

    public void reset() {
        current = null;
    }

    // From userTalk, advance to the next node & return the response of that node.
    public String botTalk(String userTalk) {
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
