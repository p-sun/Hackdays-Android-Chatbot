/**
 * Created by psun on 2017-10-14.
 */

package info.mentorme.hootmentor.Networking;

import java.net.URL;

public class ApiManager {
    public interface DidFinishPostHandler {
        void handle(boolean success, String response);
    }

    public enum Endpoint {
        ECHO, CHAT_BOT, JOB_RECOMMENDER, FIND_SIMILAR, AUTOMATION_PERCENTAGE
    }

    private String linkForEndpoint(Endpoint endpoint) {
        switch (endpoint) {
            case ECHO:
                return "/echo";
            case CHAT_BOT:
                return "/chatbot";
            case JOB_RECOMMENDER:
                return "/job_recommender";
            case FIND_SIMILAR:
                return "/findSimilar";
            case AUTOMATION_PERCENTAGE:
                return "/automation_percentage";
        }
        return "";
    }

    // HTTP POST request
    public void post(Endpoint endpoint, String userResponse, DidFinishPostHandler completion) throws Exception {
        String link = linkForEndpoint(endpoint);
        URL url = new URL("https://hoot-mentor.appspot.com/_ah/api/jobSearch/v1/jobSearch" + link);

        String urlParameters = "content=" + userResponse;

        new PostUserResponseTask(url, urlParameters, completion).execute();
    }
}
