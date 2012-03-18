package de.interpreter.brainfuck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class InputDialog extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);     
        
        findViewById(R.id.okButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this); 
    }
	
    
    
	public void onClick(View v) {
		EditText inputField = (EditText)findViewById(R.id.dialogInput);
		Intent extras = new Intent();
		extras.putExtra("input", inputField.getText().toString()); 
		setResult(v.getId() == R.id.okButton? Activity.RESULT_OK : Activity.RESULT_CANCELED, extras);
		finish();
	}


}
