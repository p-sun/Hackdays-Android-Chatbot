package info.mentorme.hootmentor.Dialog;

/**
 * Created by psun on 2017-11-02.
 */

public class ChoiceNode implements Node {
    private String[] keywords;
    private String botTalk;
    private Node[] children;
    private NodeAction action;

    public ChoiceNode(String aBotTalk, Node[] someChildren) {
        setup(aBotTalk, someChildren, null, null);
    }

    public ChoiceNode(String aBotTalk, Node[] someChildren, NodeAction aAction) {
        setup(aBotTalk, someChildren, null, aAction);
    }

    public ChoiceNode(String aBotTalk, Node[] someChildren, String[] someKeywords) {
        setup(aBotTalk, someChildren, someKeywords, null);
    }

    public ChoiceNode(String aBotTalk, Node[] someChildren, String[] someKeywords,  NodeAction aAction) {
        setup(aBotTalk, someChildren, someKeywords, aAction);
    }

    private void setup(String aBotTalk, Node[] someChildren, String[] someKeywords,  NodeAction aAction) {
        this.botTalk = aBotTalk;
        this.children = someChildren;
        this.keywords = someKeywords;
        this.action = aAction;
    }

    // Append a child to the children array
    public void addChild(Node child) {
        int oldLength = children == null ? 0 : children.length;

        Node[] newChildren = new Node[oldLength + 1];
        for (int i = 0; i < oldLength; i++) {
            newChildren[i] = children[i];
        }
        newChildren[oldLength] = child;

        children = newChildren;
    }

    public String[] keywords() {
        return keywords;
    }

    public String botTalk(String userTalk) {
        if (action != null) {
            this.action.userDidTalk(userTalk);
        }

        return botTalk;
    }

    // Return the NEXT child
    public Node next(String userTalk) {
        if (children == null || children.length == 0) {
            return null;
        }

        for (Node child: children) {
            // Return child if it doesn't require a keyword.
            if (child.keywords() == null) {
                return child;
            }

            // Return child if userTalk contains one of the child's keywords.
            for (String key: child.keywords()) {
                if (userTalk.toLowerCase().contains(key)) {
                    return child;
                }
            }
        }

        return null;
    }
}
