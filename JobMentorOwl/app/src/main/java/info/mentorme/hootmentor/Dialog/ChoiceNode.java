package info.mentorme.hootmentor.Dialog;

import info.mentorme.hootmentor.Dialog.NodeSupport.*;
import info.mentorme.hootmentor.Dialog.Tree.BotTalkHandler;

/**
 * Created by psun on 2017-11-02.
 */

public class ChoiceNode extends AbstractNode {
    private String botTalk;

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

    public void botTalk(String userTalk, BotTalkHandler handler) {
        if (action != null) {
            this.action.userDidTalk(userTalk);
        }

        handler.botDidTalk(botTalk);
    }
}
