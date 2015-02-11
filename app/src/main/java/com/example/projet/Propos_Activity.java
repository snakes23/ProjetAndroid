package com.example.projet;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Propos_Activity extends Activity {
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.propos_activity);
//////////Force l'orientation portrait/////////
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        
		final Button retour = (Button) findViewById(R.id.btBack);

        
        retour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	finish();
              
              }
            });
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }
 
}

