import java.net.IDN;
import java.util.*;

public class StuckWin {

	static final Scanner input = new Scanner(System.in);
	
	public static final String RED_BACKGROUND = "\033[41m"; // RED
	public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
	public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE
	public static final String RESET = "\033[0m"; // Text Reset
	public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK

	private static final double BOARD_SIZE = 7;

	public static final char[] LISTLETTER = {'A','B','C','D','E','F','G'};
	public static final char[] LISTNUMBER= {'0','1','2','3','4','5','6','7'};

	enum Result {
		OK
		, BAD_COLOR
		, DEST_NOT_FREE
		, EMPTY_SRC
		, TOO_FAR
		, EXT_BOARD
		, EXIT
	}

	enum ModeMvt {
		REAL
		, SIMU
	}

	final char[] joueurs = { 'B', 'R' };
	final int SIZE = 8;
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

		int xSource=0;
		int ySource=0;
		int xDest=0;
		int yDest=0;

		// Vérification que le joueur entre 2 caractère et qu'il corresponde au élement des tableau
		if(lcSource.length() != 2 || lcDest.length() != 2)
		{
			result = Result.EMPTY_SRC;return result;
		}

		if(!(issetlc(lcSource)) || !(issetlc(lcDest)))
		{
			result = Result.EMPTY_SRC;return result;
		}

		// Initialisation de x-y Source et Dest  
		for(int i =0; i < LISTLETTER.length; i++)
		{
			if(LISTLETTER[i] == lcSource.charAt(0))
			{
				xSource = i;
			}

			if(LISTLETTER[i] == lcDest.charAt(0))
			{
				xDest = i;
			}
		}

		for(int i =0; i < LISTNUMBER.length; i++)
		{
			if(LISTNUMBER[i] == lcSource.charAt(1))
			{
				ySource = i;
			}

			if(LISTNUMBER[i] == lcDest.charAt(1))
			{
				yDest = i;
			}
		}

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
				if(idLettre <= LISTLETTER.length && idLettre >= 0 && idCol-1 <= LISTNUMBER.length && idCol-1 >= 0){
					possibleDests[0] = ""+LISTLETTER[idLettre]+LISTNUMBER[idCol-1];
				}else{possibleDests[0]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}

				if(idLettre+1 <= LISTLETTER.length && idLettre+1 >= 0 && idCol-1 <= LISTNUMBER.length && idCol-1 >= 0){
					possibleDests[1] = ""+LISTLETTER[idLettre+1]+LISTNUMBER[idCol-1];
				}else{possibleDests[1]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}
								
				if(idLettre+1 <= LISTLETTER.length && idLettre+1 >= 0 && idCol <= LISTNUMBER.length && idCol>= 0){
					possibleDests[2] = ""+LISTLETTER[idLettre+1]+LISTNUMBER[idCol];
				}else{possibleDests[2]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}
				
			}
			else
			{
				if(idLettre-1 <= LISTLETTER.length && idLettre-1 >= 0 && idCol <= LISTNUMBER.length && idCol >= 0){
					possibleDests[0] = ""+LISTLETTER[idLettre-1]+LISTNUMBER[idCol];
				}else{possibleDests[0]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}


				if(idLettre-1 <= LISTLETTER.length && idLettre-1 >= 0 && idCol+1 <= LISTNUMBER.length && idCol+1 >= 0){
					possibleDests[1] = ""+LISTLETTER[idLettre-1]+LISTNUMBER[idCol+1];
				}else{possibleDests[1]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}

				if(idLettre <= LISTLETTER.length && idLettre >= 0 && idCol+1 <= LISTNUMBER.length && idCol+1 >= 0){
					possibleDests[2] = ""+LISTLETTER[idLettre]+LISTNUMBER[idCol+1];
				}else{possibleDests[2]= ""+LISTLETTER[idLettre]+LISTNUMBER[idCol];}

			}

		return possibleDests;
	}

	// void graphiqueAffiche(){

	// 	StdDraw.setXscale(-1.2, 1.2);
    //     StdDraw.setYscale(-1.2, 1.2);

    //     StdDraw.setPenRadius(0.001);
    //     StdDraw.setPenColor(StdDraw.BLUE);
	// 	StdDraw.enableDoubleBuffering();


	// 	// StdDraw.circle(0, 1, 0.1);
	// 	// StdDraw.circle(0, 1, 0.1);
	// 	int x= 0;
	// 	int y= 1;

	// 	for(int i=0;i<state.length;i++)
	// 	{
	// 		for (int j = 0, diag = state.length - i; j < 1 + i; j++, diag++){
	// 			StdDraw.circle(x, y, 0.1);
	// 			System.out.println(j+" - "+diag);
	// 		}
	// 		x -= 0.1;
	// 		y -= 0.1;
	// 		System.out.println("");
	// 	}

	// 	// double[] tPointsX = new double[6];
    //     // double[] tPointsY = new double[6];

	// 	// setPoints(tPointsX, tPointsY);

	// 	// StdDraw.polygon(tPointsX, tPointsY);

    //     StdDraw.show();
		
	// }

	/**
     * Calcule les coordonnées de points répartis sur le cercle trigo
     * et les range dans 2 tableaux 1D
     * @param tX tableau des abscisses
     * @param tY tableau des ordonnées
     */
    public static void setPoints(double[] tX, double[] tY) {
        int n = tX.length;
        final double ANGLE_STEP = 2 * Math.PI / n;
        for (int i = 0; i < n; i++) {
            tX[i] = Math.cos(i * ANGLE_STEP);
            tY[i] = Math.sin(i * ANGLE_STEP);
        }
    }

	void affiche() {
		// graphiqueAffiche();
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
					case VIDE: System.out.print(WHITE_BACKGROUND + letterCase + numberCase + RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + letterCase + numberCase+ RESET);;break;
					case 'B': System.out.print(BLUE_BACKGROUND + letterCase + numberCase + RESET);break;
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
					case VIDE: System.out.print(WHITE_BACKGROUND + letterCase + numberCase + RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + letterCase + numberCase+ RESET);;break;
					case 'B': System.out.print(BLUE_BACKGROUND + letterCase + numberCase + RESET);break;
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

		// String src = "";
		// String[] a = new String[2];
		// for(int i=0; i < state.length;i++){
		// 	for(int j=0; j < state[i].length;j++){
		// 		if(state[i][j]== couleur){
		// 			src = ""+LISTLETTER[i]+LISTNUMBER[j];
		// 			String[] dest = possibleDests(couleur, i, j);
		// 			for(int k=0; k<dest.lenght;k++){
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

		int MaxSc=0;
		int Sc=0;

		int x1=0,y1=0;
		int x2=0,y2=0;
		int x3=0,y3=0;

		String[] a = new String[2];


		// Trouver case L0
		for(int x0=0; x0 < state.length;x0++){
			for(int y0=0; y0 < state[y0].length;y0++){
				
				if(state[x0][y0]== couleur){

					String srcL0 = ""+LISTLETTER[x0]+LISTNUMBER[y0];
					// Trouver case L1
					String[] destL1 = possibleDests(couleur, x0, y0);

					for(int possL1=0; possL1<destL1.length;possL1++){
						if(deplace(couleur, srcL0, destL1[possL1], ModeMvt.SIMU) == Result.OK){
							
							for(int letter=0;letter<=LISTLETTER[letter];letter++){
								if(letter == LISTLETTER[letter]){x1=letter;}
							}
							for(int number=0;number<=LISTNUMBER[number];number++){
								if(number == LISTLETTER[number]){y1=number;}
							}

							switch(state[x1][y1]){
								case 'R': Sc -=10; break;
								case 'B': Sc -=10; break;
								case '.': Sc +=1; break;
							}
							String srcL1 = ""+LISTLETTER[x1]+LISTNUMBER[y1];
							// Trouver case L2
							String[] destL2 = possibleDests(couleur,x1, y1);

							for(int possL2=0; possL2<destL2.length;possL2++){
								if(deplace(couleur, srcL1, destL2[possL2], ModeMvt.SIMU) == Result.OK){

									for(int letter=0;letter<LISTLETTER[letter];letter++){
										if(letter == LISTLETTER[letter]){x2=letter;}
									}
									for(int number=0;number<LISTNUMBER[number];number++){
										if(number == LISTLETTER[number]){y2=number;}
									}
									
									switch(state[x2][y2]){
										case 'R': Sc +=3; break;
										case 'B': Sc +=2; break;
										case '.': Sc +=1; break;
									}
									
									String srcL2 = ""+LISTLETTER[x2]+LISTNUMBER[y2];
									// Trouver case L3
									String[] destL3 = possibleDests(couleur,x2, y2);

									for(int possL3=0; possL3<destL3.length;possL3++){
										if(deplace(couleur, srcL2, destL3[possL3], ModeMvt.SIMU) == Result.OK){
											
											for(int letter=0;letter<LISTLETTER[letter];letter++){
												if(letter == LISTLETTER[letter]){x2=letter;}
											}
											for(int number=0;number<LISTNUMBER[number];number++){
												if(number == LISTLETTER[number]){y2=number;}
											}
											
											switch(state[x3][y3]){
												case 'R': Sc +=3; break;
												case 'B': Sc +=2; break;
												case '.': Sc +=1; break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return a;
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
		
		for(int i=0; i<state.length;i++)
		{
			for(int j=0;j<state[i].length;j++)
			{
				if(state[i][j] == couleur)
				{
					String src = ""+LISTLETTER[i]+LISTNUMBER[j];
					String[] possibleDests = possibleDests(couleur, i, j);
					for(int k=0; k < possibleDests.length;k++)
					{
						if(deplace(couleur, src, possibleDests[k], ModeMvt.SIMU) == Result.OK)
						{
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
		System.out.printf("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
	}
}
