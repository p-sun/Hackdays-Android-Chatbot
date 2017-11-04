package info.mentorme.hootmentor.Dialog.NodeSupport;

/**
 * Created by psun on 2017-11-04.
 */

public abstract class AbstractNode implements Node {
    protected String[] keywords;
    protected Node[] children;

    public String[] keywords() {
        return keywords;
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
