
/**
 * IUT Nord Franche-Comté BUT informatique S1
 * Année Universitaire 2022-2023
 * SAE S1.01 / Groupe 40 
 * Berkrouber Benjamin [benjamin.berkrouber@edu.univ-fcomte.fr]
 * Taskin Semih [semih.taskin@edu.univ-fcomte.fr]
 */

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.*;

import javax.print.DocFlavor.STRING;
import javax.print.attribute.standard.PrinterInfo;
import javax.swing.SwingConstants;

public class StuckWin {

	// Permets de lire des données entrer par l'utilisateur
	static final Scanner input = new Scanner(System.in);

	//
	public static final double BOARD_SIZE = 7;

	// Initialisation de deux tableaux contenants les lettres et numéros pour
	// l'identification des cases.
	public static final char[] LISTLETTER = { 'A', 'B', 'C', 'D', 'E', 'F', 'G' }; // egale à 7 (nombre de ligne state)
	public static final char[] LISTNUMBER = { '0', '1', '2', '3', '4', '5', '6', '7' }; // egale à 8 (nombre de colone
	// de state)

	// Initialisation de la taille des cases et des pions de l'interface graphique
	public static final double RADIUSCASE = 1.5;
	public static final double RADPUISPION = 1;

	// Angle pour les hexagones dans l'interface graphique
	public static final double ANGLE_STEP = 2 * Math.PI / 6;

	// Liste d'énumérations des différentes erreurs possibles
	enum Result {
		OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT, BAD_SRC
	}

	// Liste d'énumération des différents modes de jeu possible
	enum ModeJeuPlay {
		PVP, PVIA, PVIA2, IAVIA2
	}

	ModeJeuPlay CurrentModePlay = ModeJeuPlay.PVP;

	// Liste d'énumération des différents modes d'affichage possible
	enum ModeJeuAffiche {
		CONSOLE, GUI
	}

	ModeJeuAffiche CurrentModeAffiche = ModeJeuAffiche.CONSOLE;

	// Liste d'énumération des différents modes de sauvegarde possible
	enum ModeJeuSave {
		YES, NO
	}

	ModeJeuSave CurrentModeSave = ModeJeuSave.NO;

	// Défini la possiblité de tester un déplacement ou de l'éxécuter
	enum ModeMvt {
		REAL, SIMU
	}

	// Tableau comportant le nombre de joueur et leurs noms
	final char[] joueurs = { 'B', 'R' };

	// Liste pour sauvegarder les coups joués, leur état et qui gagne
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

	// Liste pour sauvegarder les coordonnées des centres des cases dans l'interface
	// grahpique
	public static final List<Double> xCenterCaseGUi = new ArrayList<>();
	public static final List<Double> yCenterCaseGUi = new ArrayList<>();
	public static final List<String> caseGUI = new ArrayList<>();

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

		// Tableau qui stocke les cases possiblement jouables
		String[] possibleDests = new String[3];

		// Initialisation des valeurs de la case de départ & d'arrivé : currentCase = R
		// ; B ; - ; .
		char currentCase;
		char destCase;

		// Initialisation des coordonnée x-y Source et Dest
		int xSource = 0, ySource = 0;
		int xDest = 0, yDest = 0;

		// Vérification que le joueur entre 2 caractères qu'il corresponde au élement
		// des tableaux
		if (lcSource.length() != 2 || lcDest.length() != 2) {
			result = Result.BAD_SRC;
			return result;
		}

		// Vérifié que la case de départ et la case d'arriver existent dans le tableau
		// (A1) et non (/^)
		if (!(issetlc(lcSource)) || !(issetlc(lcDest))) {
			result = Result.BAD_SRC;
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
		if (emptylc(currentCase)) {
			result = Result.EMPTY_SRC;
			return result;
		}

		// Vérification couleur du pion à déplace = couleur du joueur
		if (currentCase != couleur) {
			result = Result.BAD_COLOR;
			return result;
		}

		// Vérifie que la case d'arriver est dans les bordures
		if (destCase == '-') {
			result = Result.EXT_BOARD;
			return result;
		}

		// Vérifie que la case d'arriver n'est pas occupée
		if (destCase != VIDE) {
			result = Result.DEST_NOT_FREE;
			return result;
		}

		// Verifie la distance entre la case de départ et la case d'arrivé
		possibleDests = possibleDests(couleur, xSource, ySource);

		if (!(possibleDests[0].equals(lcDest)
				|| possibleDests[1].equals(lcDest)
				|| possibleDests[2].equals(lcDest))) {
			result = Result.TOO_FAR;
			return result;
		}

		// Déplacement du pion
		if (mode == ModeMvt.REAL) {
			state[xDest][yDest] = state[xSource][ySource];
			state[xSource][ySource] = VIDE;
		}

		// Renvoie OK si toutes les vérifications son passé
		result = Result.OK;
		return result;
	}

	/**
	 * Verifie si la case que on souhaite jouer existe dans le tableau
	 *
	 * @param lcSource La case du tableau que on jeu jouer
	 * @return true si il existe un pion sinon false.
	 */
	public boolean issetlc(String lcSource) {
		boolean issetLC = false;
		boolean issetL = false;
		boolean issetC = false;

		// Vérification dans LISTNUMBER si le numéro (column) existe
		for (int i = 0; i < LISTNUMBER.length; i++) {
			if (LISTNUMBER[i] == lcSource.charAt(1)) {
				issetC = true;
			}
		}

		// Vérification dans LISTLETTER si la lettre (ligne) existe
		for (int i = 0; i < LISTLETTER.length; i++) {
			if (LISTLETTER[i] == lcSource.charAt(0)) {
				issetL = true;
			}
		}

		// Si la lettre (L) et le numéro (C) existe return true
		if (issetC && issetL) {
			issetLC = true;
		}

		return issetLC;
	}

	/**
	 * Verifie si il existe un pion dans la case que on souhaite jouer
	 * à partir de la position de départ currentCase.
	 *
	 * @param currentCase La case du tableau que on jeu jouer
	 * @return false si il existe un pion sinon true.
	 */
	public boolean emptylc(char currentCase) {
		boolean emptylc = true;
		for (int i = 0; i < joueurs.length; i++) {
			if (currentCase == joueurs[i]) {
				emptylc = false;
			}
		}
		return emptylc;
	}

	/**
	 * Renvoie la coordonée de la cas d'entrée
	 *
	 * @param List    'N' ou 'L' selon qu'on choisisse de chercher dans la liste des
	 *                lettre ou des chiffre
	 * @param element case (A2) dont on veux les coordonnée
	 * @return i la coordonnée de la case d'entrée
	 */
	public int setCo(char List, char element) {
		if (List == 'L') {
			for (int i = 0; i < LISTLETTER.length; i++) {
				if (LISTLETTER[i] == element) {
					return i;
				}
			}
		} else if (List == 'N') {
			for (int i = 0; i < LISTNUMBER.length; i++) {
				if (LISTNUMBER[i] == element) {
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
		if (couleur == joueurs[1]) {

			// Vérifie si [idLettre|idCol-1] existe
			if (idLettre < LISTLETTER.length && idLettre >= 0
					&& idCol - 1 < LISTNUMBER.length && idCol - 1 >= 1) {
				possibleDests[0] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol - 1];
			} else {
				// Sinon renvoie la case actuelle
				possibleDests[0] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol];
			}

			// Vérifie si [idLettre+1|idCol-1] existe
			if (idLettre + 1 < LISTLETTER.length && idLettre + 1 >= 0
					&& idCol - 1 < LISTNUMBER.length && idCol - 1 >= 1) {
				possibleDests[1] = "" + LISTLETTER[idLettre + 1] + LISTNUMBER[idCol - 1];
			} else {
				// Sinon renvoie la case actuelle
				possibleDests[1] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol];
			}

			// Vérifie si [idLettre+1|idCol] existe
			if (idLettre + 1 < LISTLETTER.length && idLettre + 1 >= 0
					&& idCol < LISTNUMBER.length && idCol >= 1) {
				possibleDests[2] = "" + LISTLETTER[idLettre + 1] + LISTNUMBER[idCol];
			} else {
				// Sinon renvoie la case actuelle
				possibleDests[2] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol];
			}
		}
		// Vérifie les possibleDests pour le joueur Bleu
		else {
			// Vérifie si [idLettre-1|idCol] existe
			if (idLettre - 1 < LISTLETTER.length && idLettre - 1 >= 0
					&& idCol < LISTNUMBER.length && idCol >= 0) {
				possibleDests[0] = "" + LISTLETTER[idLettre - 1] + LISTNUMBER[idCol];
			} else {
				// Sinon renvoie la case actuelle
				possibleDests[0] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol];
			}

			// Vérifie si [idLettre-1|idCol-1] existe
			if (idLettre - 1 < LISTLETTER.length && idLettre - 1 >= 0
					&& idCol + 1 < LISTNUMBER.length && idCol + 1 >= 0) {
				possibleDests[1] = "" + LISTLETTER[idLettre - 1] + LISTNUMBER[idCol + 1];
			} else {
				// Sinon renvoie la case actuelle
				possibleDests[1] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol];
			}

			// Vérifie si [idLettre|idCol+1] existe
			if (idLettre < LISTLETTER.length && idLettre >= 0
					&& idCol + 1 < LISTNUMBER.length && idCol + 1 >= 0) {
				possibleDests[2] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol + 1];
			} else {
				// Sinon renvoie la case actuelle
				possibleDests[2] = "" + LISTLETTER[idLettre] + LISTNUMBER[idCol];
			}
		}

		return possibleDests;
	}

	/**
	 * Affiche l'interface graphique
	 */
	void AfficheGUI() {

		int i;
		double x = 0;
		double y = 0;
		String lc = "";
		String playerImg = "player.png";

		if (CurrentModePlay == ModeJeuPlay.PVP) {
			StdDraw.picture(-8, -5, playerImg, 4, 7);
			StdDraw.picture(8, 5, "player2.png", 4, 7);
		} else if (CurrentModePlay == ModeJeuPlay.PVIA) {
			StdDraw.picture(-8, -5, playerImg, 4, 7);
			StdDraw.picture(8, 5, "ia1.png", 4, 7);
		} else if (CurrentModePlay == ModeJeuPlay.PVIA2) {
			StdDraw.picture(-8, -5, playerImg, 4, 7);
			StdDraw.picture(8, 5, "ia2.png", 4, 7);
		} else if (CurrentModePlay == ModeJeuPlay.IAVIA2) {
			StdDraw.picture(-8, -5, "ia1.png", 4, 7);
			StdDraw.picture(8, 5, "ia2.png", 4, 7);
		}

		x = -5;
		y = 3;
		i = 0;
		for (int j = 4; j < 8; j++, x += 1.75, y += 1) {
			lc = "" + LISTLETTER[i] + LISTNUMBER[j];
			xCenterCaseGUi.add(x);
			yCenterCaseGUi.add(y);
			caseGUI.add(lc);

			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x = -5;
		y = 1;
		i++;
		for (int j = 3; j < 8; j++, x += 1.75, y += 1) {
			lc = "" + LISTLETTER[i] + LISTNUMBER[j];
			xCenterCaseGUi.add(x);
			yCenterCaseGUi.add(y);
			caseGUI.add(lc);

			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x = -5;
		y = -1;
		i++;
		for (int j = 2; j < 8; j++, x += 1.75, y += 1) {
			lc = "" + LISTLETTER[i] + LISTNUMBER[j];
			xCenterCaseGUi.add(x);
			yCenterCaseGUi.add(y);
			caseGUI.add(lc);

			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x = -5;
		y = -3;
		i++;
		for (int j = 1; j < 8; j++, x += 1.75, y += 1) {
			lc = "" + LISTLETTER[i] + LISTNUMBER[j];
			xCenterCaseGUi.add(x);
			yCenterCaseGUi.add(y);
			caseGUI.add(lc);

			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x = -3.25;
		y = -4;
		i++;
		for (int j = 1; j < 7; j++, x += 1.75, y += 1) {
			lc = "" + LISTLETTER[i] + LISTNUMBER[j];
			xCenterCaseGUi.add(x);
			yCenterCaseGUi.add(y);
			caseGUI.add(lc);

			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x = -1.5;
		y = -5;
		i++;
		for (int j = 1; j < 6; j++, x += 1.75, y += 1) {
			lc = "" + LISTLETTER[i] + LISTNUMBER[j];
			xCenterCaseGUi.add(x);
			yCenterCaseGUi.add(y);
			caseGUI.add(lc);

			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		x = 0.25;
		y = -6;
		i++;
		for (int j = 1; j < 5; j++, x += 1.75, y += 1) {
			lc = "" + LISTLETTER[i] + LISTNUMBER[j];
			xCenterCaseGUi.add(x);
			yCenterCaseGUi.add(y);
			caseGUI.add(lc);

			drawPion(x, y, state[i][j]);
			drawHega(x, y, lc);
		}

		StdDraw.show();
	}

	/**
	 * Dessine un hexagone et peut écrire son nom.
	 *
	 * @param x  coordonée x du centre de l'hexagone
	 * @param y  coordonée y du centre de l'hexagone
	 * @param lc nom de la case (A1)
	 */
	void drawHega(double x, double y, String lc) {
		// Défini la taille du crayon
		StdDraw.setPenRadius(0.002);

		// Dessine 6 ligne dans un cercle pour former un hexagone
		for (int i = 0; i < 6; i++) {
			StdDraw.line(x + Math.cos(i * ANGLE_STEP), y + Math.sin(i * ANGLE_STEP), x + Math.cos((i + 1) * ANGLE_STEP),
					y + Math.sin((i + 1) * ANGLE_STEP));
		}

		// Défini la taille du crayon
		StdDraw.setPenRadius(0.001);
		// StdDraw.text(x, y, lc);
	}

	/**
	 * Dessine un pion dans sur le plateau
	 *
	 * @param x     coordonée x du centre du pion
	 * @param y     coordonée y du centre du pion
	 * @param value en fonction de quel joueur joue (R/B)
	 * @param lc    nom de la case (A1)
	 */
	void drawPion(double x, double y, char value) {
		// Choisis les action en fonction du joueur
		switch (value) {
			case 'B':
				// StdDraw.setPenColor(StdDraw.RED);
				// StdDraw.filledCircle(x, y, radiusPion);
				// StdDraw.setPenColor(StdDraw.BLACK);

				// Choisis quel joueur joue
				if (CurrentModePlay == ModeJeuPlay.IAVIA2) {
					StdDraw.picture(x, y, "pionIA.png", 1.5, 1.5);
				} else {
					StdDraw.picture(x, y, "pionPlayer.png", 1.5, 1.5);
				}
				break;
			case 'R':
				// StdDraw.setPenColor(StdDraw.BLUE);
				// StdDraw.filledCircle(x, y, radiusPion);
				// StdDraw.setPenColor(StdDraw.BLACK);

				// Choisis quel joueur joue
				if (CurrentModePlay == ModeJeuPlay.PVP) {
					StdDraw.picture(x, y, "pionPlayer2.png", 1.5, 1.5);
				} else if (CurrentModePlay == ModeJeuPlay.PVIA) {
					StdDraw.picture(x, y, "pionIA.png", 1.5, 1.5);
				} else {
					StdDraw.picture(x, y, "pionIA2.png", 1.5, 1.5);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Affiche l'interface graphique
	 */
	void AfficheConsole() {
		// Initialisation des variables pour parcourire le tableau
		int i, j, k, space;

		// Initialisation des variables pour stocker le numéro et la lettre de la case.
		char letterCase, numberCase;

		// Parcours les lignes du tableau en partant du haut
		for (i = 0; i < state.length; i++) {

			// Ajout d'espace pour la partie haute du losange
			for (space = state.length - 1; space >= i; space--) {
				System.out.print("  ");
			}

			// Parcours des diagonales du haut à droite vers la diagonale centrale
			for (j = 0, k = state.length - i; j < 1 + i; j++, k++) {

				// Nomination & Numeration des cases
				letterCase = LISTLETTER[j];
				numberCase = LISTNUMBER[k];

				// Affichage de la couleur de la case du tableau state au rang [j][k] en
				// fonction de sa valeur
				// + Nomination des case letterCase + NumberCase. exemple => B2
				switch (state[j][k]) {
					case VIDE:
						System.out
								.print(ConsoleColors.WHITE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);
						break;
					case 'R':
						System.out.print(ConsoleColors.RED_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);
						break;
					case 'B':
						System.out.print(ConsoleColors.BLUE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);
						break;
					case '-':
						System.out.print("  ");
						break;
					default:
						break;
				}
				// Ajout des espaces entre les cases
				System.out.print("  ");
			}
			// Retour ligne affichage du plateau
			System.out.println();
		}

		// Parcours les lignes du tableau en partant du haut
		for (i = 0; i < state.length - 1; i++) {

			// Ajout d'espace pour la partie basse du losange
			for (space = 0; space - 1 <= i; space++) {
				System.out.print("  ");
			}

			// Parcours des diagonales de la diagonale centrale jusqu'à en bas à droite
			for (j = 1 + i, k = 1; k < state.length - i; j++, k++) {

				// Nomination & Numeration des cases
				letterCase = LISTLETTER[j];
				numberCase = LISTNUMBER[k];

				// Affichage de la couleur de la case du tableau state au rang [j][k] en
				// fonction de ca valeur
				// + Nomination des case letterCase + NumberCase. exemple => B2
				switch (state[j][k]) {
					case VIDE:
						System.out
								.print(ConsoleColors.WHITE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);
						break;
					case 'R':
						System.out.print(ConsoleColors.RED_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);
						break;
					case 'B':
						System.out.print(ConsoleColors.BLUE_BACKGROUND + letterCase + numberCase + ConsoleColors.RESET);
						break;
					case '-':
						System.out.print("  ");
					default:
						;
				}

				// Ajout des espaces entre les cases
				System.out.print("  ");
			}
			// Retour ligne affichage du plateau
			System.out.println();
		}
	}

	/**
	 * Choisis entre l'affichage graphique/console en fonction du choix du joueur
	 */
	void affiche() {
		if (CurrentModeAffiche == ModeJeuAffiche.GUI) {
			// Affiche l'interface graphique
			AfficheGUI();
		} else if (CurrentModeAffiche == ModeJeuAffiche.CONSOLE) {
			// Affiche dans la console
			AfficheConsole();
		}
	}

	/**
	 * Joue un tour IA naive (NON FINI)
	 *
	 * @param couleur couleur du pion à jouer
	 * @return tableau contenant la position de départ et la destination du pion à
	 *         jouer.
	 */
	String[] jouerIA(char couleur) {

		// Initialisation variable source
		String src = "";
		// Initialisation du tableau contenant le mouvement à faire
		String[] move = new String[2];

		// Initialisation de la liste des case que on peux bouger
		List<String> listCase = new ArrayList<>();
		// Initalisation de la liste que cette case peux faire
		List<String> listMove = new ArrayList<>();

		// Initialisation des index des case dans les liste
		int indexCase = 0;
		int indexMove = 0;

		// Parcours du tableau en ligne
		for (int i = 0; i < state.length; i++) {
			// Parcours du tableau en column
			for (int j = 0; j < state[i].length; j++) {
				// Recherche les case qui corresponde à la couleur du joueur
				if (state[i][j] == couleur) {
					// Prends comme valeur le nom de la case (A1)
					src = "" + LISTLETTER[i] + LISTNUMBER[j];
					// Recherche les déplacement possible de cette case
					String[] possibleDests = possibleDests(couleur, i, j);

					// Parcours le tableau des déplacement possible
					for (int k = 0; k < possibleDests.length; k++) {
						// Vérifie si le déplacement est possbile
						if (deplace(couleur, src, possibleDests[k], ModeMvt.SIMU) == Result.OK) {
							// ajouter le nom de la case source à la liste des case source
							listCase.add(src);
						}
					}
				}
			}
		}

		// Initialise une liste des case que on peux déplacer en suprrimant les doublons
		List<String> listCaseVerif = listCase.stream().distinct().collect(Collectors.toList());
		// Prends une valeur au hasard dans la liste des case que on peux bouger
		indexCase = (int) (Math.random() * listCaseVerif.size());

		// Stock dans le tableau du déplacement à faire le nom de la case source
		move[0] = listCaseVerif.get(indexCase);

		// Donne les coordonnée de cette case
		int i = setCo('L', move[0].charAt(0));
		int j = setCo('N', move[0].charAt(1));

		// Recherche les déplacement possible pour cette case
		String[] possibleDests = possibleDests(couleur, i, j);

		// Parcours le tableau des déplacement possible
		for (int k = 0; k < possibleDests.length; k++) {
			// Vérifie si le déplacement est possible
			if (deplace(couleur, move[0], possibleDests[k], ModeMvt.SIMU) == Result.OK) {
				// Ajoute le déplacement à la liste des déplacement possible
				listMove.add(possibleDests[k]);
			}
		}

		// Prends uen valeur au hards dans la liste des movement possible
		indexMove = (int) (Math.random() * listMove.size());

		// Stock dans le tableau du déplacement à faire le nom de la case de destination
		move[1] = listMove.get(indexMove);

		return move;

	}

	/**
	 * Joue un tour IA Stratégique
	 *
	 * @param couleur couleur du pion à jouer
	 * @return tableau contenant la position de départ et la destination du pion à
	 *         jouer.
	 */
	String[] jouerIA2(char couleur) {
		String[] move = new String[2];

		move[0] = bestSrc(couleur);
		move[1] = bestDst(move[0], couleur);

		System.out.println(move[0]);
		System.out.println(move[1]);

		return move;
	}

	/**
	 * Cherche la meilleur case à jouer pour le joueur
	 *
	 * @param couleur la couleur actuelle du joueur
	 * @return la meilleur case à jouer pour le joueur.
	 */
	public String bestSrc(char couleur) {
		String src;
		int scCase = 0;
		int scCaseSave = 0;
		String srcSave = "A1";

		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				if (state[i][j] == couleur) {
					src = "" + LISTLETTER[i] + LISTNUMBER[j];
					String[] possibleDst = possibleDests(couleur, i, j);

					if (deplace(couleur, src, possibleDst[0], ModeMvt.SIMU) == Result.OK
							|| deplace(couleur, src, possibleDst[1], ModeMvt.SIMU) == Result.OK
							|| deplace(couleur, src, possibleDst[2], ModeMvt.SIMU) == Result.OK) {
						scCase = AttribSc(src, couleur, 0, 0);
					}

					if (scCase > scCaseSave) {
						srcSave = src;
						scCaseSave = scCase;
					}
				}
			}
		}
		return srcSave;
	}

	/**
	 * Cherche le meilleur déplacement à faire pour le joueur.
	 *
	 * @param src     la case depuis laquel nous allons jouer
	 * @param couleur la couleur actuelle du joueur
	 * @return la meilleur case à jouer pour le joueur.
	 */
	public String bestDst(String src, char couleur) {
		String dst;
		String dstSave = "A1";
		int scDst = 0;
		int scDstSave = 0;

		int x = setCo('L', src.charAt(0));
		int y = setCo('N', src.charAt(1));

		String[] possibleDest = possibleDests(couleur, x, y);
		for (int i = 0; i < possibleDest.length; i++) {
			if (deplace(couleur, src, possibleDest[i], ModeMvt.SIMU) == Result.OK) {
				dst = possibleDest[i];
				scDst = AttribSc(dst, couleur, 0, 1);

				if (scDst > scDstSave) {
					scDstSave = scDst;
					dstSave = dst;
				}
			}
		}
		return dstSave;
	}

	/**
	 * Attribue un score à la case actuelle
	 *
	 * @caseName la case dont on cherche le score
	 * @param couleur la couleur actuelle du joueur
	 * @param Sc      le score actuelle
	 * @param index   le niveau de profondeur auquel nous allons rechercher.
	 * @return Le score actuelle de la case.
	 */
	public int AttribSc(String caseName, char couleur, int Sc, int index) {

		if (index > 4) {
			return Sc;
		}

		int x = setCo('L', caseName.charAt(0));
		int y = setCo('N', caseName.charAt(1));

		String[] possibleDest = possibleDests(couleur, x, y);

		for (int i = 0; i < possibleDest.length; i++) {
			String src = possibleDest[i];
			Sc += addSc(couleur, x, y, index);
			Sc = AttribSc(src, couleur, Sc, index + 1);
		}
		return Sc;
	}

	/**
	 * Attribue un score à la case (NON FINI)
	 *
	 * @param couleur couleur du pion à jouer
	 * @param x       coordonnée x de la case
	 * @param y       coordonnée y de la case
	 * @param index   coéficient de force à appliquer au score de la case
	 *
	 * @return ScL nouveau score de la case
	 **/
	public double addSc(char couleur, int x, int y, int index) {

		double Sc = 0;
		if (couleur == 'R') {
			if (index == 0) {
				switch (state[x][y]) {
					case 'R':
						Sc += 10000;
						break;
					case 'B':
						Sc += 100;
						break;
					case '-':
						Sc += 1000;
						break;
					case '.':
						Sc += 0;
						break;
					default:
						Sc += 0;
				}
				return Sc;
			}

			// Score Case devant
			switch (state[x][y]) {
				case 'R':
					Sc += 1000.0 / index;
					break;
				case 'B':
					Sc += 10.0 / index;
					break;
				case '.':
					Sc += 1.0 / index;
					break;
				case '-':
					Sc += 100.0 / index;
					break;
				default:
					Sc += 0;
			}
		} else if (couleur == 'B') {
			// Cas particulier L0
			if (index == 0) {
				switch (state[x][y]) {
					case 'R':
						Sc += 100;
						break;
					case 'B':
						Sc += 10000;
						break;
					case '-':
						Sc += 1000;
						break;
					case '.':
						Sc += 0;
						break;
					default:
						Sc += 0;
				}
				return Sc;
			}

			// Score Case devant
			switch (state[x][y]) {
				case 'R':
					Sc += 10.0 / index;
					break;
				case 'B':
					Sc += 1000.0 / index;
					break;
				case '.':
					Sc += 1.0 / index;
					break;
				case '-':
					Sc += 100.0 / index;
					break;
				default:
					Sc += 0;
			}
		}
		return Sc;
	}

	/**
	 * Ecrit le résumer de la partie dans un fichier CSV
	 */
	public static void printGame() {

		// Crée le nom du fichier trace de partie
		int numDoc = 0;
		File file;

		// Recherche le dernier nom de fichier pour crée le suivant
		do {
			file = new File("StuckWin_" + numDoc++ + ".csv");
		} while (file.exists());

		// Crée le fichier trace de partie
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Ecrit le résumer de la partie dans le fichier trace
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

			// Ecrit les paramètre
			bufferedWriter.write("StuckWin");
			bufferedWriter.newLine();
			bufferedWriter.write("Groupe 40 : , Berkrouber Benjamin , Taskin Semih");
			bufferedWriter.newLine();
			bufferedWriter.write("Joueur, Src, Dest, Etats");
			bufferedWriter.write("Gagnant");

			// Ecrit les coup ainsi que leur statue dans le fichier trace
			for (int i = 0; i < coup.size(); i++) {
				bufferedWriter.write(coup.get(i));
				bufferedWriter.newLine();
			}

			// Ecrit le gagnant de la partie
			bufferedWriter.write(win.get(0));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cherche le nom de la case en fonction de l'endroit cliquer
	 *
	 * @return case le nom de la case.
	 */
	String AttribPion() {

		// Initialise le nom de la case à NO
		String caseClick = "NO";

		// Initialisation des x et y
		double x = 0;
		double y = 0;

		// Recherche le x et y de l'endroit cliquer
		while (!StdDraw.isMousePressed()) {
			x = StdDraw.mouseX();
			y = StdDraw.mouseY();
		}

		// Nomme la case cliquer
		for (int i = 0; i < caseGUI.size(); i++) {
			if (x < xCenterCaseGUi.get(i) + RADPUISPION && x > xCenterCaseGUi.get(i) - RADPUISPION
					&& y < yCenterCaseGUi.get(i) + RADPUISPION && y > yCenterCaseGUi.get(i) - RADPUISPION) {
				caseClick = caseGUI.get(i);
			}
		}

		// Renvoie la case cliquer
		return caseClick;
	}

	/**
	 * Permet à un joueur de jouer tactile sur le GUI
	 *
	 * @return tableau {src, dst}
	 */
	String[] playerPLay(char couleur) {
		String[] move = new String[2];
		System.out.println("Mouvement " + couleur);

		StdDraw.pause(600);
		affiche();

		String src = AttribPion();
		StdDraw.pause(200);
		move[0] = src;

		int x = setCo('L', src.charAt(0));
		int y = setCo('N', src.charAt(1));
		if (state[x][y] != VIDE) {
			drawPossibleDest(src, couleur);
			affiche();
		}

		String dst = AttribPion();
		StdDraw.pause(200);
		move[1] = dst;

		System.out.println(src + "->" + dst);
		coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));

		return move;
	}

	/**
	 * Dessine les cases de déplacements possibles
	 *
	 * @param Case    la case d'entrée
	 * @param couleur la couleur du pion à jouer
	 */
	void drawPossibleDest(String Case, char couleur) {
		int x = setCo('L', Case.charAt(0));
		int y = setCo('N', Case.charAt(1));

		String[] possibleDest = possibleDests(couleur, x, y);

		for (int i = 0; i < possibleDest.length; i++) {
			if (!possibleDest[i].equals(Case) && (caseGUI.indexOf(possibleDest[i]) != -1)) {
				int xDest = setCo('L', possibleDest[i].charAt(0));
				int yDest = setCo('N', possibleDest[i].charAt(1));

				if (state[xDest][yDest] == VIDE) {

					int a = caseGUI.indexOf(possibleDest[i]);

					double x1 = xCenterCaseGUi.get(a);
					double y1 = yCenterCaseGUi.get(a);

					StdDraw.setPenColor(StdDraw.RED);
					StdDraw.filledCircle(x1, y1, (RADPUISPION / 1.5));
					StdDraw.setPenColor(StdDraw.BLACK);
				}
			}
		}
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
		String[] move;

		if (CurrentModeAffiche == ModeJeuAffiche.GUI) {
			if (CurrentModePlay == ModeJeuPlay.PVP) {
				switch (couleur) {
					case 'B':
						move = playerPLay(couleur);
						src = move[0];
						dst = move[1];
						StdDraw.pause(300);
						break;
					case 'R':
						move = playerPLay(couleur);
						src = move[0];
						dst = move[1];
						StdDraw.pause(300);
						break;
					default:
						;
				}
			} else if (CurrentModePlay == ModeJeuPlay.PVIA) {
				switch (couleur) {
					case 'B':
						move = playerPLay(couleur);
						src = move[0];
						dst = move[1];
						StdDraw.pause(200);
						break;
					case 'R':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					default:
						;
				}
			} else if (CurrentModePlay == ModeJeuPlay.PVIA2) {
				switch (couleur) {
					case 'B':
						move = playerPLay(couleur);
						src = move[0];
						dst = move[1];
						StdDraw.pause(200);
						break;
					case 'R':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA2(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					default:
						;
				}
			} else if (CurrentModePlay == ModeJeuPlay.IAVIA2) {
				switch (couleur) {
					case 'B':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA2(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					case 'R':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA2(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					default:
						;
				}
			}
		} else {

			if (CurrentModePlay == ModeJeuPlay.PVP) {
				switch (couleur) {
					case 'B':
						System.out.println("Mouvement " + couleur);
						src = input.next();
						dst = input.next();
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					case 'R':
						System.out.println("Mouvement " + couleur);
						src = input.next();
						dst = input.next();
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					default:
						;
				}
			} else if (CurrentModePlay == ModeJeuPlay.PVIA) {
				switch (couleur) {
					case 'B':
						System.out.println("Mouvement " + couleur);
						src = input.next();
						dst = input.next();
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					case 'R':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					default:
						;
				}
			} else if (CurrentModePlay == ModeJeuPlay.PVIA2) {
				switch (couleur) {
					case 'B':
						System.out.println("Mouvement " + couleur);
						src = input.next();
						dst = input.next();
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));

						break;
					case 'R':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA2(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					default:
						break;
				}
			} else if (CurrentModePlay == ModeJeuPlay.IAVIA2) {
				switch (couleur) {
					case 'B':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					case 'R':
						System.out.println("Mouvement " + couleur);
						mvtIa = jouerIA2(couleur);
						src = mvtIa[0];
						dst = mvtIa[1];
						System.out.println(src + "->" + dst);
						coup.add(couleur + "," + src + "," + dst + "," + deplace(couleur, src, dst, ModeMvt.SIMU));
						break;
					default:
						break;
				}
			}
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
		for (int i = 0; i < state.length; i++) {

			// Parcours le tableau par column
			for (int j = 0; j < state[i].length; j++) {

				// Verifique si la case à la même valeur que la variable couleur 'R' OU 'B'
				if (state[i][j] == couleur) {

					// Nomination et numération de la case src
					String src = "" + LISTLETTER[i] + LISTNUMBER[j];

					// Cherche les destinations possibles de cette case
					String[] possibleDests = possibleDests(couleur, i, j);

					// Parcours le tableau de possibilité de déplacement
					for (int k = 0; k < possibleDests.length; k++) {

						// Vérifie si l'un des déplacement est possible
						if (deplace(couleur, src, possibleDests[k], ModeMvt.SIMU) == Result.OK) {
							// Si un déplacement est possible return 'N' => partie non fini.
							return 'N';
						}
					}
				}
			}
		}

		// Renvoie la couleur du joueur ne pouvant plus faire de déplacement
		return couleur;
	}

	/**
	 * Affiche les différents modes de jeu
	 */
	public static void printHelpGame() {
		System.out.println();
		System.out.println(
				"#############################################StuckWin Game#############################################");
		System.out.println();
		System.out.println("\t [gameModePlay]   (0: PvP, 1: PvIA, 2: PvIA12, 3: IAvIA2 ,default: PvP)");
		System.out.println("\t [gameModeAffiche]  (0: Console, 1: GUI ,default: GUI)");
		System.out.println("\t [gameModeSave]     (0: NO, 1: YES ,default: NO)");
		System.out.println("");
		System.out.println(
				"#######################################################################################################");
		System.out.println("");

	}

	public static void main(String[] args) {

		StuckWin jeu = new StuckWin();

		printHelpGame();

		if (args.length > 0) {
			int n = Integer.parseInt(args[0]);
			if (n == 1) {
				System.out.println("Player vs IA");
				jeu.CurrentModePlay = ModeJeuPlay.PVIA;
			} else if (n == 2) {
				System.out.println("Player vs IA2");
				jeu.CurrentModePlay = ModeJeuPlay.PVIA2;
			} else {
				System.out.println(" 2 Séléctionner un mode de jeu: ");
				int ModeJeuPlayValue = input.nextInt();

				switch (ModeJeuPlayValue) {
					case 0:
						jeu.CurrentModePlay = ModeJeuPlay.PVP;
						break;
					case 1:
						jeu.CurrentModePlay = ModeJeuPlay.PVIA;
						break;
					case 2:
						jeu.CurrentModePlay = ModeJeuPlay.PVIA2;
						break;
					case 3:
						jeu.CurrentModePlay = ModeJeuPlay.IAVIA2;
						break;
					default:
						jeu.CurrentModeAffiche = ModeJeuAffiche.CONSOLE;

				}
			}
		} else {
			System.out.println("Séléctionner un mode de jeu: ");
			int ModeJeuPlayValue = input.nextInt();

			switch (ModeJeuPlayValue) {
				case 0:
					jeu.CurrentModePlay = ModeJeuPlay.PVP;
					break;
				case 1:
					jeu.CurrentModePlay = ModeJeuPlay.PVIA;
					break;
				case 2:
					jeu.CurrentModePlay = ModeJeuPlay.PVIA2;
					break;
				case 3:
					jeu.CurrentModePlay = ModeJeuPlay.IAVIA2;
					break;
				default:
					jeu.CurrentModePlay = ModeJeuPlay.PVIA2;
			}

		}

		System.out.println("Séléctionner un mode d'affichage :");
		int ModeJeuAfficheValue = input.nextInt();

		System.out.println("Voulez-vous sauvegarder la partie .");
		int ModeJeuSaveValue = input.nextInt();

		switch (ModeJeuAfficheValue) {
			case 0:
				jeu.CurrentModeAffiche = ModeJeuAffiche.CONSOLE;
				break;
			case 1:
				jeu.CurrentModeAffiche = ModeJeuAffiche.GUI;
				break;
			default:
				jeu.CurrentModeAffiche = ModeJeuAffiche.CONSOLE;
		}

		switch (ModeJeuSaveValue) {
			case 0:
				jeu.CurrentModeSave = ModeJeuSave.NO;
				break;
			case 1:
				jeu.CurrentModeSave = ModeJeuSave.YES;
				break;
			default:
				jeu.CurrentModeSave = ModeJeuSave.NO;
		}

		String src = "";
		String dest;
		String[] reponse;
		Result status;
		char partie = 'N';
		char curCouleur = jeu.joueurs[0];
		char nextCouleur = jeu.joueurs[1];
		char tmp;
		int cpt = 0;

		if (jeu.CurrentModeAffiche == ModeJeuAffiche.GUI) {
			StdDraw.setXscale(-10.5, 10.5);
			StdDraw.setYscale(-10.5, 10.5);
			StdDraw.enableDoubleBuffering();
			StdDraw.setTitle("StuckWin");
			StdDraw.picture(0, 0, "back.jpg", 25, 25);
		}

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

				if (jeu.CurrentModeAffiche == ModeJeuAffiche.GUI) {
					StdDraw.text(0, -9, "status : " + status + " partie : " + partie);
					jeu.affiche();
					StdDraw.clear();
					StdDraw.picture(0, 0, "back.jpg", 25, 25);
				}

			} while (status != Result.OK && partie == 'N');
			tmp = curCouleur;
			curCouleur = nextCouleur;
			nextCouleur = tmp;
			cpt++;
		} while (partie == 'N'); // TODO affiche vainqueur
		jeu.affiche();
		System.out.printf("Victoire : " + partie + " (" + (cpt / 2) + " coups)");

		if (jeu.CurrentModeSave == ModeJeuSave.YES) {
			printGame();
			System.out.println("Vous pouvez retrouver le récapitulatif de votre partie dans le dossier StuckWin");
		}
	}
}