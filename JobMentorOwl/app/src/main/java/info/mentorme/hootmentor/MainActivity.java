package info.mentorme.hootmentor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import info.mentorme.hootmentor.Dialog.*;
import info.mentorme.hootmentor.Models.User;
import info.mentorme.hootmentor.Networking.ApiManager;
import info.mentorme.hootmentor.SpeechConverter.ConversionCompletion;
import info.mentorme.hootmentor.SpeechConverter.SpeechToTextConvertor;
import info.mentorme.hootmentor.SpeechConverter.TextToSpeechConvertor;
import info.mentorme.hootmentor.Support.*;

import android.view.animation.AlphaAnimation;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class MainActivity extends Activity {

    final User user = new User();
    DialogTree dialog;

//	private String[] questions = {
//			"What is your job?",
////			"What is your level of education?",
////            "What industry do you work in?",
////            "How many years of related experience do you have?"
//	};

	// User's answer to questions[0]
//	String currentJob = "";

	// Called when an API response returns with a botResponse
	private void askNextQuestion(String botResponse) {
//        System.out.println("Question index (ask next Q) " + questionIndex);

//        if (questionIndex != questions.length) {
//            questionIndex++;
//            // Don't increment
//        }
//
//
//        displayBotSpeak(botResponse);
	}

	private TextView botTextView;
	private TextView userTextView;

	private ImageButton micButton;
    private ImageButton restartButton;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		botTextView = (TextView) findViewById(R.id.questionView);
		userTextView = (TextView) findViewById(R.id.answerView);
		micButton = (ImageButton) findViewById(R.id.btnSpeak);
        restartButton = (ImageButton) findViewById(R.id.btnRestart);

		// hide the action bar
		getActionBar().hide();

		micButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    // TODO getUserTalk
//				displayBotSpeak("");

                if (dialog.isAtBeginning()) {
                    displayNextNode("");
                } else {
                    getUserResponse();
                }
			}
		});

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset & ask the first question!
                dialog.reset();
                recommendationIndex = 0;
                jobs = new ArrayList<JobRecommendation>();

                displayNextNode("");
            }
        });

        setupDialogTree();

        Permission.requestRecordAudioPermission(getApplicationContext(), this);
	}

    ArrayList<JobRecommendation> jobs = new ArrayList<JobRecommendation>();
    int recommendationIndex = 0;

    // 1) Bot displays text, and speaks it.
    private void displayBotSpeak(String aBotTalk) {
//        System.out.println("prompt speech input: " + questionIndex);
        double seconds = 1.0;//questionIndex == 0 ? 0.0 : 1;
        final String botTalk = aBotTalk;

        Delay.delay(seconds, new Delay.DelayCallback() {
            @Override
            public void afterDelay() {
                // Set text
                botTextView.setText(botTalk);
                userTextView.setText("");

                // Speak question
                TextToSpeechConvertor conv = new TextToSpeechConvertor(
                        botTalk, getApplicationContext(), new ConversionCompletion() {

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


//    // 1) Bot displays text, and speaks it.
//    private void displayBotSpeak(String aBotTalk) {
////        System.out.println("prompt speech input: " + questionIndex);
//        double seconds = 1.0;//questionIndex == 0 ? 0.0 : 1;
//        final String botTalk = aBotTalk;
//
//        Delay.delay(seconds, new Delay.DelayCallback() {
//            @Override
//            public void afterDelay() {
////                if ((questionIndex == questions.length) && jobs.isEmpty()) {
////                    jobs = JobRecommendation.arrayForJson(botTalk);
////                }
//
//                // Get current question
//
//
//
////                String question = "";
////                if (questionIndex < questions.length) { // 0...3  ignore answers
////                    question = currentQuestion();
////                } else if (questionIndex == questions.length - 1 && !botTalk.isEmpty()) { // 4
////                    // Automation Percentage response
////                    question = botTalk;
////                } else if ((questionIndex == questions.length) && jobs.isEmpty()) { // 5
////                    question = botTalk;//"Sorry, I couldn't find you an amazing match to your current job. Would you like to chat?";
////                } else if ((questionIndex == questions.length) && !jobs.isEmpty()) { // 5
////                    // get a job at a current index
////                    JobRecommendation currentJob = jobs.get(recommendationIndex);
////                    System.out.println("Current job " + currentJob);
////                    // Increment index
////                    recommendationIndex++;
////                    recommendationIndex = recommendationIndex % jobs.size();
////                    // Give a sentence
////                    question = currentJob.sentence;
////                } else if (questionIndex == (questions.length + 2)) { // 6
////                    // Any chat
////                    question = "Chat with me";
////                } else { // 7
////                    question = botTalk;
////                    System.out.println("questionIndex--" + questionIndex);
////                }
//
//                botTextView.setText(question);
//                userTextView.setText("");
//
//                // Speak question
//                TextToSpeechConvertor conv = new TextToSpeechConvertor(
//                        question, getApplicationContext(), new ConversionCompletion() {
//
//                    @Override
//                    public void onCompletion(boolean success, String result) {
//                        if (success) {
//                            MainActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getUserResponse();
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onPartialResult(String partial) {}
//                });
//            }
//        });
//    }


    // Once we have the userTalk, go to next Node, and display the botTalk
    private void displayNextNode(String userTalk) {
        String botTalk = dialog.botTalk(userTalk);
        if (botTalk != null && botTalk != "") {
            displayBotSpeak(botTalk);
        }
    }

    //  TODO(2) Get userTalk, find the next node
    // OLD Post TO API from user input.
    private void getUserResponse() {
        SpeechToTextConvertor speechConverter = new SpeechToTextConvertor(this, new ConversionCompletion() {
            @Override
            public void onPartialResult(String partial) {
                userTextView.setText(partial);
            }

            @Override
            public void onCompletion(boolean success, String userTalk) {
                if (success) {
                    userTextView.setText(userTalk);
                    displayNextNode(userTalk);

//                    if (questionIndex < questions.length - 1) { // 3
//                        if (questionIndex == 0) {
//                            currentJob = result;
//                        }
//                        askNextQuestion("");
//                    } else if (questionIndex == questions.length - 1) { // 4
//                        postToAPI(ApiManager.Endpoint.AUTOMATION_PERCENTAGE, currentJob);
//                    } else if (questionIndex == questions.length && !result.contains("chat")) { // 5
//                        System.out.println("calling JOB recommender");
//                        if (jobs.isEmpty()) {
//                            postToAPI(ApiManager.Endpoint.JOB_RECOMMENDER, currentJob);
//                        } else {
//                            // No API needed, b/c we already searched the recs for that job!
//                            displayBotSpeak("");
//                        }
//                    } else { // 6+
//                        System.out.println("Question index @ for chatbox " + questionIndex);
//                        questionIndex = questions.length + 2;
//                        postToAPI(ApiManager.Endpoint.CHAT_BOT, result);
//                    }
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

	private void postToAPI(ApiManager.Endpoint apiType, String userInput) {
        // Send it via the apiManager
        ApiManager apiManager = new ApiManager();
        try {
            apiManager.sendPost(apiType, userInput, new ApiManager.DidFinishPostHandler(){
                @Override
                public void handle(boolean success, String response) {
                    if (success) {
                        final String finalResponse = response;

                        System.out.println("API Response" + finalResponse);

                        // Update question view
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO
//                                askNextQuestion(finalResponse);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Error with POST: " + e);
        }
    }

    void setupDialogTree() {
        ChoiceNode yes = new ChoiceNode(
                "Answered yes",
                null,
                new String[] {"yes", "yeah", "ok", "yas"},
                null);

        ChoiceNode no = new ChoiceNode(
                "Answered not-yes",
                null,
                new String[] {"*"}, // Match all keywords
                null);

        ChoiceNode booleanQuestion = new ChoiceNode(
                "Pick a boolean, yes or no?",
                new Node[] {yes, no},
                null);

        ChoiceNode educationQuestion = new ChoiceNode(
                "What is your education?",
                new Node[] { booleanQuestion },
                new NodeAction() {
                    @Override
                    public void userDidTalk(String userTalk) {
                        user.education = userTalk;
                    }
                });

        ChoiceNode jobQuestion = new ChoiceNode(
                "What is your job?",
                new Node[] {educationQuestion},
                new NodeAction() {
                    @Override
                    public void userDidTalk(String userTalk) {
                        user.currentJob = userTalk;
                    }
                });

        dialog = new DialogTree(jobQuestion);
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
