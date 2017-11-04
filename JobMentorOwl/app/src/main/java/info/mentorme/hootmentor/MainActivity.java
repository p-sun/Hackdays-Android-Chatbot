package info.mentorme.hootmentor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import info.mentorme.hootmentor.Dialog.*;
import info.mentorme.hootmentor.Models.User;
import info.mentorme.hootmentor.Networking.ApiManager;
import info.mentorme.hootmentor.SpeechConverter.SpeechToTextHandler;
import info.mentorme.hootmentor.SpeechConverter.SpeechToTextConvertor;
import info.mentorme.hootmentor.SpeechConverter.TextToSpeechConvertor;
import info.mentorme.hootmentor.SpeechConverter.TextToSpeechHandler;
import info.mentorme.hootmentor.Support.*;

import android.view.animation.AlphaAnimation;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class MainActivity extends Activity {

    static User user = new User();
    DialogTree dialog;
    TextToSpeechConvertor textToSpeech;
    SpeechToTextConvertor speechToText;

	// Called when an API response returns with a botResponse
	private void askNextQuestion(String botResponse) {
//        System.out.println("Question index (ask next Q) " + questionIndex);

//        if (questionIndex != questions.length) {
//            questionIndex++;
//            // Don't increment
//        }

//        displayBotSpeak(botResponse);
	}

	private TextView botTextView;
	private TextView userTextView;

	private ImageButton owlButton;
    private ImageButton restartButton;

    private ImageView owlUserIsSpeakingView;

    boolean isOwlMouthOpen = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		botTextView = (TextView) findViewById(R.id.botTextView);
		userTextView = (TextView) findViewById(R.id.userTextView);
		owlButton = (ImageButton) findViewById(R.id.owlButton);
        restartButton = (ImageButton) findViewById(R.id.restartButton);
        owlUserIsSpeakingView = (ImageView) findViewById(R.id.owlUserIsSpeakingImage);

		// hide the action bar
		getActionBar().hide();

		owlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    textToSpeech.stop();

                if (dialog.isAtBeginning()) {
                    displayNextNode("");
                } else {
                    speechToText.startListening();
                }
			}
		});

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botTextView.setText("");
                userTextView.setText("");

                textToSpeech.stop();

                // Reset & ask the first question!
                dialog.reset();
                recommendationIndex = 0;
                jobs = new ArrayList<JobRecommendation>();
            }
        });

//        dialog = DialogTreeBuilder.maxineTree();
        dialog = DialogTreeBuilder.apiConnectedTree();

        Permission.requestRecordAudioPermission(getApplicationContext(), this);
        setupTextToSpeech();
        setupSpeechToText();
	}

    ArrayList<JobRecommendation> jobs = new ArrayList<JobRecommendation>();
    int recommendationIndex = 0;

    // 1) Bot displays text, and speaks it.
    private void displayBotSpeak(String aBotTalk) {
        final double seconds = dialog.isAtBeginning() ? 0.0 : 1;
        final String botTalk = aBotTalk;

        // TODO Make Delay an Async Task
        // If we remove runOnUiThread, we'll get "Can't create handler inside thread that has not called Looper.prepare()"
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Delay.delay(seconds, new Delay.DelayCallback() {
                    @Override
                    public void afterDelay() {
                        // Set text
                        botTextView.setText(botTalk);
                        userTextView.setText("");

                        // Speak question
                        textToSpeech.speakOut(botTalk);
                    }
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
//                        question, getApplicationContext(), new SpeechToTextHandler() {
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

        dialog.botTalk(userTalk, new BotTalkHandler() {
            @Override
            public void botDidTalk(String botTalk) {

                if (botTalk != null && botTalk != "") {
                    displayBotSpeak(botTalk);
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

    private void setupTextToSpeech() {
        textToSpeech = new TextToSpeechConvertor(
                getApplicationContext(), new TextToSpeechHandler() {

            @Override
            public void onCompletion(boolean success) {
                owlButton.setImageResource(R.drawable.owl_waiting);

                if (success) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            speechToText.startListening();
                        }
                    });
                }
            }

            @Override
            public void onStart() {
                owlButton.setImageResource(R.drawable.owl_talking);
            }

            @Override
            public void onRangeStart() {
                if (isOwlMouthOpen) {
                    owlButton.setImageResource(R.drawable.owl_waiting);
                } else {
                    owlButton.setImageResource(R.drawable.owl_talking);
                }
                isOwlMouthOpen = !isOwlMouthOpen;
            }
        });
    }

    private void setupSpeechToText() {
        speechToText = new SpeechToTextConvertor(this, new SpeechToTextHandler() {
            @Override
            public void onStart() {
                owlButton.setImageResource(R.drawable.owl_listening);
            }

            @Override
            public void onVolumeChanged(float volume) {
                int visibility = volume > 0 ? View.VISIBLE : View.INVISIBLE;
                owlUserIsSpeakingView.setVisibility(visibility);
            }

            @Override
            public void onPartialResult(String partial) {
                userTextView.setText(partial);
            }

            @Override
            public void onCompletion(boolean success, String userTalk) {
                owlButton.setImageResource(R.drawable.owl_waiting);

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
