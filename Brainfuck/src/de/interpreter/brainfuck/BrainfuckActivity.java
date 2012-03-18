package de.interpreter.brainfuck;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author Daniel Ölschlegel
 * @license 2-BSDL
 * @date 2012/03
 * @version 0.2
 */

public class BrainfuckActivity extends Activity implements OnInitListener, OnClickListener {
	private TextToSpeech tts;
	private BfInterpreter bf;
	   
	
	public final Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			((EditText) findViewById(R.id.outputfield)).append((String) msg.obj);
		}
	};
     
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tts = new TextToSpeech(this, this);
        
        // disable software keyboard
        ((EditText) findViewById(R.id.outputfield)).setInputType(InputType.TYPE_NULL);
        ((EditText) findViewById(R.id.outputfield)).setSingleLine(false);
        ((EditText) findViewById(R.id.inputfield)).setInputType(InputType.TYPE_NULL);
        ((EditText) findViewById(R.id.inputfield)).setSingleLine(false);
        
        for(int i = 1; i <= 11; ++i) {
        	int resID = this.getResources().getIdentifier("button" + i, "id", "de.interpreter.brainfuck");
            findViewById(resID).setOnClickListener(this);
        }
    }
    
    public void onInit(int status) {
    	Calendar cal = Calendar.getInstance();
    	if (cal.get(Calendar.DAY_OF_MONTH) == 4 && cal.get(Calendar.MONTH) == 0) {
	    	tts.setLanguage(Locale.GERMAN);
	    	tts.speak("programmiert von Daniel Ölschlegel", TextToSpeech.QUEUE_FLUSH, null);
	    }
    }
    
    public void onClick(View view) {
		String caption = ((Button) view).getText().toString();
		EditText inputField = (EditText) findViewById(R.id.inputfield);
		EditText outputField = (EditText) findViewById(R.id.outputfield);
		
		char buttonChar = caption.charAt(0);
		switch(buttonChar) {
		// special commands ignore non english commands
		case '<': case '>': case '+': case '-': 
		case '.': case ',': case '[': case ']':
			String text = inputField.getText().toString();
			int start =  inputField.getSelectionStart();
			inputField.setText(text.substring(0, start) + buttonChar + text.substring(start));
			inputField.setSelection(start + 1);
			break;
		case 'D': 
			inputField.setText(""); 
			break;
		case 'r': 
			bf = new BfInterpreter(inputField.getText().toString(), 0, this);
			bf.setHandler(myHandler);
			bf.setPriority(Thread.MAX_PRIORITY);
			bf.start();
			break;
		case 'c': 
			outputField.setText(""); 
			break;
		default:
			break;
		}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String input = data.getStringExtra("input");
        if (resultCode == BrainfuckActivity.RESULT_OK && input.length() > 0)
        	BfInterpreter.inputChar = input.charAt(0);
        else
        	BfInterpreter.inputChar = '\0';
        bf.blocking = false;
    }

}