package info.androidhive.speechtotext;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import info.androidhive.speechtotext.SpeechConverter.ConversionCompletion;
import info.androidhive.speechtotext.SpeechConverter.SpeechToTextConvertor;

public class MainActivity extends Activity {
	private String[] questions = {
			"What is your job?",
			"What is your level of education?",
			"How many years of experience do you have?"
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

	private void incrementQuestionIndex() {
		if (questionIndex >= questions.length) {
			return;
		}
		questionIndex++;
	}

	private TextView botTextView;
	private TextView userTextView;

	private ImageButton btnSpeak;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		botTextView = (TextView) findViewById(R.id.questionView);
		userTextView = (TextView) findViewById(R.id.answerView);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

		// hide the action bar
		getActionBar().hide();



		btnSpeak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Reset & ask the first question!
				botTextView.setText("");
				userTextView.setText("");
				questionIndex = 0;

				promptSpeechInput();
			}
		});
	}

	private SpeechRecognizer myRecognizer;
	Intent intent;



    private void promptSpeechInput() {
        SpeechToTextConvertor speechConverter = new SpeechToTextConvertor(this, new ConversionCompletion() {
            @Override
            public void onCompletion(boolean success, String result) {
                if (success) {

                    System.out.println("SUPER IMPORTANT success");
                    botTextView.setText(result);
                }
            }
        });
    }

	// Show google speech input dialog
//	private void promptSpeechInput3() {
//		// THIS IS NEW
//		myRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
//		SpeechListener mRecognitionListener = new SpeechListener();
//		myRecognizer.setRecognitionListener(mRecognitionListener);
//
//		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//
//		intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
//
//		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, currentQuestion());
//
//		// OLD
////		try {
////			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
////		} catch (ActivityNotFoundException a) {
////			Toast.makeText(getApplicationContext(),
////					getString(R.string.speech_not_supported),
////					Toast.LENGTH_SHORT).show();
////		}
//
//		// NEW
//		myRecognizer.startListening(intent);
//	}

	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("request code " + requestCode);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				// Get user response
				String userResponse = result.get(0);

				// Send it via the network
				Network network = new Network();
				try {
					network.sendPost(userResponse, new CompletionHandler(){
						@Override
						public void handle(boolean success, String response) {
							if (success) {

								final String finalResponse = response;
								// Update question view
								MainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										botTextView.setText(finalResponse);
										incrementQuestionIndex();
									}
								});

							}
						}
					});
				} catch (Exception e) {
					System.out.println("Error with POST: " + e);
				}

				// Display user response
				botTextView.setText(userResponse);
				System.out.println("User answer: " + userResponse);
			}
			break;
		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}




	// Recognizer Listener --------------
	public class SpeechListener implements RecognitionListener {
		public void onBufferReceived(byte[] buffer) {
		}
		public void onError(int error) {
			//if critical error then exit
        if(error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS){
//            Log.d(TAG, "client error");
            System.out.println("SpeechListener: Client error");

        }

		}
		public void onEvent(int eventType, Bundle params) {
//        Log.d(TAG, "onEvent");
		}
		public void onPartialResults(Bundle partialResults) {
			System.out.println("ON PARTIAL RESULTS");
		}
		public void onReadyForSpeech(Bundle params) {
//        Log.d(TAG, "on ready for speech");
		}

		public void onResults(Bundle results) {
			System.out.println("ON RESULTS " + results);

//        Log.d(TAG, "on results");
//        ArrayList<String> matches = null;
//        if(results != null){
//            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//            if(matches != null){
//                Log.d(TAG, "results are " + matches.toString());
//                final ArrayList<String> matchesStrings = matches;
//                processCommand(matchesStrings);
//                if(!killCommanded)
//                    mSpeechRecognizer.startListening(mSpeechIntent);
//                else
//                    finish();
//
//            }
//        }

		}
		public void onRmsChanged(float rmsdB) {

		}

		public void onBeginningOfSpeech() {
			System.out.println("speech beginning");
		}
		public void onEndOfSpeech() {
			System.out.println("speech done");
		}
	};

}
