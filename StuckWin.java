import java.util.*;

public class StuckWin {

	static final Scanner input = new Scanner(System.in);
	
	public static final String RED_BACKGROUND = "\033[41m"; // RED
	public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
	public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE
	public static final String RESET = "\033[0m"; // Text Reset
	public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK

	private static final double BOARD_SIZE = 7;

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

		// Récupération currentCase exemple : (A2)
		char ligneSource = lcSource.charAt(0);
		char columnSource = lcSource.charAt(1);
		char ligneDest = lcDest.charAt(0);
		char columnDest = lcDest.charAt(1);

		switch(ligneSource){
			case 'A': ligneSource = 0;break;
			case 'B': ligneSource = 1;break;
			case 'C': ligneSource = 2;break;
			case 'D': ligneSource = 3;break;
			case 'E': ligneSource = 4;break;
			case 'F': ligneSource = 5;break;
			case 'G': ligneSource = 6;break;
		}
		switch(columnSource){
			case '1': columnSource = 0;break;
			case '2': columnSource = 1;break;
			case '3': columnSource = 2;break;
			case '4': columnSource = 3;break;
			case '5': columnSource = 4;break;
			case '6': columnSource = 5;break;
			case '7': columnSource = 6;break;
		}
		possibleDests(couleur, ligneSource, columnSource);		

		char currentCase = state[ligneSource][columnSource];

		switch(currentCase){
			case 1 : ; break;
			default: ;
		}

		return result;
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

		String[] tab = new String[3];
		char currentCase = state[idLettre][idCol];

		// for(int i = 0; i<state.length;i++)
		// {
		// 	for(int j = i; j<state[i].length;j++)
		// 	{
		// 		if()
		// 	}
		// }

		return tab;
	}

	/**
	 * Affiche le plateau de jeu dans la configuration portée par
	 * l'attribut d'état "state"
	 */
	
	void affiche() {

		int ligne, column, diag, space;
        char lettreligne = ' ';

		for(ligne = 0; ligne < state.length; ligne++){

			for (space = state.length-2; space >= ligne; space--) {
				System.out.print("  ");
			}
			for (column = 0, diag = 7 - ligne; column < 1 + ligne; column++, diag++){
				switch(column){
					case 0: lettreligne = 'A';break;
					case 1: lettreligne = 'B';break;
					case 2: lettreligne = 'C';break;
					case 3: lettreligne = 'D';break;
					case 4: lettreligne = 'E';break;
					case 5: lettreligne = 'F';break;
					case 6: lettreligne = 'G';break;
				}
				switch(state[column][diag]){
					case '.': System.out.print(BLACK_BACKGROUND + WHITE_BACKGROUND + lettreligne + Integer.toString(diag) + RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + lettreligne + Integer.toString(diag)+ RESET);;break;
					case 'B': System.out.print(BLUE_BACKGROUND + lettreligne + Integer.toString(diag) + RESET);break;
					case '-': System.out.print("  ");
				}
				System.out.print("  ");
			}
			
			System.out.println();
			
		}		
		for(ligne = 0; ligne < 6; ligne++){
			for (space = 0; space <= ligne; space++) 
			{
				System.out.print("  ");
			}

			for (column = 1 + ligne , diag = 1; diag < 7 - ligne; column++, diag++){
				switch(column){
					case 0: lettreligne = 'A';break;
					case 1: lettreligne = 'B';break;
					case 2: lettreligne = 'C';break;
					case 3: lettreligne = 'D';break;
					case 4: lettreligne = 'E';break;
					case 5: lettreligne = 'F';break;
					case 6: lettreligne = 'G';break;
				}
				switch(state[column][diag]){
					case '.': System.out.print(BLACK_BACKGROUND + WHITE_BACKGROUND + lettreligne + Integer.toString(diag)+ RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + lettreligne + Integer.toString(diag)+RESET);break;
					case 'B': System.out.print(BLUE_BACKGROUND + lettreligne + Integer.toString(diag)+ RESET);break;
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
