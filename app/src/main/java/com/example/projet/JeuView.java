package com.example.projet;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class JeuView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

	Jeu_Activity End;
	
	/////Pour les pistes audio////
	private MediaPlayer good_beat;///pour jouer les effets sonores
	
	/////pour L'IA//////
	//////voir si il reste au moins un coup jouable
	boolean decision=false;
	//////donne une solution au joueur////
	boolean soluce=false;
	
	
	////temps //////////
	long beginTime;
	long pastTime;
	long diffTps;

	
	int trueTime;
	int tmpTime;
	long resume=0;
	long resume2=0;

	String Tps="";
	
	boolean pause=false;
	
	boolean Malus;
	 //////case vide ou bien supprimée///
	 private Bitmap caseVide;
	 private Bitmap caseSuppr;
	 
	 //////case de couleur////////////
	 private Bitmap Cgreen;
	 private Bitmap Cred;
	 private Bitmap Cblue;
	 private Bitmap Cyellow;
	 private Bitmap Cpurple;
	 private Bitmap Ccyan;
	 private Bitmap Corange;
	 
	 /////modifier Taille image/////
	private Bitmap scaledTmp;
	
	private Bitmap FondHaut;

	///////boolean vibre et sound effects//////
	boolean Vibre=true;
	 boolean EffSound=true;
	 int Solution=3;
	 
	 int	mapTopAncre;
	 
	 
	 private Resources colorMatchRes;
	
	 private Context Fenetrecontext;

	 Random Case_rand= new Random();
	 
	 static final int    CST_vide      = 0;
	 static final int    CST_suppr      = -1;


	 static final int    mapHeight   = 14;//lignes
	 static final int    mapWidth    = 10;//colonnes
	 
	 /////espace du haut/////
	 int Xscale;
	 int Yscale;
	 int espace;
	 int topTime;
	 ////ancrage du text Score/////
	 int coordScore;
	 Integer Score;
	 String YourScore="";
	 
	 ///Vibreur///////////
	 Vibrator vibreur; 
	 
	 /////////Fin de jeu///////////////////
	 boolean Terminated=false;
	 String messageFin;
	 

	 private int gain[]={20,60,120};
	 public      int[][] map;
	 
	 Canvas c = null;
	 public boolean 	boucle= true;
     private Thread  cv_thread;        
	 SurfaceHolder 		holder;
	 
     private Paint paint;

	 
	public JeuView(Context context, AttributeSet attrs) {
		super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);        
        Fenetrecontext= context;
        colorMatchRes 	= Fenetrecontext.getResources();        
        
        good_beat = MediaPlayer.create(getContext(),R.raw.beep_good);
    	loadimages(colorMatchRes);
    	initparameters();

        cv_thread   = new Thread(this);
        setFocusable(true);
	}

	
	
	private void loadimages(Resources 	res) {
		Cgreen=BitmapFactory.decodeResource(res, R.drawable.virus1);
		Cred=BitmapFactory.decodeResource(res, R.drawable.virus2);
		Cblue=BitmapFactory.decodeResource(res, R.drawable.virus3);
		Cyellow=BitmapFactory.decodeResource(res, R.drawable.virus4);
		Cpurple=BitmapFactory.decodeResource(res, R.drawable.virus5);
		Ccyan=BitmapFactory.decodeResource(res, R.drawable.virus6);
		Corange=BitmapFactory.decodeResource(res, R.drawable.virus7);
		caseVide=BitmapFactory.decodeResource(res, R.drawable.color_grey);
		caseSuppr=BitmapFactory.decodeResource(res, R.drawable.color_white);
		FondHaut=BitmapFactory.decodeResource(res, R.drawable.heart_monitor);
    }    
	
	
	 private void loadlevel() {
	    	for (int i=0; i< mapHeight; i++) 
	            for (int j=0; j< mapWidth; j++) 
	            	map[i][j]=Case_rand.nextInt(8);
	    }    
	
	 public void initparameters() {
		 paint = new Paint();
	    	
		 paint.setColor(Color.RED); 
		 paint.setTextSize(40); 
		
		 map = new int[mapHeight][mapWidth];
		 
		 //Si aucune sauvegarde on charge un nouveau Level Random, initialise Score et le temps//
		 if(GetSaveExist()){
		    	GetSave();
		 }else{
			 loadlevel();
			 Score=0;
			 tmpTime=240;}
		 
		 //initialise le temps de départ de l'application
		 beginTime=System.currentTimeMillis();
		 Malus=false;
		
		 ///espace pour placer le Score,Timer et Bannière du haut////
		 topTime=70;
		 espace=getHeight()-topTime;
		 mapTopAncre= (getHeight() - espace);
		 ///new largeur et hauteur des images
		 Xscale=getWidth()/(mapWidth);
		 Yscale=(espace/(mapHeight));
	        
	    ///coordonnée X du texte qui affiche le score////
	        coordScore=getWidth()-260;
		////////////////vibration du mobile/////////////		
	        vibreur = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
	      
	    	if ((cv_thread!=null) && (!cv_thread.isAlive())) {        	
	        	cv_thread.start();
	        }
	    }
	 
	 private void paintMap(Canvas canvas) {
		 Bitmap tmp = null;
		 Paint transparent = new Paint();
   		 transparent.setAlpha(255);

	    	for (int i=0; i<mapHeight ; i++) 
	            for (int j=0; j< mapWidth; j++) {
	                switch (map[i][j]) {
	                    case 1:
	                    	tmp=Cgreen;
	                    	break;                    
	                    case 2:
	                    	tmp=Cred;
	                        break;
	                    case 3:
	                    	tmp=Cblue;
	                    	break;   	
	                    case 4:
	                    	tmp=Cyellow;
	                    	break; 
	                    case 5:
	                    	tmp=Cpurple;
	                    	break;
	                    case 6:
	                    	tmp=Ccyan;
	                    	break;
	                    case 7:
	                    	tmp=Corange;
	                    	break;
	                    case CST_vide:
	                    	tmp=caseVide;
	                        break;
	                    default:
	                        tmp=caseSuppr;
	                        break;
	                     
	                }
	                ////grossi les images pour occuper la taille maxi de l'écran disponible///
	               scaledTmp = Bitmap.createScaledBitmap(tmp,Xscale, Yscale, true);
	               canvas.drawBitmap(scaledTmp, j*Xscale, i*Yscale+mapTopAncre,transparent);
	            }
	    }
	
	
	 
	 private void paintTimer_Score(Canvas canvas){
		
		 ////dessine la banière du haut///////
		 Paint transparence = new Paint();
   		 transparence.setAlpha(255);
		 scaledTmp = Bitmap.createScaledBitmap(FondHaut,getWidth(), topTime, true);
         canvas.drawBitmap(scaledTmp,0,0,transparence);
         
         
		 ////temps passé en millisecondes//////////////////
         if(pause==false){
        	
        	 pastTime=System.currentTimeMillis()-resume;
        	 diffTps=(pastTime-beginTime);
        	 ///convertis en secondes/////
        	 diffTps/=1000;
         
        	 //le temps est egale au temps de départ + le diffTime (car diffTime  negatif)////
        	 trueTime= (int)(tmpTime-diffTps);
        	 resume2=pastTime;

        	 ///malus de temps ////
        	 if(Malus){
        		 tmpTime-=5;
        		 Malus=false;}        		
         }else{
        	 //sauvegarde le dernier temps passé pour remettre après la pause
        	 resume=System.currentTimeMillis()-resume2;
         }
         
      	if(trueTime<=0){
        	 canvas.drawText( "Fin de jeu ",5,40, paint);
        	
        	 DeleteSave();
        	 messageFin="Fin du temps";
        	 Terminated=true;
      	}else{	
     	Tps=Long.toString(trueTime);
     	canvas.drawText( "Bpm: "+Tps+"''",5,40, paint);
      	}

		 ////dessine le Score///////
         YourScore=Score.toString();
 		   canvas.drawText("Score: "+YourScore,coordScore,40, paint);
 		   }
	 
	@Override
	public void run() {
		
		Canvas c = null;
        while (boucle) {
            try {
            	 
                Thread.sleep(40);
                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                	if (c != null) {
                		holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
            }
        }		
	}
	
    private void nDraw(Canvas canvas) {
		canvas.drawRGB(0,0,0);
    	paintMap(canvas);
    	paintTimer_Score(canvas);   	 
    }
    
    
    
    /////////////////GESTION SAUVEGARDE/////////////////////
    public boolean GetSaveExist(){
    	SharedPreferences pref = getContext().getSharedPreferences("MyPref", Jeu_Activity.MODE_PRIVATE); 
    	if(pref.getString("key_Map", null)!=null &&pref.getString("key_Score", null)!=null && pref.getString("key_Time", null)!=null)
    		return true;
    	else
		return false; 
    }
    
    
    
    public void SaveBestScore(){
    	int newScore=0,oldScore=0;
    	
    	SharedPreferences pref = getContext().getSharedPreferences("MyPref", Jeu_Activity.MODE_PRIVATE); 
    	SharedPreferences.Editor editor = pref.edit();
    	
    	newScore=Integer.parseInt(YourScore);
    	
    	if(pref.getString("key_BestScore", null)!=null)
    		oldScore=Integer.parseInt(pref.getString("key_BestScore", null)); 
    	else
    		oldScore=0;
    	
    	if(newScore>=oldScore)
    		editor.putString("key_BestScore", YourScore);
    	
    	editor.commit(); // commit changes
    }
    
    public void SaveGame(){
    	
    	
    	//////////Save la Map //////////////////////////
    	SharedPreferences pref = getContext().getSharedPreferences("MyPref", Jeu_Activity.MODE_PRIVATE); 
    	SharedPreferences.Editor editor = pref.edit();
    	
    	StringBuilder stringMap = new StringBuilder();
    	for (int lig=0; lig< mapHeight; lig++) 
	            for (int col=0; col< mapWidth; col++)
	            	stringMap.append(map[lig][col]).append(",");
	              	
    	editor.putString("key_Map", stringMap.toString()); 
    	
    	/////////////Save le temps restant////////////////
    	String time_restant="";
    	time_restant=Integer.toString(trueTime);
    	editor.putString("key_Time", time_restant);
    	
    	
    	////////////Save le Score//////////////////////
    	
		editor.putString("key_Score", YourScore);
		
		SaveBestScore();
    	editor.commit(); // commit changes	

    }

    ///////////////Demande la Save (Map,Score & Time) pour reprendre le jeu ou on on était//////////
    public void GetSave(){
    	SharedPreferences pref = getContext().getSharedPreferences("MyPref", Jeu_Activity.MODE_PRIVATE); 

    	///////////recupere la map//////////////////
    	String mapSaved = pref.getString("key_Map", null);
    	StringTokenizer stringMap = new StringTokenizer(mapSaved, ",");

    	for (int lig=0; lig< mapHeight; lig++) {
            for (int col=0; col< mapWidth; col++){
            	map[lig][col] = Integer.parseInt(stringMap.nextToken());
            }
    	}
    	////////////recupere le score///////////
    	String scoreSaved = pref.getString("key_Score", null);
    	Score=Integer.parseInt(scoreSaved);
    	
    	////////////recupere le temps restant///////////
    	String timeSaved = pref.getString("key_Time", null);
    	tmpTime=Integer.parseInt(timeSaved);
    	
    }
    
 
    
    public void DeleteSave(){
    	SharedPreferences pref = getContext().getSharedPreferences("MyPref", Jeu_Activity.MODE_PRIVATE);
        Editor editor = pref.edit();
        
        editor.remove("key_Time"); // upprime le temps sauvegardé
        editor.remove("key_Map"); // supprime la map sauvegardée
        editor.remove("key_Score"); // supprime le score sauvegardé
         
        // Save the changes in SharedPreferences
        editor.commit(); // commit changes
    }
    
    
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		initparameters();

	}
	
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	}
	
	public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent object holds X-Y values
		float CoordX=0;
		float CoordXtmp=0;
		
		float CoordY=0;
		float CoordYtmp=0;
		
		int L = 0,C=0;
		
		
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            
        	if(Terminated)
        		FinJeu();
        	
        	
        	/////////recupère les coordonnées du Tactile lorsque l'on appuis////
        	CoordXtmp=event.getX();
        	CoordYtmp=event.getY();
        	
        	///pour rectifier le calcul par rapport au centrage///
        	CoordYtmp-=mapTopAncre;
        	

        	/////////////Calcul /////////////////////////////
        	CoordX=CoordXtmp/(Xscale*mapWidth)*mapWidth;
        	CoordY=CoordYtmp/(Yscale*mapHeight)*mapHeight;
        	
        	
        	C=Math.round(CoordX);//cast float en entier
        	C-=1;//recule d'une colonne pour retrouver les coordonnées du tableau
        	
        	L=Math.round(CoordY);
        	L-=1;
      //ne traite que les coordonnées qui ne depasse pas de la Map et les cases Vides si il y a pas de pause
        	
        	if(C>=0 && C<mapWidth && L>=0 && L<mapHeight && !pause  && (map[L][C]==CST_suppr|| map[L][C]==CST_vide)){
        		if(EffSound)
        			good_beat.start();
        		recupColor(L,C,1);
        		
        		///Verifie que toutes les cases ne sont pas vide////
        		if(VerifVide()==true){
        			 DeleteSave();
        			 messageFin="Plus aucune case couleurs";
        			 Terminated=true;
        			 
        		}
        		
        		////Verifie qu'il reste des coups jouables ////
        		VerifGame(2);	
        		
        	}//si on a cliqué un virus donc une mauvaise case
        	else if(C>=0 && C<mapWidth && L>=0 && L<mapHeight && !pause  && (map[L][C]!=CST_suppr&& map[L][C]!=CST_vide)){
        		if(Vibre)
            		vibreur.vibrate(250);
        		Malus=true;
        	}
        }
        return super.onTouchEvent(event);
    }

	
	 
	 //////////////Verifie si il reste des coups jouables///////////
	 public void VerifGame(int identifiant){
 		decision=false;
		 for (int lig=0; lig< mapHeight; lig++) {
	            for (int col=0; col< mapWidth; col++){
	            	if(map[lig][col]==CST_vide ||map[lig][col]==CST_suppr ){
	            		recupColor(lig,col,identifiant);
	            		if(decision==true ){
	            			break;
	            		}
	            	}
	            }
	            if(decision==true)
	            	break;
	            }
		 if(decision==false){
			 DeleteSave();
			 messageFin="Plus aucun coups jouable";
			 Terminated=true;
		 }
	 }
	 /////////Verifie qu'il reste des cases sur le plateau de jeu///////
	 public boolean  VerifVide(){
		 int totale=mapHeight*mapWidth;
		 int test=0;
		 for (int lig=0; lig< mapHeight; lig++) 
	            for (int col=0; col< mapWidth; col++)
	            	if(map[lig][col]==CST_vide ||map[lig][col]==CST_suppr )
	            		test+=1;
	            		
	       if(test==totale)
	    	   return true;
	       else 
	    	   return false;
	 }
	 ////////////////Solution Donne une aide au joueur///////////////
	 public void GiveOneSoluce(int identifiant){
		 soluce=false;
		 for (int lig=0; lig< mapHeight; lig++) {
	            for (int col=0; col< mapWidth; col++){
	            	if(map[lig][col]==CST_vide ||map[lig][col]==CST_suppr ){
	            		recupColor(lig,col,identifiant);
	            		if(soluce==true)
		            		break;
	            		}
	          }
	            if(soluce==true)
            		break;
		 }
		 
	 }
	 
	
	 
	private void recupColor(int lig, int col,int identifiant) {
				
		int right =0,left=0,up=0,down=0;
		int[][] caseIntersect = new int[4][3];

		for(int i=0;i<4;i++)
			for(int j=0;j<3;j++)
				caseIntersect[i][j]=-1;
		
		/////Parcours dans le Sens horaire/////////
		
		///regarde en haut////
			for(int l=lig;l>=0 && up==0;l--)
				if(map[l][col]!=CST_vide && map[l][col]!=CST_suppr){
					up=map[l][col];
					caseIntersect[0][0]=up;
					caseIntersect[0][1]=l;
					caseIntersect[0][2]=col;	
				}	

		///regarde a droite//
		for(int c=col;c<mapWidth && right==0;c++)
			if(map[lig][c]!=CST_vide && map[lig][c]!=CST_suppr){
				right=map[lig][c];
				caseIntersect[1][0]=right;
				caseIntersect[1][1]=lig;
				caseIntersect[1][2]=c;
				} 

		 /////regarde en bas////
		for(int l=lig;l<mapHeight && down==0;l++)
				if(map[l][col]!=CST_vide && map[l][col]!=CST_suppr){
					down=map[l][col];					
					caseIntersect[2][0]=down;
					caseIntersect[2][1]=l;
					caseIntersect[2][2]=col;
				}

		///regarde a gauche//
		for(int c=col;c>=0 && left==0;c--)
			if(map[lig][c]!=CST_vide && map[lig][c]!=CST_suppr){
				left=map[lig][c];				
				caseIntersect[3][0]=left;
				caseIntersect[3][1]=lig;
				caseIntersect[3][2]=c;
			}
		    
		detectionColor(caseIntersect,identifiant);
	}


	

public void detectionColor(int tab[][],int identifiant) {
	
	boolean double_paire=false;
	boolean triplet=false;
	boolean quadruplet=false;
	
	
	
	/////compteur pour reconnaitre les gains ///
	ArrayList<Integer> identique = new ArrayList<Integer>();
	ArrayList<Integer> identique2 = new ArrayList<Integer>();
	
	///parcours le tableau pour stocker les coordonnées de cases de couleur identique
		for(int i=0;i<3;i++){
			for(int j=i+1;j<=3;j++){
				if(tab[i][0]>-1){
				/////////double paire de couleur différente////
				if(tab[i][0]==tab[j][0] && identique.size()==2 && i!=identique.get(0)){
					identique2.add(i);
					identique2.add(j);
					double_paire=true;
					break;
					}
				/////////////triplet ou quadruplet////////////////////
				else if(tab[i][0]==tab[j][0] && identique.size()>=2 && i==identique.get(0)){
					identique.add(j);
					triplet=true;
					if(identique.size()==4)
						quadruplet=true;					
				}////on ajoute des que deux id sont identiques et différente de -1
				else if(tab[i][0]==tab[j][0]){
					identique.add(i);
					identique.add(j);
					}
				}else{
					/////on break car la case i de référence ==-1
					break;
				}
			}		
			/////si triplet (vus que i==1 now) ou quadruplet detecté alors break
			if(triplet || quadruplet)
				break;
				
			if(double_paire)
				break;
		}
		
		/////Si clique bonne case mais aucun virus éliminé ( aucun coup jouable )///
		if(identique.size()==0 && identifiant==1){
			if(Vibre)
        		vibreur.vibrate(250);
    		Malus=true;
			
		}
		//////////efface les couleurs si au moins une paire est detectée////////
		////identifiant==1 pour identifier le joueur qui touche l'ecran//
		else if(identique.size()>=2 && identifiant ==1)
			removeColor(tab,identique,identique2);
		
		/////identifiant==2 pour la verification qu'il reste des coups jouable
		else if(identique.size()>=2 && identifiant ==2)
			decision=true;
		////identifiant==3 pour les solutions d'aide du menu//////
		else if (identique.size()>=2 && identifiant ==3){
			soluce=true;
			removeColor(tab,identique,identique2);	
		}
}


private void removeColor(int tab[][],ArrayList<Integer> identique, ArrayList<Integer> identique2){
	int taille=identique.size();
	int taille2=identique2.size();	
	int position=0;
	int i=0;
	///////////////Augmente le score en fonction d'une paire,triplet et quadruplet//////
	if(identique2.size()==0){
		switch (identique.size()){
			case 2:////paire
				Score+=gain[0];
				break;
			
			case 3:///triplet
				Score+=gain[1];
				break;
				
			case 4://///quadruplet
				Score+=gain[2];
				break;
			default://///aucune possibilité de coup
				
				break;
				}
		}else{
		Score+=gain[2];
		}

	do{		
		position=identique.get(i);
		map[tab[position] [1]] [tab[position] [2]]=CST_suppr;
		
		if(taille2>0){
			position=identique2.get(i);
			map[tab[position] [1]] [tab[position] [2]]=CST_suppr;
			}
		i++;}while(i<taille);
}

/////////Definit quand le jeu est fini///
public void FinJeu(){	

		boucle=false;		
		AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Fenetrecontext);
		alertDialog1.setTitle("Jeu Fini: "+messageFin);
		alertDialog1.setMessage("Votre Score Final est de "+YourScore);
		alertDialog1.setCancelable(true);
		alertDialog1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				SaveBestScore();
				System.exit(0);
				
			}
		} );
		AlertDialog alert = alertDialog1.create();
		alert.show();

}


}