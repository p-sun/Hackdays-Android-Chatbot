package info.androidhive.speechtotext;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import android.speech.SpeechRecognizer;

import info.androidhive.speechtotext.SpeechConverter.ConversionCompletion;
import info.androidhive.speechtotext.SpeechConverter.SpeechToTextConvertor;
import info.androidhive.speechtotext.SpeechConverter.TextToSpeechConvertor;

import android.view.animation.AlphaAnimation;

import java.lang.reflect.Array;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class MainActivity extends Activity {
	private String[] questions = {
			"What is your job?",
			"What is your level of education?",
            "What industry do you work in?",
            "How many years of related experience do you have?"
	};

	// User's answer to questions[0]
	String currentJob = "";

	// Which mode the user is currently in.
	// 0 - questions.count-1 ------ currently answering that question
	// questions.count ------------ chatbot mode
	private int questionIndex = 0;

	String currentQuestion() {
		return questions[questionIndex];
	}

	// Called when an API response returns with a botResponse
	private void askNextQuestion(String botResponse) {
        System.out.println("Question index (ask next Q) " + questionIndex);

        if (questionIndex != questions.length) {
            questionIndex++;
            // Don't increment
        }
        promptSpeechInput(botResponse);
	}

	private TextView botTextView;
	private TextView userTextView;

	private ImageButton btnSpeak;
    private ImageButton btnRestart;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		botTextView = (TextView) findViewById(R.id.questionView);
		userTextView = (TextView) findViewById(R.id.answerView);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnRestart = (ImageButton) findViewById(R.id.btnRestart);

		// hide the action bar
		getActionBar().hide();

		btnSpeak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				promptSpeechInput("");
			}
		});

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset & ask the first question!
                questionIndex = 0;
                recommendationIndex = 0;
                jobs = new ArrayList<JobRecommendation>();
                promptSpeechInput("");
            }
        });
	}

	private SpeechRecognizer myRecognizer;
	Intent intent;

    ArrayList<JobRecommendation> jobs = new ArrayList<JobRecommendation>();
    int recommendationIndex = 0;

    // 1) Display previous bot response / Manual question
    private void promptSpeechInput(String aBotResponse) {
        System.out.println("prompt speech input: " + questionIndex);
        double seconds = questionIndex == 0 ? 0.0 : 1;
        final String botResponse = aBotResponse;

        Delay.delay(seconds, new Delay.DelayCallback() {
            @Override
            public void afterDelay() {
                if ((questionIndex == questions.length) && jobs.isEmpty()) {
                    jobs = JobRecommendation.arrayForJson(botResponse);
                }

                String question = "";
                if (questionIndex < questions.length) { // 0...3  ignore answers
                    question = currentQuestion();
                } else if (questionIndex == questions.length - 1 && !botResponse.isEmpty()) { // 4
                    // Automation Percentage response
                    question = botResponse;
                } else if ((questionIndex == questions.length) && jobs.isEmpty()) { // 5
                    question = botResponse;//"Sorry, I couldn't find you an amazing match to your current job. Would you like to chat?";
                } else if ((questionIndex == questions.length) && !jobs.isEmpty()) { // 5
                    // get a job at a current index
                    JobRecommendation currentJob = jobs.get(recommendationIndex);
                    System.out.println("Current job " + currentJob);
                    // Increment index
                    recommendationIndex++;
                    recommendationIndex = recommendationIndex % jobs.size();
                    // Give a sentence
                    question = currentJob.sentence;
                } else if (questionIndex == (questions.length + 2)) { // 6
                    // Any chat
                    question = "Chat with me";
                } else { // 7
                    question = botResponse;
                    System.out.println("questionIndex--" + questionIndex);
                }

                botTextView.setText(question);
                userTextView.setText("");

                // Speak question
                TextToSpeechConvertor conv = new TextToSpeechConvertor(
                        question, getApplicationContext(), new ConversionCompletion() {

                    @Override
                    public void onCompletion(boolean success, String result) {
                        if (success) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getUserResponse();
                                }
                            });
                        }
                    }

                    @Override
                    public void onPartialResult(String partial) {}
                });
            }
        });
    }

    // (2) Post TO API from user input.
    private void getUserResponse() {
        SpeechToTextConvertor speechConverter = new SpeechToTextConvertor(this, new ConversionCompletion() {
            @Override
            public void onPartialResult(String partial) {
                userTextView.setText(partial);
            }

            @Override
            public void onCompletion(boolean success, String result) {
                if (success) {
                    userTextView.setText(result);

                    if (questionIndex < questions.length - 1) { // 3
                        if (questionIndex == 0) {
                            currentJob = result;
                        }
                        askNextQuestion("");
                    } else if (questionIndex == questions.length - 1) { // 4
                        postToAPI(Network.Type.AUTOMATION_PERCENTAGE, currentJob);
                    } else if (questionIndex == questions.length && !result.contains("chat")) { // 5
                        System.out.println("calling JOB recommender");
                        if (jobs.isEmpty()) {
                            postToAPI(Network.Type.JOB_RECOMMENDER, currentJob);
                        } else {
                            // No API needed, b/c we already searched the recs for that job!
                            promptSpeechInput("");
                        }
                    } else { // 6+
                        System.out.println("Question index @ for chatbox " + questionIndex);
                        questionIndex = questions.length + 2;
                        postToAPI(Network.Type.CHAT_BOT, result);
                    }
                }
            }
        });
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void postToAPI(Network.Type apiType, String userInput) {
        // Send it via the network
        Network network = new Network();
        try {
            network.sendPost(apiType, userInput, new CompletionHandler(){
                @Override
                public void handle(boolean success, String response) {
                    if (success) {
                        final String finalResponse = response;

                        System.out.println("API Response" + finalResponse);

                        // Update question view
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                askNextQuestion(finalResponse);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Error with POST: " + e);
        }
    }

	// Fade textView
    private void setUpFadeAnimation(final TextView textView) {
        // Start from 0.1f if you desire 90% fade animation
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(3000);
        // End to 0.1f if you desire 90% fade animation
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(3000);

        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                textView.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                textView.setText("");
                textView.setAlpha(1.0f);
//                textView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        textView.startAnimation(fadeOut);
    }

}
