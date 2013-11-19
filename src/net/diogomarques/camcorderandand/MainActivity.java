package net.diogomarques.camcorderandand;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button mButtonStart, mButtonStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camcorder); 
        mButtonStart = (Button) findViewById(R.id.buttonStart);
        mButtonStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startRecordingService();
			}
		});
        mButtonStop = (Button) findViewById(R.id.buttonStop);
        mButtonStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopRecordingService();
			}
		});
    }
    
    private void startRecordingService() {
    	Intent intent = new Intent(this, RecordingService.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startService(intent);
    }
    
    private void stopRecordingService() {
    	stopService(new Intent(this, RecordingService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
