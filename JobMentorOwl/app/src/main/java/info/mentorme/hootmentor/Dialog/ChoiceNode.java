package info.mentorme.hootmentor.Dialog;

/**
 * Created by psun on 2017-11-02.
 */

interface NodeAction {
    void userDidTalk(String userTalk);
}

interface Node {
    // If userTalk contains this keyword, this will be the next node
    String[] keywords();

    // After userTalk1, this node returns its response
    String botTalk(String userTalk);

    // After userTalk2, this node returns the next node
    Node next(String userTalk);
}

class ChoiceNode implements Node {
    private String[] keywords;
    private String botTalk;
    private Node[] children;
    private NodeAction action;

    ChoiceNode(String aBotTalk, Node[] someChildren, NodeAction aAction) {
        setup(aBotTalk, someChildren, null, aAction);
    }

    ChoiceNode(String aBotTalk, ChoiceNode[] someChildren, String[] someKeywords,  NodeAction aAction) {
        setup(aBotTalk, someChildren, someKeywords, aAction);
    }

    private void setup(String aBotTalk, Node[] someChildren, String[] someKeywords,  NodeAction aAction) {
        this.botTalk = aBotTalk;
        this.children = someChildren;
        this.keywords = someKeywords;
        this.action = aAction;
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

        if (children.length == 1) {
            return children[0];
        }

        for (Node child: children) {
            for (String key: child.keywords()) {
                System.out.println("child " + child + " key " + key);

                if (key == "*" || userTalk.contains(key)) {
                    System.out.println("found key" + key + " " + child.keywords());
                    return child;
                }
            }
        }

        return null; // TODO return first child if no keywords match???? OR deal with the error?
    }
}
