package odomo;

import java.util.Scanner;

/**
 * Gestion de la partie Chauffage.
 */
class Chauffage {

    /**
     * Température souhaitée en mode économique.
     */
    static int temperEco;
    
    /**
     * Température souhaitée en mode normal.
     */
    static int temperNormal;

    /**
     * Premier créneau du jour en mode normal.
     */
    static int[][] creneau1;
    
    /**
     * Deuxième créneau du jour en mode normal.
     */
    static int[][] creneau2;
    
    /**
     * Initialiser les données de chauffage.
     */
    static void initialiser() {
        temperNormal = 19;
        temperEco = 16;
        creneau1 = new int[][]{{6, 9}, {6, 9}, {6, 22}, {6, 9}, {6, 9}, {7, 23}, {7, 23}};
        creneau2 = new int[][]{{17, 22}, {17, 22}, {1, 0}, {17, 22}, {17, 22}, {1, 0}, {1, 0}};
    }

    /**
     * Matrice des créneaux en mode normal, pour l'histogramme.
     *
     * @return la matrice des créneaux
     */
    static boolean[][] matriceCreneaux() {
        boolean[][] matrice = new boolean[8][24];
        // Ligne 8 doit toujours etre vide
        for (int ligne = 0; ligne < matrice.length - 1; ligne++) {
            for (int col = 0; col < matrice[ligne].length; col++) {
                matrice[ligne][col] = dansCreneauNormal(ligne, col);
            }
        }
        return matrice;
    }
    
    /**
     * Indique si une heure donnée (d'un jour donné) est dans un créneau en mode
     * normal.
     * 
     * @param jour le jour concerné (0 = lundi)
     * @param heure l'heure concernée
     * @return true si l'heure est en mode normal, false si mode économique
     */
    static boolean dansCreneauNormal(int jour, int heure) {
        return ((creneau1[jour][0] <= heure && creneau1[jour][1] >= heure) 
                || (creneau2[jour][0] <= heure && creneau2[jour][1] >= heure ));
    }
    
    /**
     * Indique la température souhaitée sur un horaire donné.
     * 
     * @param jour le jour considéré (0 = lundi)
     * @param heure l'heure considérée
     * @return la température souhaitée à cet horaire
     */
    static int temperatureSouhaitee(int jour, int heure) {
        int temp = temperEco;
        if (dansCreneauNormal(jour, heure)) {
            temp =  temperNormal;
        }
        return temp;
    }

    /**
     * Procédure de saisie des créneaux de chauffage.
     */
    static void saisieCreneaux() {
        System.out.println("Saisie des créneaux en mode normal d'un jour donné");
        System.out.println("  par exemple : mar;7;20 pour un seul créneau le mardi, de 7h à 20h");
        System.out.println("                jeu;6;8;18;21 pour deux créneaux le jeudi, de 6h à 8h et de 18h à 21h");
        Scanner sc = new Scanner(System.in);
        String saisie;
        boolean saisieCorrecte;
        do {
            System.out.print("Créneaux d'un jour : ");
            saisie = sc.next();
            saisieCorrecte = traitementSaisieCreneaux(saisie);
        } while (!saisieCorrecte);
    }

    /**
     * Traite la saisie de créneaux par l'utilisateur.
     *
     * @param saisie la saisie de l'utilisateur
     * @return true ssi la saisie a été correcte
     */
    static boolean traitementSaisieCreneaux(String saisie) {
        boolean correct = saisie != null;
        String[] champs = null;
        if (correct) {
            champs = saisie.split(";");
            correct &= champs.length == 3 || champs.length == 5;
            if (!correct) {
                System.err.println("Format incorrect : 3 ou 5 champs separes " 
                        + "par des points-virgules sont attendus, " 
                        + champs.length + " ont été saisis.");
            }
        }
        if (correct) {
            correct &= Odomo.numeroJour(champs[0]) >= 0;
            if (!correct) {
                System.err.println("Nom de jour incorrect : " + champs[0] + ".");
            }
        }
        int creneau1debut = -1;
        if (correct) {
            creneau1debut = heureCreneau(champs[1]);
            correct = creneau1debut >= 0;
        }
        int creneau1fin = -1;
        if (correct) {
            creneau1fin = heureCreneau(champs[2]);
            correct = creneau1fin >= 0;
        }
        if (correct) {
            correct &= (creneau1debut <= creneau1fin) || (creneau1debut == 1 && creneau1fin == 0);
            if (!correct) {
                System.err.println("Créneau incorrect : l'heure de début doit " 
                        + "précéder (ou égaler) l'heure de fin " 
                        + "(ou choisir le créneau 1h-0h pour un créneau vide).");
            }
        }
        int creneau2debut = -1;
        int creneau2fin = -1;
        if (correct && champs.length == 5) {
            creneau2debut = heureCreneau(champs[3]);
            correct = creneau2debut >= 0;
            if (correct) {
                creneau2fin = heureCreneau(champs[4]);
                correct = creneau2fin >= 0;
            }
            if (correct) {
                correct &= (creneau2debut <= creneau2fin) || (creneau2debut == 1 && creneau2fin == 0);
                if (!correct) {
                    System.err.println("Créneau incorrect : l'heure de début doit " 
                            + "précéder (ou égaler) l'heure de fin " 
                            + "(ou choisir le créneau 1h-0h pour un créneau vide).");
                }
            }
        }
        if (correct) {
            int numJour = Odomo.numeroJour(champs[0]);
            Chauffage.creneau1[numJour][0] = creneau1debut;
            Chauffage.creneau1[numJour][1] = creneau1fin;
            if (champs.length == 5) {
                Chauffage.creneau2[numJour][0] = creneau2debut;
                Chauffage.creneau2[numJour][1] = creneau2fin;
            } else {
                Chauffage.creneau2[numJour][0] = 1;
                Chauffage.creneau2[numJour][1] = 0;
            }
        }
        return correct;
    }

    /**
     * Récupère l'heure d'un créneau donné sous forme de chaîne.
     *
     * @param chaineHeure l'heure sous forme de chaîne
     * @return l'heure sous forme d'entier (-1 si incorrecte)
     */
    static int heureCreneau(String chaineHeure) {
        int heure;
        try {
            heure = Integer.parseInt(chaineHeure);
        } catch (NumberFormatException e) {
            System.err.println("L'heure de créneau n'est pas un entier : " + chaineHeure);
            heure = -1;
        }
        if (heure > 23) {
            System.err.println("L'heure doit être comprise entre 0 et 23 " 
                    + "(inclus), au lieu de : " + chaineHeure + ".");
            heure = -1;
        }
        return heure;
    }
    
    /**
     * Fusion des créneaux qui s'intersectent (ou se jouxtent).
     */
    static void nettoyageCreneaux() {
        for (int jour = 0; jour < 7; jour++) {
            if (creneau1[jour][0] <= creneau1[jour][1]) {
                if (creneau2[jour][0] <= creneau2[jour][1]) {
                    if (creneau1[jour][1] == creneau2[jour][0]) {
                        creneau1[jour][1] = creneau2[jour][1];
                        creneau2[jour][0] = 1;
                        creneau2[jour][1] = 0;
                    }
                }
            } else {
                creneau1[jour][0] = 1;
                creneau1[jour][1] = 0;
            }
        }
    }
}
