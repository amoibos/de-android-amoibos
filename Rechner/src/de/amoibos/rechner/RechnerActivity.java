package de.amoibos.rechner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RechnerActivity extends Activity implements OnClickListener {
    EditText display;
    char opCode = ' ';
    String firstDisplay;
    boolean nextNumber = false;  
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        display = (EditText) findViewById(R.id.editText1);
        for(int i=1; i<21; ++i) {
        	int resID = this.getResources().getIdentifier("button"+i, "id", "de.amoibos.rechner");
            ((Button)findViewById(resID)).setOnClickListener(this);
        }
    }

	@Override
	public void onClick(View arg0) {
		String caption = ((Button) arg0).getText().toString();
		String text = display.getText().toString();
		switch(caption.charAt(0)) {
		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9': 	
			if(text.equals("0") || nextNumber) {
				display.setText(caption);
				nextNumber = false;
			} else
				display.setText(text + caption);
			break;
		case '.': 	
			if(!text.contains(".") && text.length()>0 && !nextNumber) 
				display.setText(text + caption);
			break;
		case '+': case '-': case '*': case '/': case '^': 
			if(text.equals(""))
				break;
			if(caption.equals("+/-")) { 
				if(!text.equals("0"))
					display.setText(""+(Float.parseFloat(text) * -1));
			} else {
				nextNumber = true;
				if(firstDisplay != null) {
					String res = evaluate(firstDisplay, text, caption.charAt(0));
					if(!res.equals(""))
						firstDisplay = res;
				} else
					firstDisplay = text;
				opCode = caption.charAt(0);
			}	
			break;
		case '<':
			if(text.length() == 1)
				display.setText("0");
			else if(!text.equals(""))
				display.setText(text.substring(0, text.length()-1));
			break;
		case 'C':
			nextNumber = false;
			firstDisplay = null;
			opCode = ' ';
			display.setText("0");
			break;
		case '=':	
			if(text.equals(""))
				break;
			String res = evaluate(firstDisplay, text, opCode);
			if(!res.equals("")) {
				if(res.equals("Infinity"))
					res = "0";
				display.setText(res);
				firstDisplay = res;
			}
			break;
		}
	}
	
	String evaluate(String s1, String s2, char opCode) {
		switch(opCode){
		case '+':
			return ""+(Float.parseFloat(s1) + Float.parseFloat(s2));
		case '-':
			return ""+(Float.parseFloat(s1) - Float.parseFloat(s2));
		case '*':
			return ""+(Float.parseFloat(s1) * Float.parseFloat(s2));	
		case '/':
			if(Float.parseFloat(s2)==0)
				break;
			return ""+(Float.parseFloat(s1) / Float.parseFloat(s2));
		case '^':
			return ""+(float)(Math.pow(Float.parseFloat(s1), Float.parseFloat(s2)));
		}
		return "";
	}
}