package com.example.projet;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Menu_Activity extends Activity {
	
	boolean ActifSound=false;
	private MediaPlayer music;///pour jouer la music de fond

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);
		
		/////Permet d'accéder aux boutons //////////////////
		final Button play = (Button) findViewById(R.id.btJouer);
		final Button soundEtat=(Button)findViewById(R.id.btSound);
		final Button myScore=(Button)findViewById(R.id.btScore);
		final Button exit = (Button) findViewById(R.id.btQuitter);
		
		music = MediaPlayer.create(getBaseContext(),R.raw.sound_main_cut );
		music.setLooping(true);//joue en boucle la musique
		
		//////////Force l'orientation portrait/////////
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		///////////////////Boutons//////////////////////////////////
        play.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	SharedPreferences pref = getBaseContext().getSharedPreferences("MyPref", Jeu_Activity.MODE_PRIVATE); 
        	if(pref.getString("key_Map", null)==null)
        		play.setText(R.string.Text_jouer);
        	else
        		play.setText(R.string.Text_reprendre);
        	
          Intent intent = new Intent(Menu_Activity.this,com.example.projet.Jeu_Activity.class);
          startActivity(intent);
          }
        });
       
        
        soundEtat.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	ActifSound=!ActifSound;
	        	if(ActifSound){
	        		music.start();
	        		soundEtat.setText(R.string.SoundOn);
	        	}else{ 
	        		music.pause();
	        		soundEtat.setText(R.string.SoundOff);
	        	}
	         
	          }
	        });
        
        
        myScore.setOnClickListener(new View.OnClickListener() {
	        @Override
	        
	        public void onClick(View v) {
	        	SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE); 
	        	String valueScore="";
	        	if(pref.getString("key_BestScore", null)!=null)
	        		valueScore=pref.getString("key_BestScore", null);
	        	else
	        		valueScore="0";
	        	
	        	
	        	AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Menu_Activity.this);
	        	alertDialog1.setTitle("Score");
	        	alertDialog1.setMessage(Html.fromHtml("<font size=7><b>Your Best Score </font></b>: "+
	        			"<b>"+valueScore +"<b></center>"));
	        	// Set the Icon for the Dialog
	        	alertDialog1.setIcon(R.drawable.icn_king);

	        	alertDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int which) {
			    		   dialog.cancel();
			    		   Toast.makeText(getApplicationContext(), "Essaie de battre ton Score",Toast.LENGTH_LONG).show();
			    	   }
			    	});
	        	AlertDialog alertDialogCreate = alertDialog1.create();
	        	alertDialogCreate.show();
	         
	          }
	        });
        
        
        exit.setOnClickListener(new View.OnClickListener() {
			@Override
	        public void onClick(View v) {
	        	AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Menu_Activity.this);
	        	alertDialog1.setTitle("Exit");
	        	alertDialog1.setMessage(Html.fromHtml("Souhaitez-Vous vraiment quitter le jeu ? "));
	        	// Set the Icon for the Dialog
	        	alertDialog1.setIcon(R.drawable.icn_exit);

	        	alertDialog1.setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int which) {
			    		   dialog.cancel();
			    	   }
			    	});
	        	
	        	alertDialog1.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int which) {
			    		   Detruit();

			    	   }
			    	});
	        	AlertDialog alertDialogCreate = alertDialog1.create();
	        	alertDialogCreate.show();
	          }
	        });     
	}
	//////////////gestion de la touch Back du telephone///
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
			case KeyEvent.KEYCODE_BACK:
				AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Menu_Activity.this);
	        	alertDialog1.setTitle("Exit");
	        	alertDialog1.setMessage(Html.fromHtml("Souhaitez-Vous vraiment quitter le jeu ? "));
	        	// Set the Icon for the Dialog
	        	alertDialog1.setIcon(R.drawable.icn_exit);

	        	alertDialog1.setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int which) {
			    		   dialog.cancel();
			    		   Toast.makeText(getApplicationContext(), "You chose a negative answer",Toast.LENGTH_LONG).show();
			    	   }
			    	});
	        	alertDialog1.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int which) {
			    		   Detruit();
			    	   }
			    	});
	        	AlertDialog alertDialogCreate = alertDialog1.create();
	        	alertDialogCreate.show();
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
///////////Lorsque l'on quitte le jeu/////////
	public void Detruit() {
		if(ActifSound)
			///on stop la music///
			music.stop();
	    finish();
	    super.onDestroy();
		 }

	/////////////////////////Menu/////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater myMenu = getMenuInflater();
		myMenu.inflate(R.menu.menu_principal, menu);
		return true;
	}
	
	
	 ///////////////////////////DIALOG ALERT & Change Activity via Menu /////////////////////////////

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.id_Propos:
	    	 
	              Intent propos = new Intent(Menu_Activity.this,com.example.projet.Propos_Activity.class);
	            	startActivity(propos);
	              
	    	return true;
	    	
	    	
	    case R.id.id_Aide:
	    
	    	AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Menu_Activity.this);
	    	alertDialog1.setTitle("Réglement du Jeu");
	    	alertDialog1.setMessage(Html.fromHtml("<b>Le But du Jeu est d'éliminer un maximum de virus colorés!</b><br><br>" +
	    			"&nbsp;&nbsp;&nbsp; Pour cela, vous devez cliquer sur les cases vides (blanches ou grises) qui se trouvent à l'intersection verticale et/ou horizontale des virus de même couleurs avant la fin du temps impartis<br><br>" +
	    			"<i>-paire = 20 pts</i> <br>"+
	    			"<i>-triplet = 60 pts </i><br>"+
	    			"<i>-quadruplet = 120 pts</i> <br>"+
	    			"<br>Le jeu s'arrêt si il n'y a plus de coup jouable, si le plateau est vide ou si le temps est écoulé."));
        	// Set the Icon for the Dialog
        	alertDialog1.setIcon(R.drawable.icn_aide);

        	alertDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int which) {
		    		   dialog.cancel();
		    	   }
		    	});
        	alertDialog1.show();
	    	return true;
	    
	    	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	

}
