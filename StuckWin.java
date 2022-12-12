import java.net.IDN;
import java.util.*;

import javax.print.DocFlavor.STRING;

public class StuckWin {

	static final Scanner input = new Scanner(System.in);
	
	public static final String RESET = "\033[0m"; // Text Reset
	public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
	public static final String RED_BACKGROUND = "\033[41m";    // RED
	public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
	public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
	public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
	public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
	public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
	public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

	public static final double ANGLE_STEP = 2 * Math.PI / 6;


	private static final double BOARD_SIZE = 7;

	// Initialisation de deux tableau contenant les lettre et numéro pour l'identification des cases.
	public static final char[] LISTLETTER = {'A','B','C','D','E','F','G'}; // egale à 7 (nombre de ligne state)
	public static final char[] LISTNUMBER= {'0','1','2','3','4','5','6','7'}; // egale à 8 (nombre de colone de state)

	// Initialisation de la taille des case et des pions de l'interface graphique
	public static final double radiusCase = 1.5;
	public static final double radiusPion = 0.8;

	// Liste d'énumération des différente erreur possible
	enum Result {
		OK
		, BAD_COLOR
		, DEST_NOT_FREE
		, EMPTY_SRC
		, TOO_FAR
		, EXT_BOARD
		, EXIT
		, BAD_SRC
	}

	// Défini la possiblité de tester un déplacement ou de l'éxécuter 
	enum ModeMvt {
		REAL
		, SIMU
	}

	// Tableau comportant le nombre de joueur et leur noms
	final char[] joueurs = { 'B', 'R' };

	final int SIZE = 8;

	// Constante qui défini un espace vide dans le tableau
	final char VIDE = '.';

	// 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas
	char[][] state = {
			{ '-', '-', '-', '-', 'R', 'R', 'R', 'R' },
			{ '-', '-', '-', '.', 'R', 'R', 'R', 'R' },
			{ '-', '-', '.', '.', '.', 'R', 'R', 'R' },
			{ '-', 'B', 'B', '.', '.', '.', 'R', 'R' },
			{ '-', 'B', 'B', 'B', '.', '.', '.', '-' },
			{ '-', 'B', 'B', 'B', 'B', '.', '-', '-' },
			{ '-', 'B', 'B', 'B', 'B', '-', '-', '-' },
	};	

	/**
	 * Déplace un pion ou simule son déplacement
	 * 
	 * @param couleur  couleur du pion à déplacer
	 * @param lcSource case source Lc
	 * @param lcDest   case destination Lc
	 * @param mode     ModeMVT.REAL/SIMU selon qu'on réalise effectivement le
	 *                 déplacement ou qu'on le simule seulement.
	 * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD,
	 *         EXIT} selon le déplacement
	 */
	Result deplace(char couleur, String lcSource, String lcDest, ModeMvt mode) {

		Result result = Result.OK;

		// Tableau qui stock les case possiblement jouable
		String[] possibleDests = new String[3];

		// Stock la valeur de la case de départ & d'arrivé : currentCase = R ; B ; - ; . 
		char currentCase;
		char destCase;

		int xSource=0, ySource=0;
		int xDest=0, yDest=0;

		// Vérification que le joueur entre 2 caractère et qu'il corresponde au élement des tableau
		if(lcSource.length() != 2 || lcDest.length() != 2)
		{
			result = Result.BAD_SRC;return result;
		}

		// Vérifié que la case de départ et la case d'arrive existe dans le tableau (A1) et non (/^)
		if(!(issetlc(lcSource)) || !(issetlc(lcDest)))
		{
			result = Result.BAD_SRC;return result;
		}

		// Initialisation de x-y Source et Dest  
		xSource = setCo('L', lcSource.charAt(0));
		ySource = setCo('N', lcSource.charAt(1));

		xDest = setCo('L', lcDest.charAt(0));
		yDest = setCo('N', lcDest.charAt(1));

		// Initialisation de currentCase & destCase
		currentCase = state[xSource][ySource];
		destCase = state[xDest][yDest];

		// Vérifie qu'il existe un pion dans la case
		if(emptylc(currentCase)){
			result = Result.EMPTY_SRC; return result;
		}
		
		// Vérification couleur du pion à déplace = couleur du joueur
		if(currentCase != couleur){
			result = Result.BAD_COLOR; return result;
		}

		// Vérifie que la case d'arriver est dans les bordure
		if(destCase == '-')
		{
			result = Result.EXT_BOARD;return result;
		}

		// Vérifie que la case d'arriver n'est pas occuper
		if(destCase != VIDE)
		{
			result = Result.DEST_NOT_FREE; return result;
		}

		// Verifie la distance entre la case de départ et la case d'arrivé
		possibleDests = possibleDests(couleur, xSource, ySource);

		if(!(possibleDests[0].equals(lcDest) 
		|| possibleDests[1].equals(lcDest) 
		|| possibleDests[2].equals(lcDest)))
		{
			result = Result.TOO_FAR;return result;
		}

		// Déplacement du pion
		if(mode == ModeMvt.REAL)
		{
			state[xDest][yDest] = state[xSource][ySource];
			state[xSource][ySource] = VIDE;
		}
		
		result = Result.OK; return result;
	}
	

	public int setCo(char List, char element){
		if(List == 'L'){
			for(int i =0; i < LISTLETTER.length; i++){
				if(LISTLETTER[i] == element){
					return i;
				}
			}
		}else if (List == 'N'){
			for(int i =0; i < LISTNUMBER.length; i++){
				if(LISTNUMBER[i] == element){
					return i;
				}
			}
		}
		return 0;
	}

		/**
	 * Verifie si la case que on souhaite jouer existe dans le tableau
	 * @param lcSource La case du tableau que on jeu jouer
	 * @return true si il existe un pion sinon false.
	 */
	public boolean issetlc(String lcSource){
		boolean issetLC = false;
		boolean issetL = false;
		boolean issetC = false;

		for(int i=0; i<LISTNUMBER.length;i++){
			if(LISTNUMBER[i] == lcSource.charAt(1)){
				issetC = true;
			}
		}

		for(int i=0; i <LISTLETTER.length; i++){
			if(LISTLETTER[i] == lcSource.charAt(0)){
				issetL = true;
			}
		}

		if(issetC && issetL){
			issetLC= true;
		}

		return issetLC;
	}

	/**
	 * Verifie si il existe un pion dans la case que on souhaite jouer
	 * à partir de la position de départ currentCase.
	 * @param currentCase La case du tableau que on jeu jouer
	 * @return true si il existe un pion sinon false.
	 */
	public boolean emptylc(char currentCase){
		boolean emptylc = true;
		for(int i=0; i < joueurs.length; i++){
			if(currentCase == joueurs[i]){
				emptylc = false;
			}
		}
		return emptylc;
	}

	/**
	 * Construit les trois chaînes représentant les positions accessibles
	 * à partir de la position de départ [idLettre][idCol].
	 * 
	 * @param couleur  couleur du pion à jouer
	 * @param idLettre id de la ligne du pion à jouer
	 * @param idCol    id de la colonne du pion à jouer
	 * @return tableau des trois positions jouables par le pion (redondance possible
	 *         sur les bords)
	 */
	String[] possibleDests(char couleur, int idLettre, int idCol) {
		String[] possibleDests = new String[3];

			if(couleur == joueurs[1]){
				// ----------------
				if(idLettre < LISTLETTER.length && idCol-1 < LISTNUMBER.length && idCol-1 >= 1 && idLettre >= 0){
					possibleDests[0] = ""+LISTLETTER[idLettre]+LISTNUMBER[idCol-1];
				}else{possibleDests[0]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}
				// ------------------
				if(idLettre+1 < LISTLETTER.length && idCol-1 < LISTNUMBER.length && idCol-1 >= 1 && idLettre+1 >= 0){
					possibleDests[1] = ""+LISTLETTER[idLettre+1]+LISTNUMBER[idCol-1];
				}else{possibleDests[1]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}
				//  -----------------
				if(idLettre+1 < LISTLETTER.length && idCol < LISTNUMBER.length && idCol >= 1 && idLettre+1 >= 0){					
					possibleDests[2] = ""+LISTLETTER[idLettre+1]+LISTNUMBER[idCol];
				}else{possibleDests[2]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}
				
			}
			else
			{
				if(idLettre-1 < LISTLETTER.length && idCol < LISTNUMBER.length && idLettre-1 >= 0 && idCol >= 0 ){
					possibleDests[0] = ""+LISTLETTER[idLettre-1]+LISTNUMBER[idCol];
				}else{possibleDests[0]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}

				if(idLettre-1 < LISTLETTER.length && idCol+1 < LISTNUMBER.length && idLettre-1 >= 0 && idCol+1 >= 0 ){
					possibleDests[1] = ""+LISTLETTER[idLettre-1]+LISTNUMBER[idCol+1];
				}else{possibleDests[1]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}

				if(idLettre < LISTLETTER.length && idCol+1 < LISTNUMBER.length && idLettre >= 0 && idCol+1 >= 0 ){
					possibleDests[2] = ""+LISTLETTER[idLettre]+LISTNUMBER[idCol+1];
				}else{possibleDests[2]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}

			}

		return possibleDests;
	}

	void graphiqueAffiche(){

		StdDraw.setXscale(-10.5, 10.5);
        StdDraw.setYscale(-10.5, 10.5);

		StdDraw.enableDoubleBuffering();

		double x= 0;
		double y= 0;

		x= -7;
		y= 3;
		for(int i = 4; i<8; i++, x+=1.5, y+=1){
			drawHega(x, y);
		}

		x= -7;
		y= 1;
		for(int i = 3; i<8; i++, x+=1.5, y+=1){
			drawHega(x, y);
			System.out.println(x);
			System.out.println(y);
		}

		x= -7;
		y= -1;
		for(int i = 2; i<8; i++, x+=1.5, y+=1){
			drawHega(x, y);

		}

		x= -7;
		y= -3;
		for(int i = 1; i<8; i++, x+=1.5, y+=1){
			drawHega(x, y);

		}

		x= -5.5;
		y= -4;
		for(int i = 1; i<8; i++, x+=1.5, y+=1){
			drawHega(x, y);

		}

		x= -4;
		y= -5;
		for(int i = 1; i<8; i++, x+=1.5, y+=1){
			drawHega(x, y);

		}



		// for(int i=0;i<state.length;i++)
		// {
		// 	toto = 0;
		// 	x = -7;
		// 	System.out.println(state.length -1);
		// 	for (int space = state.length -1; space >= i; space--) {
		// 		System.out.println(space);
		// 		toto++;
		// 	}
		// 	System.out.println(toto);
		// 	x = x + (1 * toto);

		// 	for (int j=0;j<state[i].length;j++){
		// 		y--;
		// 		if(state[i][j] != '-'){

		// 			StdDraw.setPenRadius(0.001);
		// 			StdDraw.setPenColor(StdDraw.BLACK);
		// 			StdDraw.circle(x, y, radiusCase);
		// 			drawHega(x, y);

		// 			switch(state[i][j]){
		// 				case 'R': 
		// 					StdDraw.setPenColor(StdDraw.RED);
		// 					StdDraw.filledCircle(x, y, radiusPion);
		// 					break;
		// 				case 'B':
		// 					StdDraw.setPenColor(StdDraw.BLUE);
		// 					StdDraw.filledCircle(x, y, radiusPion);
		// 					break;
		// 				case '.': 
		// 					StdDraw.setPenColor(StdDraw.WHITE);
		// 					StdDraw.filledCircle(x, y, radiusPion);
		// 					break;
		// 			}

		// 			StdDraw.setPenRadius(0.01);
		// 			StdDraw.setPenColor(StdDraw.GREEN);
		// 			StdDraw.point(x, y);

		// 		}
		// 	}
		// }

        StdDraw.show();
		
	}

	public static void drawHega(double x,double y) {
		StdDraw.setPenRadius(0.002);
		for (int i = 0; i < 6; i++) {
			StdDraw.line(x + Math.cos(i*ANGLE_STEP)
						,y + Math.sin(i * ANGLE_STEP)
						,x + Math.cos((i+1) * ANGLE_STEP)
						,y + Math.sin((i+1) * ANGLE_STEP));
		}
		StdDraw.setPenRadius(0.001);
	}

	void affiche() {
		graphiqueAffiche();
		// Déclaration des variable pour parcourire le tableau
		int column, line, diag, space;
        char letterCase,numberCase;

		// Parcours des colonne de la moitier droite du tableau
		for(column = 0; column < state.length; column++){

			// Ajout d'espace pour la partie haute du losange
			for (space = state.length -1; space >= column; space--) {
				System.out.print("  ");
			}
			
			// Parcours des diagonale du haut à droite vers le centre
			for (line = 0, diag = state.length - column; line < 1 + column; line++, diag++){
				
				// Nomination & Numeration des cases 
				letterCase = LISTLETTER[line];
				numberCase = LISTNUMBER[diag];

				/**  Numeration des colonnes de 0->7 pour 0->7 (nombre colonne dans le tableau) 
				* + Affichage des case (Fond-couleur + Nomination ligne + numeration colon + Reset style)
				* VERIFIER AFFICHAGE AVEC joueurs[0] pour B
				*/
				switch(state[line][diag]){
					case VIDE: System.out.print(ConsoleColors.WHITE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);break;
					case 'R': System.out.print(ConsoleColors.RED_BACKGROUND + letterCase + numberCase+ ConsoleColors.RESET);;break;
					case 'B': System.out.print(ConsoleColors.BLUE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);break;
					case '-': System.out.print("  ");
				}
				// Ajout des espaces entre les cases
				System.out.print("  ");
			}
			// Retour ligne affichange du plateau
			System.out.println();	
		}		

		// Parcours des colonne de la moitier gauche du tableau
		for(column = 0; column < state.length -1; column++){
			
			// Ajout d'espace pour la partie basse du losange
			for (space = 0; space-1 <= column; space++){
				System.out.print("  ");
			}
			// Parcours des diagonale du milieu vers le bas à gauche du tableau
			for (line = 1 + column , diag = 1; diag < state.length - column; line++, diag++){
				
				letterCase = LISTLETTER[line];
				numberCase = LISTNUMBER[diag];

				switch(state[line][diag]){
					case VIDE: System.out.print(ConsoleColors.WHITE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);break;
					case 'R': System.out.print(ConsoleColors.RED_BACKGROUND + letterCase + numberCase+ ConsoleColors.RESET);;break;
					case 'B': System.out.print(ConsoleColors.BLUE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);break;
					case '-': System.out.print("  ");
				}
				System.out.print("  ");
			}
			System.out.println();
		}
	}

	/**
	 * Joue un tour
	 * 
	 * @param couleur couleur du pion à jouer
	 * @return tableau contenant la position de départ et la destination du pion à
	 *         jouer.
	 */
	String[] jouerIA(char couleur) {

		String src = "";
		String dest = "";


			// String[] a = new String[2];
			// for(int i=0; i < state.length;i++){
			// 	for(int j=0; j < state[i].length;j++){
			// 		if(state[i][j]== couleur){
			// 			src = ""+LISTLETTER[i]+LISTNUMBER[j];
			// 			String[] dest = possibleDests(couleur, i, j);
			// 			for(int k=0; k<dest.length;k++){
			// 				if(deplace(couleur, src, dest[k], ModeMvt.SIMU) == Result.OK){
			// 					a[0]=src;
			// 					a[1]=dest[k];
			// 					return a;
			// 				}
			// 			}
			// 		}
			// 	}
			// }
			// return a;


		int maxScL1=0, xSaveL1=0, ySaveL1=0;
		int maxScL0=0, xSaveL0=0, ySaveL0=0;

		int x1=0,y1=0;
		int x2=0,y2=0;
		int x3=0,y3=0;
		int x4=0,y4=0;
		int x5=0,y5=0;

		int ScL0=0;
		int ScL1=0;
		int ScL2=0;
		int ScL3=0;
		int ScL4=0;
		int ScL5=0;
		int ScL6=0;

		int[] ScL1Tab = new int[2];

		String[] move = new String[2];


		// Trouver case L0
		for(int x0=0; x0 < state.length;x0++){
			for(int y0=0; y0 < state[x0].length;y0++){
				if(state[x0][y0]== couleur){
					String srcL0 = ""+LISTLETTER[x0]+LISTNUMBER[y0];

					// System.out.println("");
					// System.out.println("");
					// System.out.println(WHITE_BACKGROUND+srcL0+RESET+" CaseValue = "+state[x0][y0]+" CaseCoo = "+x0+"-"+y0);

					// Trouver case L1
					String[] destL1 = possibleDests(couleur, x0, y0);
					ScL1+=ScL2;
					
					for(int possL1=0; possL1<destL1.length;possL1++){
						if(deplace(couleur, srcL0, destL1[possL1], ModeMvt.SIMU) == Result.OK){

							x1= setCo('L', destL1[possL1].charAt(0));
							y1= setCo('N', destL1[possL1].charAt(1));

							ScL1 = AddSc(ScL1, couleur, x1, y1,1);
							
							String srcL1 = ""+LISTLETTER[x1]+LISTNUMBER[y1];
							// System.out.println("  |"+BLUE_BACKGROUND+srcL1+RESET+" CaseValue = "+state[x1][x2]+" CaseCoo = ["+x1+"-"+y1+"]");

							// Trouver case L2
							String[] destL2 = possibleDests(couleur,x1, y1);							
							ScL2+=ScL3;
							for(int possL2=0; possL2<destL2.length;possL2++){
								if(issetlc(srcL1) && srcL1 != (destL2[possL2])){
									x2= setCo('L', destL2[possL2].charAt(0));
									y2= setCo('N', destL2[possL2].charAt(1));
									ScL2 = AddSc(ScL2, couleur, x2, y2,1);
									String srcL2 = ""+LISTLETTER[x2]+LISTNUMBER[y2];
									
									// System.out.println("      |"+BLACK_BACKGROUND+srcL2+RESET+" CaseValue = "+state[x2][y2]+" CaseCoo = ["+x2+"-"+y2+"]");							

									// Trouve case L3
									String[] destL3 = possibleDests(couleur, x2, y2);
									ScL3+=ScL4;
									for(int possL3=0;possL3<destL3.length;possL3++){
										if(issetlc(srcL2) && srcL2 != (destL3[possL3])){
											x3= setCo('L', destL3[possL3].charAt(0));
											y3= setCo('N', destL3[possL3].charAt(1));
											ScL3 = AddSc(ScL3, couleur, x3, y3,1);
											String srcL3 = ""+LISTLETTER[x3]+LISTNUMBER[y3];

											// System.out.println("         |"+RED_BACKGROUND+srcL3+RESET+" CaseValue = "+state[x3][y3]+" CaseCoo = ["+x3+"-"+y3+"]");

										}
									}
									ScL2+=ScL3;
									// System.out.println("         |"+RED_BACKGROUND+"Valeur L3 = "+ScL3+RESET);
									ScL3=0;
								}
							}
							ScL1+=ScL2;
							// System.out.println("      |"+BLACK_BACKGROUND+"Valeur L2 = "+ScL2+RESET);
							ScL2=0;
						}
					}
					ScL0+=ScL1;
					// System.out.println("  |"+BLUE_BACKGROUND+"Valeur L1 = "+ScL1+RESET);
					ScL1=0;

					if(ScL0 > maxScL0){
						move[0] = ""+LISTLETTER[x0]+LISTNUMBER[y0];
						xSaveL0 = x0;
						ySaveL0 = y0;
						maxScL0 = ScL0;
					}

					// System.out.println(WHITE_BACKGROUND+"Valeur L0 = "+ScL0+RESET);
					ScL0=0;
				}
			}
		}

		System.out.println("meilleur case : "+move[0]);

		String[] destL1 = possibleDests(couleur, xSaveL0, ySaveL0);



		for(int possL1=0; possL1<destL1.length;possL1++){
			if(deplace(couleur, move[0], destL1[possL1], ModeMvt.SIMU) == Result.OK){

				System.out.println("test");

				x1= setCo('L', destL1[possL1].charAt(0));
				y1= setCo('N', destL1[possL1].charAt(1));

				System.out.println(x1+"-"+y1);

				ScL1 = AddSc(ScL1, couleur, x1, y1,1);
				String srcL1 = ""+LISTLETTER[x2]+LISTNUMBER[y2];
				String[] destL2 = possibleDests(couleur,x1, y1);							

				for(int possL2=0; possL2<destL2.length;possL2++){
					if(issetlc(srcL1) && srcL1 != (destL2[possL2])){
						x2= setCo('L', destL2[possL2].charAt(0));
						y2= setCo('N', destL2[possL2].charAt(1));

						ScL2 = AddSc(ScL2, couleur, x2, y2,1);
						String srcL2 = ""+LISTLETTER[x2]+LISTNUMBER[y2];
						String[] destL3 = possibleDests(couleur,x2, y2);
						
						for(int possL3=0; possL3<destL3.length;possL3++){
							if(issetlc(srcL2) && srcL2 != (destL3[possL3])){
								x3= setCo('L', destL3[possL3].charAt(0));
								y3= setCo('N', destL3[possL3].charAt(1));
		
								ScL3 = AddSc(ScL2, couleur, x2, y2,1);
							}
						}
						ScL2+=ScL3;
						ScL3=0;
					}
				}
				ScL1+=ScL2;
				ScL2=0;
			}

			if(ScL1 > maxScL1){
				System.out.println(RED_BACKGROUND+move[1]+RESET);
				move[1] = ""+LISTLETTER[x1]+LISTNUMBER[y1];
			}
			ScL1=0;
		}

		System.out.println(move[0]+" =>"+move[1]);

		return move;
	}




	public String bestCase(){

		return "az";
	}

	public String bestMove(String src){

		return "AZ";
	}


	public int AddSc(int ScL, char couleur, int x, int y, int coef){
		if(couleur == joueurs[0]){
			switch(state[x][y]){
				case 'R': ScL +=5*coef; break;
				case 'B': ScL +=10*coef; break;
				case '.': ScL +=1*coef; break;
				case '-': ScL +=5*coef; break;
				default: ScL +=0; 
			}
		}else if(couleur == joueurs[1]){
			switch(state[x][y]){
				case 'R': ScL +=10*coef; break;
				case 'B': ScL +=5*coef; break;
				case '.': ScL +=1*coef; break;
				case '-': ScL +=5*coef; break;
				default: ScL +=0; 
			}
		}
		// System.out.println("debug AddSc couleur = "+couleur+" x ="+x+" y="+y+" == "+state[x][y]+" vaut donc "+ScL);	

		return ScL;
	}

	/**
	 * gère le jeu en fonction du joueur/couleur
	 * 
	 * @param couleur
	 * @return tableau de deux chaînes {source,destination} du pion à jouer
	 */
	String[] jouer(char couleur) {
		String src = "";
		String dst = "";
		String[] mvtIa;
		switch (couleur) {
			case 'B':
				System.out.println("Mouvement " + couleur);
				src = input.next();
				dst = input.next();
				System.out.println(src + "->" + dst);
				break;
			case 'R':
				System.out.println("Mouvement " + couleur);
				mvtIa = jouerIA(couleur);
				src = mvtIa[0];
				dst = mvtIa[1];
				System.out.println(src + "->" + dst);
				break;
		}
		return new String[] { src, dst };
	}

	/**
	 * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
	 * 
	 * @param couleur
	 * @return
	 */
	char finPartie(char couleur) {
		
		for(int i=0; i<state.length;i++){
			for(int j=0;j<state[i].length;j++){
				if(state[i][j] == couleur){
					String src = ""+LISTLETTER[i]+LISTNUMBER[j];
					String[] possibleDests = possibleDests(couleur, i, j);
					for(int k=0; k < possibleDests.length;k++){
						if(deplace(couleur, src, possibleDests[k], ModeMvt.SIMU) == Result.OK){
							return 'N';
						}
					}
				}
			}
		}

		return couleur;
	}

	public static void main(String[] args) {

		StuckWin jeu = new StuckWin();
		String src = "";
		String dest;
		String[] reponse;
		Result status;
		char partie = 'N';
		char curCouleur = jeu.joueurs[0];
		char nextCouleur = jeu.joueurs[1];
		char tmp;
		int cpt = 0;

		// version console
		do {
			// séquence pour Bleu ou rouge
			jeu.affiche();
			do {
				status = Result.EXIT;
				reponse = jeu.jouer(curCouleur);
				src = reponse[0];
				dest = reponse[1];
				if ("q".equals(src))
					return;
				status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);
				partie = jeu.finPartie(nextCouleur);
				System.out.println("status : " + status + " partie : " + partie);
			} while (status != Result.OK && partie == 'N');
			tmp = curCouleur;
			curCouleur = nextCouleur;
			nextCouleur = tmp;
			cpt++;
		} while (partie == 'N'); // TODO affiche vainqueur
		jeu.affiche();
		System.out.printf("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
	}
}
