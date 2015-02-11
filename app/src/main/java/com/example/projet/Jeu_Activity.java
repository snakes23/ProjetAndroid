package com.example.projet;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;



public class Jeu_Activity extends Activity {
	 private JeuView mSView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		///////// Met le mode plein écran //////
				requestWindowFeature(Window.FEATURE_NO_TITLE); 
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
									 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.jeu_activity);
        ////////Force l'orientation portrait/////////
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mSView = (JeuView)findViewById(R.id.JeuView);
		mSView.setVisibility(View.VISIBLE); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // item selection//

	    switch (item.getItemId()) {
	    case  R.id.id_Vibration:
	    	mSView.Vibre=!mSView.Vibre;
	    	return true;
	    	
	    case R.id.id_SoundEffect:
	    	mSView.EffSound=!mSView.EffSound;
	    	return true;
	    	
	    case R.id.id_Solution:
	    	if(mSView.Solution>0){
	    			mSView.Solution--;
	    			mSView.GiveOneSoluce(3);
	    	}else{
	    		   Toast.makeText(getApplicationContext(), "Plus de solution disponible",Toast.LENGTH_LONG).show();
	    	}
	    	return true;
	    
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_jeu, menu);
		return true;
	}
		
	
	////////////////met a jour le menu/////////////////////
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		MenuItem ItemVibration = menu.findItem(R.id.id_Vibration);
		MenuItem ItemEffectSound = menu.findItem(R.id.id_SoundEffect);
		MenuItem ItemSolution = menu.findItem(R.id.id_Solution);
	///////////change le texte du menu vibreur//////
	if(mSView.Vibre)
		ItemVibration .setTitle("Vibreur ON");
	else 
		ItemVibration .setTitle("Vibreur OFF");
	
	///////change le texte du menu effets sonore////
	if(mSView.EffSound)
		ItemEffectSound .setTitle("Effets Sonores ON");
	else 
		ItemEffectSound .setTitle("Effets Sonores OFF");
	
	//////////change le texte du menu solution//////
	ItemSolution.setTitle("Solution : "+ mSView.Solution);
		
	
	return super.onPrepareOptionsMenu(menu);
	}
	
	//////////////gestion de la touch Back du telephone///
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
		////Met le jeu en pause////////
			mSView.pause=true;
			
			AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Jeu_Activity.this);
        	alertDialog1.setTitle("Exit");
        	alertDialog1.setMessage(Html.fromHtml("Si vous quittez la partie en cours, cette dernière sera sauvegardée ?"));
        	// Set the Icon for the Dialog
        	alertDialog1.setIcon(R.drawable.icn_game);

        	
        	alertDialog1.setNegativeButton("Sauvegarder", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int which) {
		    		   mSView.SaveGame();
		    		   Toast.makeText(getApplicationContext(), "Jeu Sauvegardé",Toast.LENGTH_LONG).show();
		    		   finish();
		    		   
		    	   }
		    	});
        	alertDialog1.setNeutralButton("recommencer", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int which) {
		    		   mSView.DeleteSave();
		    		   Intent intent = getIntent();
		    		   mSView.boucle=false;
		    		   finish();
		    		   startActivity(intent);
		    	   }
		    	});
        	
        	alertDialog1.setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int which) {
		    		////enlève la pause////////
		    			mSView.pause=false;
		    		   dialog.cancel();
		    	   }
		    	});
        	AlertDialog alertDialogCreate = alertDialog1.create();
        	alertDialogCreate.show();
			break;
		}
	    
	    return super.onKeyDown(keyCode, event);
	}

}
