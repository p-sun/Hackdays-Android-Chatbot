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
        ECHO, FIND_SIMILAR, NOC_RISK, MARKET_REPORT, NOC_TO_TITLE
    }

    private static String linkForEndpoint(Endpoint endpoint) {
        switch (endpoint) {
            // Requires {'content': 'anything'}; returns {'content': 'anything'}
            case ECHO:
                return "/echo";

            // Requires {'content': 'job title'}; returns {'content': "['list', 'jobs']"}
            case FIND_SIMILAR:
                return "/findSimilar";

            // Requires {'content': 'noc_code'}; returns {'content': 'job risk'}
            // job risk is a Float
            case NOC_RISK:
                return "/nocRisk";


            case NOC_TO_TITLE:
                return "/nocCodeToTitle";

            // Requires NOC_CODE -> Returns report
            // https://github.com/jQwotos/HootMentor/tree/google-app-engine#ahapijobsearchv1jobsearchmarketreportdetails
            case MARKET_REPORT:
                return "/marketReportDetails";

        }
        return "";
    }

    // HTTP POST request
    public static void post(Endpoint endpoint, String userResponse, DidFinishPostHandler completion) throws Exception {
        String link = linkForEndpoint(endpoint);
        URL url = new URL("https://hoot-mentor.appspot.com/_ah/api/jobSearch/v1/jobSearch" + link);

        String urlParameters = "content=" + userResponse;

        new PostUserResponseTask(url, urlParameters, completion).execute();
    }
}
