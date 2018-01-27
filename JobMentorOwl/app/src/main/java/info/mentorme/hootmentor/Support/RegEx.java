package info.mentorme.hootmentor.Support;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by psun on 2017-11-04.
 */

public class RegEx {
    private static ArrayList<String> listForContentMatching(String content, String regEx) {
        ArrayList<String> matches = new ArrayList<String>();

        // Add each regEx match to the array
        Matcher m = Pattern.compile(regEx)
                .matcher(content);
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }

    public static String[] matchNumbersWithSingleQuotes(String content) {
        String regEx = "'[0-9]*'";

        ArrayList<String> matches = listForContentMatching(content, regEx);

        String[] cleaned = new String[matches.size()];

        // Remove trim the first and last ' characters
        for (int i = 0; i < matches.size(); i++) {
            String current = matches.get(i);
            cleaned[i] = current.substring(1, current.length() - 1);
        }

        return cleaned;
    }
}
