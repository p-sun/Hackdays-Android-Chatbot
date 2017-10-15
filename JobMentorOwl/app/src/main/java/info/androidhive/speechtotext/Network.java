/**
 * Created by psun on 2017-10-14.
 */

package info.androidhive.speechtotext;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.*;

import java.net.URL;
import android.os.AsyncTask;

import javax.net.ssl.HttpsURLConnection;

interface CompletionHandler {
    public void handle(boolean success, String response);
}

public class Network {

    class PostUserResponseTask extends AsyncTask<String, Void, Void> {
        private final String userResponse;
        private final CompletionHandler completion;

        public PostUserResponseTask(String userAnswer, CompletionHandler completion) {
            this.userResponse = userAnswer;
            this.completion = completion;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("https://272c67d1.ngrok.io/response");
                String urlParameters = "response=" + userResponse;
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(urlParameters);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator")  + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
                this.completion.handle(true, responseOutput.toString());

                br.close();

            } catch (Exception e) {
                System.out.println("MentorMe Exception on POST: " + e);
                this.completion.handle(false, "");
            }

            return null;
        }
    }

    // HTTP POST request
    public void sendPost(String userResponse, CompletionHandler completion) throws Exception {
        new PostUserResponseTask(userResponse, completion).execute();
    }
}
