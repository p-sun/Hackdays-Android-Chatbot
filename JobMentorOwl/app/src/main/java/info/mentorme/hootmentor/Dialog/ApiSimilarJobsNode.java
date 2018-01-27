package info.mentorme.hootmentor.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import info.mentorme.hootmentor.Dialog.NodeSupport.AbstractNode;
import info.mentorme.hootmentor.Dialog.NodeSupport.Node;
import info.mentorme.hootmentor.Dialog.Tree.BotTalkHandler;
import info.mentorme.hootmentor.MainActivity;
import info.mentorme.hootmentor.Networking.ApiManager;

public class ApiSimilarJobsNode extends AbstractNode {

    public ApiSimilarJobsNode(Node aChild) {
        this.addChild(aChild);
    }

    // After userTalk1, this node returns its response
    public void botTalk(String userTalk, BotTalkHandler handler) {
        postToAPI(userTalk, handler);
    }

    private void postToAPI(String userInput, final BotTalkHandler handler) {
        ApiManager.Endpoint apiType = ApiManager.Endpoint.ECHO;

        String[] codes = MainActivity.user.similarNocCodes;

        if (codes == null) {
            handler.botDidTalk("I couldn't find the job.");
        }


        handler.botDidTalk("My list of jobs is " + MainActivity.user.similarNocCodes);
//
//        try {
//            ApiManager.post(apiType, userInput, new ApiManager.DidFinishPostHandler(){
//                @Override
//                public void handle(boolean success, String apiJson) {
//                    if (success) {
//                        String botTalk = getJsonContent(apiJson);
//                        handler.botDidTalk(botTalk);
//                    } else {
//                        handler.botDidTalk("Uh oh. I network errored");
//                    }
//                }
//            });
//        } catch (Exception e) {
//            System.out.println("Error with POST: " + e);
//            handler.botDidTalk("Uh oh. I network errored");
//        }
    }

    private String getJsonContent(String json) {
        try {
            JSONObject object = new JSONObject(json);
            return object.getString("content");
        } catch (JSONException e) {
            System.out.println("error parsing json for job recs: " + json);
        }
        return null;
    }
}
