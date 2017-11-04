package info.mentorme.hootmentor.Dialog.NodeSupport;

import info.mentorme.hootmentor.Dialog.Tree.BotTalkHandler;

/**
 * Created by psun on 2017-11-02.
 */

public interface Node {
    // If userTalk contains this keyword, this will be the next node
    String[] keywords();

    // After userTalk1, this node returns its response
    void botTalk(String userTalk, BotTalkHandler handler);

    // After userTalk2, this node returns the next node
    Node next(String userTalk);
}
