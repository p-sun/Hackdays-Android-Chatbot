package info.mentorme.hootmentor;

import info.mentorme.hootmentor.Dialog.ApiEchoNode;
import info.mentorme.hootmentor.Dialog.ChoiceNode;
import info.mentorme.hootmentor.Dialog.Tree.DialogTree;
import info.mentorme.hootmentor.Dialog.NodeSupport.*;

public class DialogTreeBuilder {
    public static DialogTree apiConnectedTree() {

        ApiEchoNode echo = new ApiEchoNode(null);

        ChoiceNode job = new ChoiceNode(
                "Hoot Hoot! What is your job?",
                new Node[] {echo},
                new NodeAction() {
                    @Override
                    public void userDidTalk(String userTalk) {
                        MainActivity.user.currentJob = userTalk;
                    }
                });

        return new DialogTree(job);
    }

    public static DialogTree maxineTree() {
        ChoiceNode outlook = new ChoiceNode(
                "Hoot Hoot! In the city of Ottawa, there will be good " +
                        "employment growth for photographers." +
                        "You can take a 2 year diploma in photography at Algonquin College, " +
                        "close to home! Would you like to learn more about this program?",
                null,
                new String[] {"future", "outlook", "out look"});

        ChoiceNode salary = new ChoiceNode(
                "The average earnings in Ottawa is around 24000 dollars a year, which is 12 dollars per hour.",
                new Node[] {outlook},
                new String[] {"salary", " earn"});
        outlook.addChild(salary);

        ChoiceNode photography = new ChoiceNode(
                "Fantastic! Photography jobs only have a 2.1% chance of being automated! " +
                        "I can tell you the future outlook, the salary, a day in the life, or I can find jobs near you.",
                new Node[] {outlook, salary});

        ChoiceNode interest = new ChoiceNode(
                "I’m sorry to hear that. Unfortunately, 92% of " +
                        "retail associate jobs will be automated in the next 10 to 20 years. " +
                        "I can suggest jobs for you. What are some of your interests?",
                new Node[] {photography});

        ChoiceNode job = new ChoiceNode(
                "What is your job?",
                new Node[] {interest});

        ChoiceNode live = new ChoiceNode(
                "Hi Maxine. It’s a Hoot to meet you! Where do you live?",
                new Node[] {job});

        ChoiceNode name = new ChoiceNode(
                "Hoot Hoot! What is your name?",
                new Node[] {live},
                new NodeAction() {
                    @Override
                    public void userDidTalk(String userTalk) {
                        MainActivity.user.name = userTalk;
                    }
                });

        return new DialogTree(name);
    }

    public static DialogTree sampleTree() {
        ChoiceNode yes = new ChoiceNode(
                "Answered yes",
                null,
                new String[] {"yes", "yeah", "ok", "yas"});

        ChoiceNode no = new ChoiceNode(
                "Answered not-yes",
                null); // Match all keywords

        ChoiceNode booleanQuestion = new ChoiceNode(
                "Pick a boolean, yes or no?",
                new Node[] {yes, no});

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
                "What is your job? What is your job? What is your job?",
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
