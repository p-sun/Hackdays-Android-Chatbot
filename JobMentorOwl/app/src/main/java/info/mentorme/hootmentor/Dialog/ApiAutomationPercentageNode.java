package info.mentorme.hootmentor.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import info.mentorme.hootmentor.Dialog.NodeSupport.AbstractNode;
import info.mentorme.hootmentor.Dialog.NodeSupport.Node;
import info.mentorme.hootmentor.Dialog.Tree.BotTalkHandler;
import info.mentorme.hootmentor.MainActivity;
import info.mentorme.hootmentor.Networking.ApiManager;
import info.mentorme.hootmentor.Support.RegEx;

/**
 * Created by psun on 2017-11-03.
 */

interface FindSimilarHandler {
    void handleSimilarNocCodes(boolean success, String[] nocCodes);
}

interface FindPercentageHander {
    void handlePercentage(boolean success, String percentage);
}

public class ApiAutomationPercentageNode extends AbstractNode {
    public ApiAutomationPercentageNode(Node aChild) {
        this.addChild(aChild);
    }

    // After userTalk1, this node returns its response
    public void botTalk(final String userTalk, final BotTalkHandler handler) {

      findSimilar(userTalk, new FindSimilarHandler() {
            @Override
            public void handleSimilarNocCodes(boolean success, String[] nocCodes) {
                if (!success) {
                   handler.botDidTalk("Uh oh. I network errored.");
                   return;
                }

                if (nocCodes == null || nocCodes.length == 0) {
                    handler.botDidTalk("I couldn't find that job.");
                    return;
                }

                MainActivity.user.similarNocCodes = nocCodes;

                // TODO I HAVE THE NOCCODES
                for (String num: nocCodes) {
                    System.out.println(num);
                }

                String currentCode = nocCodes[0];
                findPercentage(currentCode, new FindPercentageHander() {
                    @Override
                    public void handlePercentage(boolean success, String percentage) {
                        handler.botDidTalk("The likelihood of " + userTalk + "jobs being automated is " + percentage + "%.");
                    }
                });
            }
        });
    }

    private void findPercentage(String nocCode, final FindPercentageHander handler) {
        ApiManager.Endpoint apiType = ApiManager.Endpoint.NOC_RISK;

        try {
            ApiManager.post(apiType, nocCode, new ApiManager.DidFinishPostHandler(){
                @Override
                public void handle(boolean success, String apiJson) {
                    if (!success) {
                        handler.handlePercentage(false, null);
                    }

                    String percentage = jsonContent(apiJson);

                    if (percentage == null) {
                        handler.handlePercentage(false, null);
                    }
                    handler.handlePercentage(true, percentage);
                }
            });
        } catch (Exception e) {
            System.out.println("Error with POST: " + e);
            handler.handlePercentage(false, null);
        }
    }

    private void findSimilar(String jobTitle, final FindSimilarHandler handler) {
        ApiManager.Endpoint apiType = ApiManager.Endpoint.FIND_SIMILAR;

        try {
            ApiManager.post(apiType, jobTitle, new ApiManager.DidFinishPostHandler(){
                @Override
                public void handle(boolean success, String apiJson) {
                    System.out.println("FINDSIMILAR API -- success: " + success + ", json " + apiJson);
                    if (!success) {
                        handler.handleSimilarNocCodes(false, null);
                        return;
                    }

                    String[] codes = nocCodesForJson(apiJson);
                    handler.handleSimilarNocCodes(true, codes);
                    return;
                }
            });
        } catch (Exception e) {
            System.out.println("Error with POST: " + e);
            handler.handleSimilarNocCodes(false, null);
        }
    }

    private String[] nocCodesForJson(String json) {
        String nocList = jsonContent(json);

        if (nocList == null) {
            return null;
        }

        return RegEx.matchNumbersWithSingleQuotes(nocList);
    }

    private String jsonContent(String json) {
        try {
            JSONObject object = new JSONObject(json);
            return object.getString("content");
        } catch (JSONException e) {
            System.out.println("error parsing json" + json);
        }
        return null;
    }
}
