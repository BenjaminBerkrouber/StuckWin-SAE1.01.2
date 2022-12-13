import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.print.attribute.standard.PrinterInfo;

public class StuckWin {

	// Permet de lire des donnée entrer par l'utilisateur
	static final Scanner input = new Scanner(System.in);

	// 
	private static final double BOARD_SIZE = 7;

	// Initialisation de deux tableaux contenants les lettres et numéros pour l'identification des cases.
	public static final char[] LISTLETTER = {'A','B','C','D','E','F','G'}; // egale à 7 (nombre de ligne state)
	public static final char[] LISTNUMBER= {'0','1','2','3','4','5','6','7'}; // egale à 8 (nombre de colone de state)

	// Initialisation de la taille des case et des pions de l'interface graphique
	public static final double radiusCase = 1.5;
	public static final double radiusPion = 0.8;
	public static final double radiusPionEfface = 0.81;

	// Angle pour les hexagone dans l'interface graphique
	public static final double ANGLE_STEP = 2 * Math.PI / 6;

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

	// 
	public static final List<String> coup = new ArrayList<>();
	public static final List<String> etat = new ArrayList<>();

	public static final List<String> win = new ArrayList<>();


	// 
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

		// Initialisation des valeur de la case de départ & d'arrivé : currentCase = R ; B ; - ; . 
		char currentCase;
		char destCase;

		// Initialisation des coordonnée x-y Source et Dest
		int xSource=0, ySource=0;
		int xDest=0, yDest=0;

		// Vérification que le joueur entre 2 caractère et qu'il corresponde au élement des tableau
		if(lcSource.length() != 2 || lcDest.length() != 2)
		{
			result = Result.BAD_SRC;
			printError("BAD_SRC");
			etat.add("BAD_SRC");
			return result;
		}

		// Vérifié que la case de départ et la case d'arrive existe dans le tableau (A1) et non (/^)
		if(!(issetlc(lcSource)) || !(issetlc(lcDest)))
		{
			result = Result.BAD_SRC;
			printError("BAD_SRC");
			etat.add("BAD_SRC");
			return result;
		}

		// Assignation de x-y Source et Dest  
		xSource = setCo('L', lcSource.charAt(0));
		ySource = setCo('N', lcSource.charAt(1));

		xDest = setCo('L', lcDest.charAt(0));
		yDest = setCo('N', lcDest.charAt(1));

		// Assignation de currentCase & destCase
		currentCase = state[xSource][ySource];
		destCase = state[xDest][yDest];

		// Vérifie qu'il existe un pion dans la case
		if(emptylc(currentCase)){
			result = Result.EMPTY_SRC;
			printError("EMPTY_SRC");
			etat.add("EMPTY_SRC");
			return result;
		}
		
		// Vérification couleur du pion à déplace = couleur du joueur
		if(currentCase != couleur){
			result = Result.BAD_COLOR; 
			printError("BAD_COLOR");
			etat.add("BAD_COLOR");
			return result;
		}

		// Vérifie que la case d'arriver est dans les bordure
		if(destCase == '-')
		{
			result = Result.EXT_BOARD;
			printError("EXT_BOARD");
			etat.add("EXT_BOARD");
			return result;
		}

		// Vérifie que la case d'arriver n'est pas occuper
		if(destCase != VIDE)
		{
			result = Result.DEST_NOT_FREE; 
			printError("DEST_NOT_FREE");
			etat.add("DEST_NOT_FREE");
			return result;
		}

		// Verifie la distance entre la case de départ et la case d'arrivé
		possibleDests = possibleDests(couleur, xSource, ySource);

		if(!(possibleDests[0].equals(lcDest) 
		|| possibleDests[1].equals(lcDest) 
		|| possibleDests[2].equals(lcDest)))
		{
			result = Result.TOO_FAR;
			printError("TOO_FAR");
			etat.add("BAD_SRC");
			return result;
		}

		// Déplacement du pion
		if(mode == ModeMvt.REAL)
		{
			state[xDest][yDest] = state[xSource][ySource];
			state[xSource][ySource] = VIDE;
		}

		etat.add("OK");
		
		// Renvoie OK si toute les vérification son passer
		result = Result.OK; return result;
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

		// Vérification dans LISTNUMBER si le numéro (column) existe 
		for(int i=0; i<LISTNUMBER.length;i++){
			if(LISTNUMBER[i] == lcSource.charAt(1)){
				issetC = true;
			}
		}

		// Vérification dans LISTLETTER si la lettre (ligne) existe
		for(int i=0; i <LISTLETTER.length; i++){
			if(LISTLETTER[i] == lcSource.charAt(0)){
				issetL = true;
			}
		}

		// Si la lettre (L) et le numéro (C) existe return true
		if(issetC && issetL){
			issetLC= true;
		}

		return issetLC;
	}

	/**
	 * Verifie si il existe un pion dans la case que on souhaite jouer
	 * à partir de la position de départ currentCase.
	 * @param currentCase La case du tableau que on jeu jouer
	 * @return false si il existe un pion sinon true.
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
	 * Verifie si la case que on souhaite jouer existe dans le tableau
	 * @param List
	 * @return 
	 */
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

			// Vérifie les possibleDests pour le joueur Rouge 
			if(couleur == joueurs[1]){

				// Vérifie si [idLettre|idCol-1] existe
				if(idLettre < LISTLETTER.length && idLettre >= 0 
					&& idCol-1 < LISTNUMBER.length && idCol-1 >= 1 ){
						possibleDests[0] = ""+LISTLETTER[idLettre]+LISTNUMBER[idCol-1];
				}else{
					// Sinon revoie la case actuelle
					possibleDests[0]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];
				}

				// Vérifie si [idLettre+1|idCol-1] existe
				if(idLettre+1 < LISTLETTER.length && idLettre+1 >= 0 
					&& idCol-1 < LISTNUMBER.length && idCol-1 >= 1){
						possibleDests[1] = ""+LISTLETTER[idLettre+1]+LISTNUMBER[idCol-1];
				}else{
					// Sinon revoie la case actuelle
					possibleDests[1]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];
				}

				// Vérifie si [idLettre+1|idCol] existe
				if(idLettre+1 < LISTLETTER.length && idLettre+1 >= 0 
					&& idCol < LISTNUMBER.length && idCol >= 1){					
						possibleDests[2] = ""+LISTLETTER[idLettre+1]+LISTNUMBER[idCol];
				}else{
					// Sinon renvoie la case actuelle
					possibleDests[2]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];
				}
			}
			// Vérifie les possibleDests pour le joueur Bleu
			else
			{
				// Vérifie si [idLettre-1|idCol] existe
				if(idLettre-1 < LISTLETTER.length && idLettre-1 >= 0 
					&& idCol < LISTNUMBER.length && idCol >= 0 ){
						possibleDests[0] = ""+LISTLETTER[idLettre-1]+LISTNUMBER[idCol];
				}else{
					// Sinon renvoie la case actuelle
					possibleDests[0]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];
				}

				// Vérifie si [idLettre-1|idCol-1] existe
				if(idLettre-1 < LISTLETTER.length && idLettre-1 >= 0 
					&& idCol+1 < LISTNUMBER.length && idCol+1 >= 0 ){
						possibleDests[1] = ""+LISTLETTER[idLettre-1]+LISTNUMBER[idCol+1];
				}else{
					// Sinon renvoie la case actuelle
					possibleDests[1]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];
				}

				// Vérifie si [idLettre|idCol+1] existe
				if(idLettre < LISTLETTER.length && idLettre >= 0  
					&& idCol+1 < LISTNUMBER.length && idCol+1 >= 0 ){
						possibleDests[2] = ""+LISTLETTER[idLettre]+LISTNUMBER[idCol+1];
				}else{
					// Sinon renvoie la case actuelle
					possibleDests[2]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];
				}
			}

		return possibleDests;
	}

	void graphiqueAffiche(){

		StdDraw.setXscale(-10.5, 10.5);
        StdDraw.setYscale(-10.5, 10.5);
		StdDraw.enableDoubleBuffering();

		StdDraw.setTitle("StuckWin");
		StdDraw.picture(0, 0, "back.jpg", 25, 25);
		StdDraw.picture(-8, -5, "player.png", 5, 8);
		StdDraw.picture(8, 5, "ia1.png", 5, 8);


		int i;
		double x= 0;
		double y= 0;
		String lc = "";


		x= -5;
		y= 3;
		i=0;
		for(int j = 4; j<8; j++, x+=1.5, y+=1){
			lc = ""+LISTLETTER[i]+LISTNUMBER[j];
			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x= -5;
		y= 1;
		i++;
		for(int j = 3; j<8; j++, x+=1.5, y+=1){
			lc = ""+LISTLETTER[i]+LISTNUMBER[j];
			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x= -5;
		y= -1;
		i++;
		for(int j = 2; j<8; j++, x+=1.5, y+=1){
			lc = ""+LISTLETTER[i]+LISTNUMBER[j];
			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x= -5;
		y= -3;		
		i++;
		for(int j = 1; j<8; j++, x+=1.5, y+=1){
			lc = ""+LISTLETTER[i]+LISTNUMBER[j];
			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x= -3.5;
		y= -4;
		i++;
		for(int j = 1; j<7; j++, x+=1.5, y+=1){
			lc = ""+LISTLETTER[i]+LISTNUMBER[j];
			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x= -2;
		y= -5;
		i++;
		for(int j = 1; j<6; j++, x+=1.5, y+=1){
			lc = ""+LISTLETTER[i]+LISTNUMBER[j];
			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x= -0.5;
		y= -6;
		i++;
		for(int j = 1; j<5; j++, x+=1.5, y+=1){
			lc = ""+LISTLETTER[i]+LISTNUMBER[j];
			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

        StdDraw.show();
		
	}

	public static void drawHega(double x,double y, String lc) {
		StdDraw.setPenRadius(0.002);
		for (int i = 0; i < 6; i++) {
			StdDraw.line(x + Math.cos(i*ANGLE_STEP)
						,y + Math.sin(i * ANGLE_STEP)
						,x + Math.cos((i+1) * ANGLE_STEP)
						,y + Math.sin((i+1) * ANGLE_STEP));
		}
		StdDraw.setPenRadius(0.001);
		// StdDraw.text(x, y, lc);
	}

	public static void drawPion(double x,double y, char value){
		switch(value){
			case 'R': 
				// StdDraw.setPenColor(StdDraw.RED);
				// StdDraw.filledCircle(x, y, radiusPion);
				// StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.picture(x, y, "pionR.png",1.5,1.5);

				break;
			case 'B':
				// StdDraw.setPenColor(StdDraw.BLUE);
				// StdDraw.filledCircle(x, y, radiusPion);
				// StdDraw.setPenColor(StdDraw.BLACK);

				StdDraw.picture(x, y, "pionB.png",1.5,1.5);
				break;

		}
	}

	public static void drawError(String error){
		StdDraw.text(0, 9, error);
	}

	void affiche() {
		// Affiche l'interface graphique 
		graphiqueAffiche();

		// Initialisation des variable pour parcourire le tableau
		int i, j, k, space;

		// Initialisation des variable pour stocker le numéro et la lettre de la case.
        char letterCase,numberCase;

		// Parcours les lignes du tableau en partant du haut
		for(i = 0; i < state.length; i++){

			// Ajout d'espace pour la partie haute du losange
			for (space = state.length -1; space >= i; space--) {
				System.out.print("  ");
			}
			
			// Parcours des diagonale du haut à droite vers la diagonale centrale
			for (j = 0, k = state.length - i; j < 1 + i; j++, k++){
				
				// Nomination & Numeration des cases 
				letterCase = LISTLETTER[j];
				numberCase = LISTNUMBER[k];

				// Affichage de la couleur de la case du tableau state au rang [j][k] en fonction de ca valeur 
				// + Nomination des case letterCase + NumberCase. exemple => B2
				switch(state[j][k]){
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

		// Parcours les lignes du tableau en partant du haut
		for(i = 0; i < state.length -1; i++){
			
			// Ajout d'espace pour la partie basse du losange
			for (space = 0; space-1 <= i; space++){
				System.out.print("  ");
			}

			// Parcours des diagonale de la diagonale centrale jusqu'à en bas à droite
			for (j = 1 + i , k = 1; k < state.length - i; j++, k++){
				
				// Nomination & Numeration des cases 
				letterCase = LISTLETTER[j];
				numberCase = LISTNUMBER[k];

				// Affichage de la couleur de la case du tableau state au rang [j][k] en fonction de ca valeur 
				// + Nomination des case letterCase + NumberCase. exemple => B2
				switch(state[j][k]){
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

		// Integer.parseInt(1) == 1
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
				System.out.println(ConsoleColors.RED_BACKGROUND+move[1]+ConsoleColors.RESET);
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

	public static void printGame(){
		File file = new File("StuckWin_00.csv");

        if (!file.exists()){
			try{
				file.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		try(BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(file))){
			
			bufferedWriter.write("StuckWin"+","+"1V1");
			bufferedWriter.newLine();
			bufferedWriter.write("Groupe 40 : , Berkrouber Benjamin , Taskin Semih");
			bufferedWriter.newLine();
			bufferedWriter.write("Joueur, Src, Dest, Etats");
			bufferedWriter.newLine();
			
			for(int i=0; i <coup.size(); i++){
				bufferedWriter.write(coup.get(i));
				bufferedWriter.newLine();
			}

			bufferedWriter.write(win.get(0));

		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void printError(String error){
		StdDraw.text(0, -9, error);
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
				coup.add(couleur+","+src+","+dst);
				break;
			case 'R':
				System.out.println("Mouvement " + couleur);
				mvtIa = jouerIA(couleur);
				src = mvtIa[0];
				dst = mvtIa[1];
				System.out.println(src + "->" + dst);
				coup.add(couleur+","+src+","+dst);
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
		
		// Parcours le tableau par ligne
		for(int i=0; i<state.length;i++){

			// Parcours le tableau par column
			for(int j=0;j<state[i].length;j++){

				// Verifique si la case à la même valeur que la variable couleur 'R' OU 'B'
				if(state[i][j] == couleur){

					//Nomination et numération de la case src
					String src = ""+LISTLETTER[i]+LISTNUMBER[j];

					// Cherche les destination possible de cette case
					String[] possibleDests = possibleDests(couleur, i, j);

					// Parcours le tableau de possiblité de déplacement
					for(int k=0; k < possibleDests.length;k++){

						// Vérifie si l'un des déplacement est possible
						if(deplace(couleur, src, possibleDests[k], ModeMvt.SIMU) == Result.OK){
							// Si on déplacement et possible return 'N' => partie non fini.
							return 'N';
						}
					}
				}
			}
		}

		// Renvoie la couleur du joueur ne pouvant plus faire de déplacement
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
		win.add("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
		printGame();
		System.out.println("Vous pouvez retrouver votre partie le fichier test.txt");
	}
}
