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

		// Récupération currentCase & DestCase exemple : lineSource = E columnSource = 3)
		char lineSource = lcSource.charAt(0);
		char columnSource = lcSource.charAt(1);
		char lineDest = lcDest.charAt(0);
		char columnDest = lcDest.charAt(1);

		// Stock la valeur de la case de départ & d'arrivé : currentCase = R ; B ; - ; . 
		char currentCase;
		char DestCase;
		
		char currentColor = couleur;

		// Tableau qui stock les case possiblement jouable
		String[] possibleDests = new String[3];

		for(int i = 0; i< state.length;i++)
		{
			
		}

		switch(lineSource){
			case 'A': lineSource = 0;break;
			case 'B': lineSource = 1;break;
			case 'C': lineSource = 2;break;
			case 'D': lineSource = 3;break;
			case 'E': lineSource = 4;break;
			case 'F': lineSource = 5;break;
			case 'G': lineSource = 6;break;
			default:
		}
		switch(columnSource){
			case '0': columnSource = 0;break;
			case '1': columnSource = 1;break;
			case '2': columnSource = 2;break;
			case '3': columnSource = 3;break;
			case '4': columnSource = 4;break;
			case '5': columnSource = 5;break;
			case '6': columnSource = 6;break;
			case '7': columnSource = 7;break;
			default:
		}

		switch(lineDest){
			case 'A': lineDest = 0;break;
			case 'B': lineDest = 1;break;
			case 'C': lineDest = 2;break;
			case 'D': lineDest = 3;break;
			case 'E': lineDest = 4;break;
			case 'F': lineDest = 5;break;
			case 'G': lineDest = 6;break;
			default:
		}
		switch(columnDest){
			case '0': columnDest = 0;break;
			case '1': columnDest = 1;break;
			case '2': columnDest = 2;break;
			case '3': columnDest = 3;break;
			case '4': columnDest = 4;break;
			case '5': columnDest = 5;break;
			case '6': columnDest = 6;break;
			case '7': columnDest = 7;break;
			default:
		}

		currentCase = state[lineSource][columnSource];
		DestCase = state[lineDest][columnDest];
		possibleDests = possibleDests(couleur, lineSource, columnSource);		


		// Verification qu'il existe un pion
		if(!issetSrcPion(currentCase)){
			return Result.EMPTY_SRC;
		}

		// Vérification couleur du pion à déplace = couleur du joueur
		if(currentCase != couleur){
			return Result.BAD_COLOR;
		}

		// Vérifie que la case d'arriver est dans les bordure
		if(DestCase == '-')
		{
			return Result.EXT_BOARD;
		}

		// Vérifie que la case d'arriver n'est pas occuper
		if(DestCase != VIDE)
		{
			return Result.DEST_NOT_FREE;
		}

		// Verifie la distance entre la case de départ et la case d'arrivé
		if(!valideDistanceSrcToDest(possibleDests, lcDest))
		{
			return Result.TOO_FAR;
		}

		// Déplacement du pion
		System.out.println("Success");
		state[lineDest][columnDest] = state[lineSource][columnSource];
		state[lineSource][columnSource] = '.';

		return Result.OK;
	}

	/**
	 * Verifie si il existe un pion dans la case que on souhaite jouer
	 * à partir de la position de départ currentCase.
	 * @param currentCase La case du tableau que on jeu jouer
	 * @return true si il existe un pion sinon false.
	 */
	public boolean issetSrcPion(char currentCase){
		boolean issetPion = false;
		for(int i=0; i < joueurs.length; i++)
		{
			if(currentCase == joueurs[i])
			{
				issetPion = true;
			}
		}

		return issetPion;
	}

		/**
	 * Verifie si la distance entre currentCase et DestCase et valide
	 * à partir de la position de départ lcDest.
	 * @param possibleDests tableau des trois positions jouables par le pion
	 * @param lcDest id de la case de déplacement souhaité
	 * @return true si la distance et valide sinon false.
	 */
	public boolean valideDistanceSrcToDest(String[] possibleDests, String lcDest){
		boolean valideDistanceSrcToDest = false;
		for(int i = 0; i <= possibleDests.length ; i++){ 
			if(!(possibleDests[i].equals(lcDest))){
				valideDistanceSrcToDest = true;
			}
		}
		return valideDistanceSrcToDest;
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

		// throw new java.lang.UnsupportedOperationException("à compléter possible déplacement");

		String[] possibleDests = new String[3];

		if(couleur == 'R')
		{
			possibleDests[0]= Integer.toString(idLettre) + Integer.toString(idCol -1);
			possibleDests[1]= Integer.toString(idLettre +1) + Integer.toString(idCol -1);
			possibleDests[2]= Integer.toString(idLettre +1) + Integer.toString(idCol);
		}
		else
		{
			possibleDests[0]= Integer.toString(idLettre -1) + Integer.toString(idCol -1);
			possibleDests[1]= Integer.toString(idLettre -1) + Integer.toString(idCol +1);
			possibleDests[2]= Integer.toString(idLettre ) + Integer.toString(idCol +1);
		}

		String dest, lineDest, columndest="";

		lineDest = LIST[0]+LIST[1];
		
		for(int i =0;i<3;i++)
		{
			switch(possibleDests[i].charAt(0)){
				case '0': lineDest = "A";break;
				case '1': ldest = "A";break;
				case '2': ldest = "B";break;
				case '3': ldest = "C";break;
				case '4': ldest = "D";break;
				case '5': ldest = "E";break;
				case '6': ldest = "F";break;
				case '7': ldest = "G";break;
				default:;
			}
			switch(possibleDests[i].charAt(1)){
				case '0': cdest = "0";break;
				case '1': cdest = "1";break;
				case '2': cdest = "2";break;
				case '3': cdest = "3";break;
				case '4': cdest = "4";break;
				case '5': cdest = "5";break;
				case '6': cdest = "6";break;
				case '7': cdest = "7";break;
			}
		dest = linedest+columndest;
		possibleDests[i]=dest;
		}

		return possibleDests;
	}

	/**
	 * Affiche le plateau de jeu dans la configuration portée par
	 * l'attribut d'état "state"
	 */
	void affiche() {
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
			for (line = 0, diag = 7 - column; line < 1 + column; line++, diag++){
				
				// Nomination & Numeration des cases 
				letterCase = LISTLETTER[line];
				numberCase = LISTNUMBER[diag];

				/**  Numeration des colonnes de 0->7 pour 0->7 (nombre colonne dans le tableau) 
				* + Affichage des case (Fond-couleur + Nomination ligne + numeration colon + Reset style)
				*/
				switch(state[line][diag]){
					case '.': System.out.print(WHITE_BACKGROUND + letterCase + numberCase + RESET);break;
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
		for(column = 0; column < 6; column++){
			
			// Ajout d'espace pour la partie basse du losange
			for (space = 0; space-1 <= column; space++){
				System.out.print("  ");
			}
			// Parcours des diagonale du milieu vers le bas à gauche du tableau
			for (line = 1 + column , diag = 1; diag < state.length - column; line++, diag++){
				
				letterCase = LISTLETTER[line];
				numberCase = LISTNUMBER[diag];

				switch(state[line][diag]){
					case '.': System.out.print(WHITE_BACKGROUND + letterCase + numberCase + RESET);break;
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
		// votre code ici. Supprimer la ligne ci-dessous.
		throw new java.lang.UnsupportedOperationException("à compléter joueur IA");
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
		// votre code ici. Supprimer la ligne ci-dessous.
		throw new java.lang.UnsupportedOperationException("à compléter la fin de la partie");
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
