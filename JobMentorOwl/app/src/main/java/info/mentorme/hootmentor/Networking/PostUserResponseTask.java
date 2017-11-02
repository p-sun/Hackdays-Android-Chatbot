package info.mentorme.hootmentor.Networking;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class PostUserResponseTask extends AsyncTask<String, Void, Void> {
    private URL url;
    private final String urlParameters;
    private final ApiManager.DidFinishPostHandler completion;

    public PostUserResponseTask(URL aURL, String parameters, ApiManager.DidFinishPostHandler completion) {
        this.url = aURL;
        this.urlParameters = parameters;
        this.completion = completion;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
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
            output.append(System.getProperty("line.separator")  + "Endpoint " + "POST");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            output.append(System.getProperty("line.separator")
                    + "Response "
                    + System.getProperty("line.separator")
                    + System.getProperty("line.separator")
                    + responseOutput.toString());

            this.completion.handle(true, responseOutput.toString());
            br.close();

        } catch (Exception e) {
            System.out.println("MentorMe Exception on POST: " + e);
            this.completion.handle(false, "");
        }

        return null;
    }
}
