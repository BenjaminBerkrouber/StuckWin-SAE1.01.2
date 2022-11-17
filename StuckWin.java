import java.util.*;

public class StuckWin {

	static final Scanner input = new Scanner(System.in);
	
	public static final String RED_BACKGROUND = "\033[41m"; // RED
	public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
	public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE
	public static final String RESET = "\033[0m"; // Text Reset
	public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK

	private static final double BOARD_SIZE = 7;

	public static final char[] LIST = {'A','B','C','D','E','F','G'};

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

		// mode = ModeMvt.SIMU;
		// mode = ModeMvt.REAL;

		// Récupération currentCase exemple : (E3)
		char lineSource = lcSource.charAt(0);
		char columnSource = lcSource.charAt(1);

		// Récupération FutureCase exemple : (D4)
		char lineDest = lcDest.charAt(0);
		char columnDest = lcDest.charAt(1);

		// Stockage valeur current&future Case
		char currentCase = state[lineSource][columnSource];
		char futureCase = state[lineDest][columnDest];

		System.out.print(" test ");
		System.out.print(currentCase);
		System.out.print(futureCase);


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

		String[] Destination_possible = possibleDests(couleur, lineSource, columnSource);		

		if(couleur == 'R')
		{
			for(int i=0 ; i<3 ; i++)
			{
				if(Destination_possible[i].equals(lcDest) && mode ==  ModeMvt.REAL)
				{
					System.out.println("Success");
					state[lineDest][columnDest];
				}
			}
		}

		return Result.OK;
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

		String[] tab = new String[3];


		if(couleur == 'R')
		{
			tab[0]= Integer.toString(idLettre) + Integer.toString(idCol -1);
			tab[1]= Integer.toString(idLettre +1) + Integer.toString(idCol -1);
			tab[2]= Integer.toString(idLettre +1) + Integer.toString(idCol);
		}
		else
		{
			tab[0]= Integer.toString(idLettre -1) + Integer.toString(idCol -1);
			tab[1]= Integer.toString(idLettre -1) + Integer.toString(idCol +1);
			tab[2]= Integer.toString(idLettre ) + Integer.toString(idCol +1);
		}

		String dest, ldest = "", cdest="";

		for(int i =0;i<3;i++)
		{
			switch(tab[i].charAt(0)){
				case '0': ldest = "A";break;
				case '1': ldest = "A";break;
				case '2': ldest = "B";break;
				case '3': ldest = "C";break;
				case '4': ldest = "D";break;
				case '5': ldest = "E";break;
				case '6': ldest = "F";break;
				case '7': ldest = "G";break;
				default:;
			}
			switch(tab[i].charAt(1)){
				case '0': cdest = "0";break;
				case '1': cdest = "1";break;
				case '2': cdest = "2";break;
				case '3': cdest = "3";break;
				case '4': cdest = "4";break;
				case '5': cdest = "5";break;
				case '6': cdest = "6";break;
				case '7': cdest = "7";break;
			}
		dest = ldest+cdest;
		tab[i]=dest;
		}
		return tab;
	}

	/**
	 * Affiche le plateau de jeu dans la configuration portée par
	 * l'attribut d'état "state"
	 */
	void affiche() {
		// Déclaration des variable pour parcourire le tableau
		int column, line, diag, space;
        char lettreline = ' ';

		// Parcours des colonne de la moitier droite du tableau
		for(column = 0; column < state.length; column++){

			// Ajout d'espace pour la partie haute du losange
			for (space = state.length -1; space >= column; space--) {
				System.out.print("  ");
			}
			
			// Parcours des diagonale du haut à droite vers le centre
			for (line = 0, diag = 7 - column; line < 1 + column; line++, diag++){
				
				// Nomination des lignes en lettre de A->G pour 0->7 (nombre de ligne dans le tableau)
				lettreline = LIST[line];

				/**  Numeration des colonnes de 0->7 pour 0->7 (nombre colonne dans le tableau) 
				* + Affichage des case (Fond-couleur + Nomination ligne + numeration colon + Reset style)
				*/
				switch(state[line][diag]){
					case '.': System.out.print(WHITE_BACKGROUND + lettreline + Integer.toString(diag) + RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + lettreline + Integer.toString(diag)+ RESET);;break;
					case 'B': System.out.print(BLUE_BACKGROUND + lettreline + Integer.toString(diag) + RESET);break;
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
				
				lettreline = LIST[line];

				switch(state[line][diag]){
					case '.': System.out.print(BLACK_BACKGROUND + WHITE_BACKGROUND + lettreline + Integer.toString(diag)+ RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + lettreline + Integer.toString(diag)+RESET);break;
					case 'B': System.out.print(BLUE_BACKGROUND + lettreline + Integer.toString(diag)+ RESET);break;
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
