package info.mentorme.hootmentor;

import info.mentorme.hootmentor.Dialog.ChoiceNode;
import info.mentorme.hootmentor.Dialog.DialogTree;
import info.mentorme.hootmentor.Dialog.Node;
import info.mentorme.hootmentor.Dialog.NodeAction;

/**
 * Created by psun on 2017-11-03.
 */

public class MaxineDialogTree {
    public static DialogTree setup() {
        ChoiceNode yes = new ChoiceNode(
                "Answered yes",
                null,
                new String[] {"yes", "yeah", "ok", "yas"},
                null);

        ChoiceNode no = new ChoiceNode(
                "Answered not-yes",
                null,
                new String[] {"*"}, // Match all keywords
                null);

        ChoiceNode booleanQuestion = new ChoiceNode(
                "Pick a boolean, yes or no?",
                new Node[] {yes, no},
                null);

        ChoiceNode educationQuestion = new ChoiceNode(
                "What is your education?",
                new Node[] { booleanQuestion },
                new NodeAction() {
                    @Override
                    public void userDidTalk(String userTalk) {
                        MainActivity.user.education = userTalk;
                    }
                });

        ChoiceNode jobQuestion = new ChoiceNode(
                "What is your job?",
                new Node[] {educationQuestion},
                new NodeAction() {
                    @Override
                    public void userDidTalk(String userTalk) {
                        MainActivity.user.currentJob = userTalk;
                    }
                });

        return new DialogTree(jobQuestion);
    }
}
