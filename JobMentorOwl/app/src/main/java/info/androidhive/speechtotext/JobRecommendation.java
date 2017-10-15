package info.androidhive.speechtotext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by psun on 2017-10-15.
 */

public class JobRecommendation {
    String job;
    String sentence;

    public JobRecommendation(String aJob, String aSentence) {
        this.job = aJob;
        this.sentence = aSentence;
    }

    public static ArrayList<JobRecommendation> arrayForJson(String json) {
        ArrayList<JobRecommendation> jobs = new ArrayList<JobRecommendation>();

        try {
            JSONObject object = new JSONObject(json);
            JSONArray jobArray = object.getJSONArray("jobs");
            JSONArray sentenceArray = object.getJSONArray("sentences");

            for (int i = 0; i < jobArray.length(); i++) {
                JobRecommendation job = new JobRecommendation(jobArray.getString(i), sentenceArray.getString(i));
                jobs.add(job);
                System.out.println("Job " + jobArray.getString(i));
                System.out.println("Sentence  " + sentenceArray.getString(i));
            }
        } catch (JSONException e) {
            System.out.println("error parsing json for job recs: " + json);
        }

        return jobs;
    }
}
