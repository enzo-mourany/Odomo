package odomo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * L'écran permettant d'afficher les données.
 */
class Ecran {

    /**
     * L'écran avant remplissage avec les données.
     */
    static final String ECRAN_VIDE
            = "+----------------+-------------------------------------------------------------+\n"
            + "|HH:mm    JJ/MM  | Mode : MODEMODEMODE                                         |\n"
            + "|                |        HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH |\n"
            + "|intérieur :     | ZZZZZ0 ZONE0ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "| ttttt°C        | ZZZZZ1 ZONE1ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "|    yy% hygro   | ZZZZZ2 ZONE2ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "|                | ZZZZZ3 ZONE3ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "|extérieur :     | ZZZZZ4 ZONE4ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "| TTTTT° C       | ZZZZZ5 ZONE5ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "|    YY% hygro   | ZZZZZ6 ZONE6ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "|  PPPP  hPa     | ZZZZZ7 ZONE7ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ |\n"
            + "|  SSSS  W/m2 SOL|                                                             |\n"
            + "|                |       TITRE_HISTO_TITRE_HISTO_TITRE_HISTO_TITRE_HISTO  LVER |\n"
            + "|      o   o     |        0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 LLL0 |\n"
            + "|   o   NNN   o  |        1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 LLL1 |\n"
            + "|  o NOO   NEE o |        2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 LLL2 |\n"
            + "|  oOOO VVV EEEo |        3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 LLL3 |\n"
            + "|  o SOO   SEE o |        4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 LLL4 |\n"
            + "|   o   SSS   o  |        5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5 LLL5 |\n"
            + "|      o   o     |        6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 LLL6 |\n"
            + "|                |        7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 LLL7 |\n"
            + "|                |  LHOR LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL\\LLL8 |\n"
            + "|                |                                                             |\n"
            + "+----------------+-------------------------------------------------------------+";

    /**
     * Caractère affiché dans l'histogramme pour une case allumée.
     */
    final static char HISTO_ALLUME = '#';

    /**
     * Caractère affiché dans l'histogramme pour une case éteinte.
     */
    final static char HISTO_ETEINT = '_';

    /**
     * Type d'histogramme actuellement affiché (pluviométrie, température...).
     */
    static int typeHisto;

    /**
     * Constante pour le type d'histogramme "pluviométrie des 24 dernières
     * heures".
     */
    final static int TYPE_HISTO_PLUVIO_HEURE = 0;

    /**
     * Constante pour le type d'histogramme "chauffage".
     */
    final static int TYPE_HISTO_CHAUFFAGE = 1;

    /**
     * Constante pour le type d'histogramme "températures des 60 dernières
     * minutes".
     */
    final static int TYPE_HISTO_TEMPER_MINUTES = 2;

    /**
     * Affichage de l'écran principal.
     */
    static void ecranPrincipal() {
        System.out.println(insererDonnees(ECRAN_VIDE));
    }

    /**
     * Insère toutes les données sur l'écran.
     *
     * @param ecranVierge l'écran de départ, sans données
     * @return l'écran avec données
     */
    static String insererDonnees(String ecranVierge) {
        StringBuilder ecran = new StringBuilder(ecranVierge);
        insererDateEtHeure(ecran);
        insererMeteoImmediate(ecran);
        insererMode(ecran);
        insererDonneesZones(ecran);
        insererDonneesHistogramme(ecran);
        return ecran.toString();
    }

    /**
     * Insère la date et l'heure sur l'écran.
     *
     * @param ecran l'écran dans lequel insérer la date et l'heure
     */
    static void insererDateEtHeure(StringBuilder ecran) {
        insererUnChampDateHeure("HH", "HH", ecran);
        insererUnChampDateHeure("mm", "mm", ecran);
        insererUnChampDateHeure("dd", "JJ", ecran);
        insererUnChampDateHeure("MM", "MM", ecran);
    }

    /**
     * Insérer un champ de date ou d'heure sur l'écran.
     *
     * @param champSysteme nom du champ au format système
     * @param champEcran nom du champ sur l'écran
     * @param ecran l'écran à renseigner
     */
    static void insererUnChampDateHeure(String champSysteme, String champEcran,
            StringBuilder ecran) {
        LocalDateTime dateSysteme = LocalDateTime.now();
        DateTimeFormatter formatHeures = DateTimeFormatter.ofPattern(champSysteme);
        remplacerMotif(champEcran, dateSysteme.format(formatHeures), ecran, true);
    }

    /**
     * Insérer les données concernant les conditions météorologiques courantes.
     *
     * @param ecran l'écran dans lequel insérer les données
     */
    static void insererMeteoImmediate(StringBuilder ecran) {
        remplacerMotif("ttttt", String.format("%5.1f", Meteo.majTemperInt()), ecran, true);
        remplacerMotif("yy", String.format("%2.0f", Meteo.majHygroInt()), ecran, true);
        remplacerMotif("TTTTT", String.format("%5.1f", Meteo.majTemperExt()), ecran, true);
        remplacerMotif("YY", String.format("%2.0f", Meteo.majHygroExt()), ecran, true);
        remplacerMotif("PPPP", String.format("%4.0f", Meteo.majPression()), ecran, true);
        remplacerMotif("SSSS", String.format("%4.0f", Meteo.majEnsoleillement()), ecran, true);
        char insolation = Meteo.insolation() ? '+' : '-';
        remplacerMotif("SOL", "(" + insolation + ")", ecran, true);
        remplacerMotif("VVV", String.format("%3.0f", Meteo.majVitesseVent()), ecran, true);
        insererDirectionVent(ecran);
    }

    /**
     * Insérer les données concernant la direction du vent.
     *
     * @param ecran l'écran dans lequel insérer les données
     */
    static void insererDirectionVent(StringBuilder ecran) {
        String direction = Meteo.majDirectionVent();
        String[] champsDir = {"NNN", "NEE", "EEE", "SEE", "SSS", "SOO", "OOO",
            "NOO"};
        for (String champDir : champsDir) {
            if (champDir.startsWith(direction)) {
                remplacerMotif(champDir, " # ", ecran, true);
            } else {
                remplacerMotif(champDir, "   ", ecran, true);
            }
        }
    }

    /**
     * Insérer les données concernant le mode en cours.
     *
     * @param ecran l'écran dans lequel insérer les données
     */
    static void insererMode(StringBuilder ecran) {
        remplacerMotif("MODEMODEMODE", Odomo.mode, ecran, false);
    }

    /**
     * Insérer les données des différentes zones.
     *
     * @param ecran l'écran à compléter
     */
    static void insererDonneesZones(StringBuilder ecran) {
        String zs = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
        if (Odomo.MODE_JARDIN.equals(Odomo.mode)) {
            remplacerMotif("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH",
                    "Zones :", ecran, false);
            for (int ligne = 0; ligne < 8; ligne++) {
                remplacerMotif("ZZZZZ" + ligne, "Zone " + ligne, ecran, false);
                remplacerMotif("ZONE" + ligne + zs, "", ecran, false);
            }
        } else { // mode non jardin
            remplacerMotif("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH",
                    "", ecran, false);
            for (int ligne = 0; ligne < 8; ligne++) {
                remplacerMotif("ZZZZZ" + ligne, "", ecran, false);
                remplacerMotif("ZONE" + ligne + zs, "", ecran, false);
            }
        }
    }

    /**
     * Insérer les données de l'histogramme.
     *
     * @param ecran l'écran à compléter
     */
    static void insererDonneesHistogramme(StringBuilder ecran) {
        boolean[][] matriceHisto = matriceDonneesHisto();
        for (int ligne = 0; ligne < 8; ligne++) {
            remplacerMotif(motifLigneHisto(ligne),
                    histoLigne(ligne, matriceHisto[ligne]), ecran, true);
            remplacerMotif("LLL" + ligne, histoLegendeOrdonnee(ligne), ecran, true);
        }
        remplacerMotif("LLL8", histoLegendeOrdonnee(8), ecran, true);
        remplacerMotif("TITRE_HISTO_TITRE_HISTO_TITRE_HISTO_TITRE_HISTO",
                titreHisto(), ecran, false);
        remplacerMotif("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL",
                histoLegendeAbscisse(), ecran, true);
        remplacerMotif("LHOR", histoUniteAbscisse(), ecran, true);
        remplacerMotif("LVER", histoUniteOrdonnee(), ecran, true);
    }

    /**
     * Matrice des données à afficher dans l'histogramme, en fonction du type
     * d'histogramme.
     *
     * @return matrice des données à afficher dans l'histogramme
     */
    static boolean[][] matriceDonneesHisto() {
        boolean[][] matriceHisto;
        switch (Ecran.typeHisto) {
            case Ecran.TYPE_HISTO_PLUVIO_HEURE:
                matriceHisto = Meteo.matricePluvioHeure();
                break;
            case Ecran.TYPE_HISTO_TEMPER_MINUTES:
                matriceHisto = Meteo.matriceTemperMinutes();
                break;
            case Ecran.TYPE_HISTO_CHAUFFAGE:
                matriceHisto = Chauffage.matriceCreneaux();
                break;
            default:
                throw new UnsupportedOperationException(
                        "Type d'historique non supporté");
        }
        return matriceHisto;
    }

    static String titreHisto() {
        String titre = "";
        if (typeHisto == TYPE_HISTO_PLUVIO_HEURE) {
            titre = "Pluviométrie des 24 dernières heures :";
        } else if (typeHisto == TYPE_HISTO_TEMPER_MINUTES) {
            titre = "Température des 60 dernières minutes :";
        } else if (typeHisto == TYPE_HISTO_CHAUFFAGE) {
            titre = "Créneaux :";
        }
        return titre;
    }

    /**
     * Motif d'une ligne de l'histogramme.
     *
     * @param ligne numéro de la ligne
     * @return le motif de cette ligne
     */
    static String motifLigneHisto(int ligne) {
        StringBuilder motif = new StringBuilder();
        for (int colonne = 0; colonne < 24; colonne++) {
            motif.append(" ").append(ligne);
        }
        return motif.toString();
    }

    /**
     * Données à afficher dans l'histogramme, pour une ligne donnée.
     *
     * @param ligne numéro de la ligne à traiter
     * @param ligneMatrice ligne de la matrice booléenne contenant les données
     * @return la chaîne de caractères pour cette ligne
     */
    static String histoLigne(int ligne, boolean[] ligneMatrice) {
        StringBuilder donneesLigne = new StringBuilder();
        for (int col = 0; col < 24; col++) {
            donneesLigne
                    .append(" ")
                    .append(ligneMatrice[col] ? HISTO_ALLUME : HISTO_ETEINT);
        }
        return donneesLigne.toString();
    }

    /**
     * Unité de l'axe des abscisses (horizontal).
     *
     * @return l'unité de l'axe des abscisses à afficher
     */
    static String histoUniteAbscisse() {
        return "h";
    }

    /**
     * Légende de l'histogramme pour l'axe des abscisses.
     *
     * @return la légende de l'histogramme
     */
    static String histoLegendeAbscisse() {
        String legende = "";
        switch (typeHisto) {
            case TYPE_HISTO_PLUVIO_HEURE:
                legende = " -22 -20 -18 -16 -14 -12 -10  -8  -6  -4  -2   0";
                break;
            case TYPE_HISTO_TEMPER_MINUTES:
                legende = " -55 -50 -45 -40 -35 -30 -25 -20 -15 -10  -5   0";
                break;
            case TYPE_HISTO_CHAUFFAGE:
                legende = " 0   2   4   6   8  10  12  14  16  18  20  22  ";
                break;
            default:
                throw new UnsupportedOperationException(
                        "Légende non supportée pour ce type d'histogramme");
        }
        return legende;
    }

    /**
     * Unité de l'axe des ordonnées (vertical).
     *
     * @return l'unité de l'axe des ordonnées à afficher
     */
    static String histoUniteOrdonnee() {
        // TODO
        String unite = "";
        switch (typeHisto) {
            case TYPE_HISTO_PLUVIO_HEURE:
                unite = "mm";
                break;
            case TYPE_HISTO_TEMPER_MINUTES:
                unite = "°C";

                break;
            case TYPE_HISTO_CHAUFFAGE:
                unite = "jour";
                break;     
        }
        return unite;
    }

    /**
     * Légende de l'histogramme pour l'axe des ordonnées.
     *
     * @param ligne le numéro de ligne de l'histogramme
     * @return la légende de l'histogramme
     */
    static String histoLegendeOrdonnee(int ligne) {
        String legende = "";
        int valeur = -10000;
        switch(ligne) {
            case 0:
                valeur = (int) Meteo.valeurMax(Meteo.pluvioHeure);
                legende = Odomo.JOURS[ligne] + valeur;
                break;
            case 1:
            case 2:
            case 3:
                legende = Odomo.JOURS[ligne];
                break;   
            case 4:
                valeur = (int) Meteo.valeurMax(Meteo.pluvioHeure) / 2;
                legende = Odomo.JOURS[ligne] + valeur;
                break;
            case 5:
            case 6:
                legende = Odomo.JOURS[ligne];
                break; 
            case 7:
                legende = "";
                break;    
            case 8:
                valeur = 0;
                break;
        }
        if (valeur != -10000) {
            legende = "" + valeur;
        }
        return legende;
    }

    /**
     * Renvoie le numéro de ligne de l'histogramme correspondant à une valeur,
     * étant donné des valeurs min et max à afficher. Par exemple si on doit
     * afficher des valeurs de pluviométrie par heure, avec un maximum de 27.2mm
     * sur les 24 dernières heures, et qu'on s'intéresse à une heure où il a plu
     * 16mm, alors numLigne(16., 0., 27.2) renverra la ligne correspondant à 16
     * (entre 0 et 7, donc). Notez que pour la pluviométrie le minimum est
     * toujours à zéro sur l'histogramme.
     *
     * @param val valeur à représenter sur l'histogramme
     * @param valMin valeur minimale parmi toutes les colonnes
     * @param valMax valeur maximale parmi toutes les colonnes
     * @return ligne de l'histogramme correspondant à cette valeur
     */
    static int numLigne(double val, double valMin, double valMax) {
        return 7 - (int) (Math.round(7. * (val - valMin) / (valMax - valMin)));
    }

    /**
     * Remplace un motif (mot-clé) par une valeur dans un écran. Le motif et la
     * valeur doivent avoir la même longueur.
     *
     * @param motif le motif à remplacer
     * @param valeur la valeur à insérer à la place du motif
     * @param ecran l'écran dans lequel se trouve le motif
     * @param alignerADroite indique s'il faut aligner à droite (à gauche sinon)
     */
    static void remplacerMotif(String motif, String valeur, StringBuilder ecran,
            boolean alignerADroite) {
        if (motif.length() < valeur.length()) {
            // chaîne rognée à droite si trop longue
            valeur = valeur.substring(0, motif.length());
        }
        if (motif.length() > valeur.length()) {
            // chaîne complétée avec des espaces si trop vide
            String format = "%" + (alignerADroite ? "" : "-") + motif.length() + "s";
            valeur = String.format(format, valeur);
        }
        int index = ecran.indexOf(motif);
        if (index >= 0) {
            ecran.replace(index, index + motif.length(), valeur);
        }
    }
}
