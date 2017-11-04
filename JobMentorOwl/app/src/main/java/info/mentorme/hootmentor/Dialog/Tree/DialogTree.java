package info.mentorme.hootmentor.Dialog.Tree;

import info.mentorme.hootmentor.Dialog.NodeSupport.*;

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

    public boolean isAtBeginning() {
        return current == null;
    }

    // From userTalk, advance to the next node if possible
    // & return the response of that node.
    public void botTalk(String userTalk, BotTalkHandler handler) {
        Node next = nextNode(userTalk);
        if (next == null) {
            handler.botDidTalk(null);
        }

        current = next;
        if (current != null) {
            current.botTalk(userTalk, handler);
        }
    }

    private Node nextNode(String userTalk) {
        if (current == null) {
            return head;
        } else {
            return current.next(userTalk);
        }
    }
}
