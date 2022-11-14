package odomo;

import java.util.Arrays;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests portant sur la classe Meteo.
 */
public class MeteoTest {

    @Test
    public void testMajTemperInt() {
        for (int i = 0; i < 100; i++) {
            double temper = Meteo.majTemperInt();
            assertTrue(temper >= 18.0 && temper < 20.0);
        }
    }

    @Test
    public void testMajHygroInt() {
        for (int i = 0; i < 100; i++) {
            double hygro = Meteo.majHygroInt();
            assertTrue(hygro >= 30. && hygro < 60.);
        }
    }

    @Test
    public void testDecalageDroite() {
        double precision = .01;
        double[] tab1avant = {30., 2.5, 27.};
        double[] tab1apres = {30., 30., 2.5};
        Meteo.decalageDroite(tab1avant);
        Assert.assertArrayEquals(tab1apres, tab1avant, precision);
        double[] tab2avant = {-.3, 12.};
        double[] tab2apres = {-.3, -.3};
        Meteo.decalageDroite(tab2avant);
        Assert.assertArrayEquals(tab2apres, tab2avant, precision);
        double[] tab3avant = {4.7};
        double[] tab3apres = {4.7};
        Meteo.decalageDroite(tab3avant);
        Assert.assertArrayEquals(tab3apres, tab3avant, precision);
        double[] tab4avant = {};
        double[] tab4apres = {};
        Meteo.decalageDroite(tab4avant);
        Assert.assertArrayEquals(tab4apres, tab4avant, precision);
    }

    @Test
    public void testTemperaturesCoherentes() {
        // initialisation des données par minute
        Meteo.temperExtMinMinute = new double[60];
        Meteo.temperExtMoyMinute = new double[60];
        Meteo.temperExtMaxMinute = new double[60];
        double minHeureCourante = Float.MAX_VALUE;
        double maxHeureCourante = Float.MIN_VALUE;
        for (int i = 0; i < 60; i++) {
            Meteo.temperExtMoyMinute[i] = Meteo.aleatoire(-10, 30);
            Meteo.temperExtMinMinute[i]
                    = Meteo.temperExtMoyMinute[i] - Meteo.aleatoire(0, 3);
            Meteo.temperExtMaxMinute[i]
                    = Meteo.temperExtMoyMinute[i] + Meteo.aleatoire(0, 3);
            if (Meteo.temperExtMinMinute[i] < minHeureCourante) {
                minHeureCourante = Meteo.temperExtMinMinute[i];
            }
            if (Meteo.temperExtMaxMinute[i] > maxHeureCourante) {
                maxHeureCourante = Meteo.temperExtMaxMinute[i];
            }
        }
        // initialisation des données par heure
        Meteo.temperExtMinHeure = new double[24];
        Meteo.temperExtMaxHeure = new double[24];
        Meteo.temperExtMinHeure[0] = minHeureCourante;
        Meteo.temperExtMaxHeure[0] = maxHeureCourante;
        // test : cas positif standard
        assertTrue(Meteo.temperaturesCoherentes());
        // la température de la minute courante peut dépasser les min/max de l'heure
        Meteo.temperExtMinMinute[0] = minHeureCourante - 1;
        Meteo.temperExtMaxMinute[0] = maxHeureCourante + 1;
        assertTrue(Meteo.temperaturesCoherentes());
        // une température minimale plus haute que la moyenne
        double ancienneTemp = Meteo.temperExtMinMinute[17];
        Meteo.temperExtMinMinute[17] = Meteo.temperExtMoyMinute[17] + 0.2;
        assertFalse(Meteo.temperaturesCoherentes());
        Meteo.temperExtMinMinute[17] = ancienneTemp;
        // une température maximale plus basse que la moyenne
        ancienneTemp = Meteo.temperExtMaxMinute[58];
        Meteo.temperExtMaxMinute[58] = Meteo.temperExtMoyMinute[58] - 0.7;
        assertFalse(Meteo.temperaturesCoherentes());
        Meteo.temperExtMaxMinute[58] = ancienneTemp;
        // une température maximale plus basse que la minimale
        ancienneTemp = Meteo.temperExtMaxMinute[6];
        Meteo.temperExtMaxMinute[6] = Meteo.temperExtMinMinute[6] - 0.1;
        assertFalse(Meteo.temperaturesCoherentes());
        Meteo.temperExtMaxMinute[6] = ancienneTemp;
    }

    /**
     * Fonction utilitaire pour tester si un tableau est obtenu à partir d'un
     * autre par un décalage à droite.
     *
     * @param tab1 tableau de référence
     * @param tab2 tableau prétendument décalé à droite
     * @return vrai ssi le second est décalé à droite par rapport au premier
     */
    static boolean tableauxDecalesADroite(double[] tab1, double[] tab2) {
        boolean ok = tab1.length == tab2.length;
        int i = 0;
        while (ok && i < tab1.length - 1) {
            ok &= (tab1[i] == tab2[i + 1]);
            i++;
        }
        return ok;
    }

    @Test
    public void testFinMinuteTemperExtMin() {
        double precision = 0.01;
        // préparation du cas de test
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        Meteo.temperExtMinMinute[0] = -2.3;
        Meteo.temperExtMinHeure[0] = -1.3;
        Meteo.temperExtMinJour[0] = -2.1;
        double[] temperExtMinMinuteOld = Arrays.copyOf(Meteo.temperExtMinMinute, 60);
        double[] temperExtMinHeureOld = Arrays.copyOf(Meteo.temperExtMinHeure, 24);
        double[] temperExtMinJourOld = Arrays.copyOf(Meteo.temperExtMinJour, 365);
        double oldTemperExt = Meteo.temperExt;
        // calcul
        Meteo.finMinuteTemperExtMin();
        // décalage à droite
        assertTrue(tableauxDecalesADroite(temperExtMinMinuteOld, Meteo.temperExtMinMinute));
        // nouvelle température min pour la minute courante
        assertEquals(Meteo.temperExt, Meteo.temperExtMinMinute[0], precision);
        // température extérieure non modifiée
        assertEquals(oldTemperExt, Meteo.temperExt, precision);
        // mise à jour de la température min de l'heure
        assertEquals(temperExtMinMinuteOld[0], Meteo.temperExtMinHeure[0], precision);
        // mise à jour de la température min du jour
        assertEquals(temperExtMinMinuteOld[0], Meteo.temperExtMinJour[0], precision);
        // autre cas, sans mise à jour des minimums heure et jour
        Meteo.temperExtMinMinute[0] = -0.3;
        Meteo.temperExtMinHeure[0] = -1.3;
        Meteo.temperExtMinJour[0] = -2.1;
        Meteo.finMinuteTemperExtMin();
        assertEquals(temperExtMinHeureOld[0], Meteo.temperExtMinHeure[0], precision);
        assertEquals(temperExtMinJourOld[0], Meteo.temperExtMinJour[0], precision);
    }

    @Test
    public void testFinMinuteTemperExtMoy() {
        double precision = 0.01;
        // préparation du cas de test
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        double[] temperExtMoyMinuteOld = Arrays.copyOf(Meteo.temperExtMoyMinute, 60);
        double[] temperExtMoyHeureOld = Arrays.copyOf(Meteo.temperExtMoyHeure, 24);
        double oldTemperExt = Meteo.temperExt;
        // calcul des moyennes
        double moyHeure, moyJour;
        double somme = 0;
        for (double moyH : Meteo.temperExtMoyMinute) {
            somme += moyH;
        }
        moyHeure = somme / 60;
        temperExtMoyHeureOld[0] = moyHeure;
        somme = 0;
        for (double moyJ : temperExtMoyHeureOld) {
            somme += moyJ;
        }
        moyJour = somme / 24;
        // calcul
        Meteo.finMinuteTemperExtMoy();
        // décalage à droite
        assertTrue(tableauxDecalesADroite(temperExtMoyMinuteOld, Meteo.temperExtMoyMinute));
        // nouvelle température moyenne pour la minute courante
        assertEquals(Meteo.temperExt, Meteo.temperExtMoyMinute[0], precision);
        // température extérieure non modifiée
        assertEquals(oldTemperExt, Meteo.temperExt, precision);
        // mise à jour de la température moyenne de l'heure
        assertEquals(moyHeure, Meteo.temperExtMoyHeure[0], precision);
        // mise à jour de la température moyenne du jour
        assertEquals(moyJour, Meteo.temperExtMoyJour[0], precision);
    }

    @Test
    public void testFinMinuteTemperExtMax() {
        double precision = 0.01;
        // préparation du cas de test
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        Meteo.temperExtMaxMinute[0] = 21.3;
        Meteo.temperExtMaxHeure[0] = 20.3;
        Meteo.temperExtMaxJour[0] = 21.1;
        double[] temperExtMaxMinuteOld = Arrays.copyOf(Meteo.temperExtMaxMinute, 60);
        double[] temperExtMaxHeureOld = Arrays.copyOf(Meteo.temperExtMaxHeure, 24);
        double[] temperExtMaxJourOld = Arrays.copyOf(Meteo.temperExtMaxJour, 365);
        double oldTemperExt = Meteo.temperExt;
        // calcul
        Meteo.finMinuteTemperExtMax();
        // décalage à droite
        assertTrue(tableauxDecalesADroite(temperExtMaxMinuteOld, Meteo.temperExtMaxMinute));
        // nouvelle température max pour la minute courante
        assertEquals(Meteo.temperExt, Meteo.temperExtMaxMinute[0], precision);
        // température extérieure non modifiée
        assertEquals(oldTemperExt, Meteo.temperExt, precision);
        // mise à jour de la température max de l'heure
        assertEquals(temperExtMaxMinuteOld[0], Meteo.temperExtMaxHeure[0], precision);
        // mise à jour de la température max du jour
        assertEquals(temperExtMaxMinuteOld[0], Meteo.temperExtMaxJour[0], precision);
        // autre cas, sans mise à jour des maximums heure et jour
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        Meteo.temperExtMaxMinute[0] = 13.7;
        Meteo.temperExtMaxHeure[0] = 15.4;
        Meteo.temperExtMaxJour[0] = 16.2;
        oldTemperExt = Meteo.temperExt;
        temperExtMaxHeureOld = Arrays.copyOf(Meteo.temperExtMaxHeure, 24);
        temperExtMaxJourOld = Arrays.copyOf(Meteo.temperExtMaxJour, 365);
        Meteo.finMinuteTemperExtMax();
        assertEquals(temperExtMaxHeureOld[0], Meteo.temperExtMaxHeure[0], precision);
        assertEquals(temperExtMaxJourOld[0], Meteo.temperExtMaxJour[0], precision);
        // température extérieure non modifiée
        assertEquals(oldTemperExt, Meteo.temperExt, precision);
    }

    @Test
    public void testFinHeureTemperExt() {
        double precision = 0.01;
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        double[] temperExtMinJourOld = Arrays.copyOf(Meteo.temperExtMinJour, 365);
        double[] temperExtMinHeureOld = Arrays.copyOf(Meteo.temperExtMinHeure, 24);
        double[] temperExtMoyHeureOld = Arrays.copyOf(Meteo.temperExtMoyHeure, 24);
        double[] temperExtMaxHeureOld = Arrays.copyOf(Meteo.temperExtMaxHeure, 24);
        double[] temperExtMoyMinuteOld = Arrays.copyOf(Meteo.temperExtMoyMinute, 60);
        Meteo.finHeureTemperExt();
        // décalage à droite
        assertTrue(tableauxDecalesADroite(temperExtMinHeureOld, Meteo.temperExtMinHeure));
        assertTrue(tableauxDecalesADroite(temperExtMoyHeureOld, Meteo.temperExtMoyHeure));
        assertTrue(tableauxDecalesADroite(temperExtMaxHeureOld, Meteo.temperExtMaxHeure));
        // température extérieure au début
        assertEquals(Meteo.temperExt, Meteo.temperExtMinHeure[0], precision);
        assertEquals(Meteo.temperExt, Meteo.temperExtMoyHeure[0], precision);
        assertEquals(Meteo.temperExt, Meteo.temperExtMaxHeure[0], precision);
        // temperExtMoyMinute inchangé
        assertArrayEquals(temperExtMoyMinuteOld, Meteo.temperExtMoyMinute, precision);
        // temperExtMinJour inchangé
        assertArrayEquals(temperExtMinJourOld, Meteo.temperExtMinJour, precision);
    }

    @Test
    public void testFinJourTemperExt() {
        double precision = 0.01;
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        double[] temperExtMinJourOld = Arrays.copyOf(Meteo.temperExtMinJour, 365);
        double[] temperExtMoyJourOld = Arrays.copyOf(Meteo.temperExtMoyJour, 365);
        double[] temperExtMaxJourOld = Arrays.copyOf(Meteo.temperExtMaxJour, 365);
        double[] temperExtMaxHeureOld = Arrays.copyOf(Meteo.temperExtMaxHeure, 24);
        double[] temperExtMoyMinuteOld = Arrays.copyOf(Meteo.temperExtMoyMinute, 60);
        Meteo.finJourTemperExt();
        // décalage à droite
        assertTrue(tableauxDecalesADroite(temperExtMinJourOld, Meteo.temperExtMinJour));
        assertTrue(tableauxDecalesADroite(temperExtMoyJourOld, Meteo.temperExtMoyJour));
        assertTrue(tableauxDecalesADroite(temperExtMaxJourOld, Meteo.temperExtMaxJour));
        // température extérieure au début
        assertEquals(Meteo.temperExt, Meteo.temperExtMinJour[0], precision);
        assertEquals(Meteo.temperExt, Meteo.temperExtMoyJour[0], precision);
        assertEquals(Meteo.temperExt, Meteo.temperExtMaxJour[0], precision);
        // temperExtMoyMinute inchangé
        assertArrayEquals(temperExtMoyMinuteOld, Meteo.temperExtMoyMinute, precision);
        // temperExtMaxHeure inchangé
        assertArrayEquals(temperExtMaxHeureOld, Meteo.temperExtMaxHeure, precision);
    }

    @Test
    public void testFinMinutePluvio() {
        double precision = 0.01;
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        Meteo.pluvioMinute[0] = 2.3; // pour ne pas tomber sur 0
        double[] pluvioJourOld = Arrays.copyOf(Meteo.pluvioJour, 365);
        double[] pluvioHeureOld = Arrays.copyOf(Meteo.pluvioHeure, 24);
        double[] pluvioMinuteOld = Arrays.copyOf(Meteo.pluvioMinute, 60);
        Meteo.finMinutePluvio();
        // décalage à droite
        assertTrue(
                tableauxDecalesADroite(pluvioMinuteOld, Meteo.pluvioMinute));
        // zéro au début
        assertEquals(0, Meteo.pluvioMinute[0], precision);
        // mise à jour du cumul par heure
        assertEquals(pluvioHeureOld[0] + pluvioMinuteOld[0],
                Meteo.pluvioHeure[0], precision);
        // mise à jour du cumul par jour
        assertEquals(pluvioJourOld[0] + pluvioMinuteOld[0],
                Meteo.pluvioJour[0], precision);
    }

    @Test
    public void testFinHeurePluvio() {
        double precision = 0.01;
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        double[] pluvioJourOld = Arrays.copyOf(Meteo.pluvioJour, 365);
        double[] pluvioHeureOld = Arrays.copyOf(Meteo.pluvioHeure, 24);
        double[] pluvioMinuteOld = Arrays.copyOf(Meteo.pluvioMinute, 60);
        Meteo.finHeurePluvio();
        // décalage à droite
        assertTrue(tableauxDecalesADroite(pluvioHeureOld, Meteo.pluvioHeure));
        // zéro au début
        assertEquals(0, Meteo.pluvioHeure[0], precision);
        // pluvioMinute inchangé
        assertArrayEquals(pluvioMinuteOld, Meteo.pluvioMinute, precision);
        // pluvioJour inchangé
        assertArrayEquals(pluvioJourOld, Meteo.pluvioJour, precision);
    }

    @Test
    public void testFinJourPluvio() {
        double precision = 0.01;
        Meteo.initialiserDonneesJournalieres();
        Meteo.initialiserDonneesParHeure();
        Meteo.initialiserDonneesParMinute();
        double[] pluvioJourOld = Arrays.copyOf(Meteo.pluvioJour, 365);
        double[] pluvioHeureOld = Arrays.copyOf(Meteo.pluvioHeure, 24);
        double[] pluvioMinuteOld = Arrays.copyOf(Meteo.pluvioMinute, 60);
        Meteo.finJourPluvio();
        // décalage à droite
        assertTrue(tableauxDecalesADroite(pluvioJourOld, Meteo.pluvioJour));
        // zéro au début
        assertEquals(0, Meteo.pluvioJour[0], precision);
        // pluvioMinute inchangé
        assertArrayEquals(pluvioMinuteOld, Meteo.pluvioMinute, precision);
        // pluvioHeure inchangé
        assertArrayEquals(pluvioHeureOld, Meteo.pluvioHeure, precision);
    }

    @Test
    public void testInsolation() {
        Meteo.ensoleillement = 0.0;
        assertFalse(Meteo.insolation());
        Meteo.ensoleillement = 120.0;
        assertTrue(Meteo.insolation());
        Meteo.ensoleillement = 119.0;
        assertFalse(Meteo.insolation());
        Meteo.ensoleillement = 1000.0;
        assertTrue(Meteo.insolation());
    }

    @Test
    public void testInitialiser() {
        Meteo.initialiser();
        // les tableaux sont créés
        assertNotNull(Meteo.temperExtMinJour);
        assertNotNull(Meteo.temperExtMaxJour);
        assertNotNull(Meteo.temperExtMoyJour);
        assertNotNull(Meteo.pluvioJour);
        assertNotNull(Meteo.pluvioHeure);
        assertNotNull(Meteo.pluvioMinute);
        assertNotNull(Meteo.hygroMinute);
        assertNotNull(Meteo.hygroHeure);
        assertNotNull(Meteo.hygroMinute);
        // les tableaux contiennent des valeurs différentes de zéro
        assertTrue(contientValeurNonNulle(Meteo.temperExtMinJour));
        assertTrue(contientValeurNonNulle(Meteo.temperExtMaxJour));
        assertTrue(contientValeurNonNulle(Meteo.temperExtMoyJour));
        assertTrue(contientValeurNonNulle(Meteo.pluvioJour));
        assertTrue(contientValeurNonNulle(Meteo.pluvioHeure));
        assertTrue(contientValeurNonNulle(Meteo.pluvioMinute));
        assertTrue(contientValeurNonNulle(Meteo.hygroMinute));
        assertTrue(contientValeurNonNulle(Meteo.hygroHeure));
        assertTrue(contientValeurNonNulle(Meteo.hygroMinute));
    }

    @Test
    public void testAllumerNbCasesDuBas() {
        boolean[][] matrice = new boolean[8][24];
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int colonne = 0; colonne < 24; colonne++) {
                matrice[ligne][colonne] = true;
            }
        }
        Meteo.allumerNbCasesDuBas(matrice, 3, 7);
        Meteo.allumerNbCasesDuBas(matrice, 0, 1);
        Meteo.allumerNbCasesDuBas(matrice, 23, 8);
        for (int ligne = 0; ligne < 8; ligne++) {
            assertEquals(ligne >= 1, matrice[ligne][3]);
            assertEquals(ligne >= 7, matrice[ligne][0]);
            assertTrue(matrice[ligne][23]);
            assertTrue(matrice[ligne][1]);
        }
    }

    @Test
    public void testAllumerNbCasesIntervalle() {
        boolean[][] matrice = new boolean[8][24];
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int colonne = 0; colonne < 24; colonne++) {
                matrice[ligne][colonne] = false;
            }
        }
        Meteo.allumerNbCasesIntervalle(matrice, 8, 7, 0);
        Meteo.allumerNbCasesIntervalle(matrice, 0, 3, 3);
        Meteo.allumerNbCasesIntervalle(matrice, 23, 6, 2);
        for (int ligne = 0; ligne < 8; ligne++) {
            assertTrue(matrice[ligne][8]);
            assertEquals(ligne == 3, matrice[ligne][0]);
            assertEquals(2 <= ligne && ligne <= 6, matrice[ligne][23]);
            assertFalse(matrice[ligne][1]);
        }
    }

    @Test
    public void testMatricePluvioHeure() {
        Odomo.mode = Odomo.MODE_METEO;
        Ecran.typeHisto = Ecran.TYPE_HISTO_PLUVIO_HEURE;
        // données testées
        double[] pluvio = {2.3, 3.9, 1.0, 0., 0., 0., 0., 0., 0., 0., 0., 0.,
            0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 4.5, 1.7};
        Meteo.pluvioHeure = pluvio;
        // calcul de la matrice booléenne
        boolean[][] matricePluvioCalculee = Meteo.matricePluvioHeure();
        // dimensions correctes et initialisation
        assertEquals("Nombre de lignes incorrect", 8, matricePluvioCalculee.length);
        assertNotNull(matricePluvioCalculee[0]);
        assertEquals("Nombre de colonnes incorrect", 24, matricePluvioCalculee[0].length);
        assertNotNull(matricePluvioCalculee[0][0]);
        // valeurs correctes ?
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int colonne = 0; colonne < 24; colonne++) {
                boolean calcule = matricePluvioCalculee[ligne][colonne];
                switch (colonne) {
                    case 0:
                        assertTrue(calcule == ligne >= 4);
                        break;
                    case 1:
                        assertTrue(calcule);
                        break;
                    case 21:
                        assertTrue(calcule == ligne >= 5);
                        break;
                    case 22:
                        assertTrue(calcule == ligne >= 1);
                        break;
                    case 23:
                        assertTrue(calcule == ligne >= 3);
                        break;
                    default:
                        assertFalse(calcule);
                        break;
                }
            }
        }
    }

    @Test
    public void testMatriceTemperMinutes() {
        Odomo.mode = Odomo.MODE_METEO;
        Ecran.typeHisto = Ecran.TYPE_HISTO_TEMPER_MINUTES;
        double precision = .0001;
        // données testées
        Meteo.temperExtMinMinute = new double[]{
            21.91658353805542, 21.42251169681549, 14.807575702667236,
            29.503679394721985, 28.24105978012085, -0.17151927947998047,
            18.685890436172485, -10.393776655197144, 27.227733492851257,
            19.422696590423584, 37.80837643146515, 23.738927006721497,
            -4.509767413139343, 24.69513988494873, 4.724032163619995,
            21.6319522857666, 31.324504375457764, -5.598420143127441,
            27.526323795318604, 11.716031074523926, 29.584022641181946,
            -0.39061474800109863, -8.715437650680542, 6.402895450592041,
            11.641194105148315, -4.737683892250061, 33.83514380455017,
            15.773542284965515, 0.4972282648086548, 7.881775856018066,
            10.273549318313599, -7.753142356872559, 22.472800731658936,
            1.7851492166519165, -1.9775500297546387, 39.22502362728119,
            10.974419355392456, -4.4747514724731445, -8.210866093635559,
            25.312217235565186, 6.092568516731262, 7.413942217826843,
            27.499738931655884, 11.44296646118164, 6.446069836616516,
            2.5447241067886353, 22.07208549976349, 30.83384072780609,
            -2.9902665615081787, 9.788194060325623, 19.126283526420593,
            -1.284460425376892, 15.440157532691956, 0.4904310703277588,
            -5.584732174873352, 23.399755716323853, 3.168349504470825,
            -7.076514363288879, 20.135560870170593, -10.35886549949646};
        Meteo.temperExtMaxMinute = new double[]{
            22.919569611549377, 24.483978867530823, 23.865784883499145,
            32.24332571029663, 30.275553107261658, 1.9153000116348267,
            20.79384410381317, -8.589014768600464, 28.297062516212463,
            22.00410795211792, 39.55147683620453, 26.05431377887726,
            -1.3416969776153564, 26.151576042175293, 7.636130094528198,
            25.137822151184082, 34.29650938510895, -3.3552204370498657,
            30.194963097572327, 14.122663378715515, 31.248205304145813,
            0.614517331123352, -6.358462333679199, 7.350939750671387,
            12.630542874336243, -4.576101064682007, 35.78427219390869,
            19.00464940071106, 3.259942889213562, 9.705127716064453,
            11.44475269317627, -5.8938987255096436, 23.93260395526886,
            4.89652681350708, 0.7739006280899048, 39.59191942214966,
            11.91367256641388, -2.8538142442703247, -7.232410788536072,
            26.58535349369049, 6.962609648704529, 8.792604446411133,
            29.453346848487854, 13.906576037406921, 8.93044626712799,
            5.08839750289917, 23.798665642738342, 32.670860171318054,
            -0.11845433712005615, 11.611431956291199, 22.410158276557922,
            2.535230875015259, 17.214906454086304, 2.2645455598831177,
            -4.345115661621094, 24.074638843536377, 5.437362790107727,
            -4.87435781955719, 20.868696808815002, -9.20582890510559};
        // calcul de la matrice booléenne
        boolean[][] matriceCalculee = Meteo.matriceTemperMinutes();
        // dimensions correctes et initialisation
        assertEquals("Nombre de lignes incorrect", 8, matriceCalculee.length);
        assertNotNull(matriceCalculee[0]);
        assertEquals("Nombre de colonnes incorrect", 24, matriceCalculee[0].length);
        assertNotNull(matriceCalculee[0][0]);
        // valeurs correctes ?
        boolean[][] matriceCorrecte = new boolean[][]{
            {false, false, false, false, false, false, false, false, false,
                true, false, false, false, false, false, false, false, false,
                false, true, false, false, false, false},
            {false, false, false, false, false, true, false, true, false, true,
                false, false, false, true, false, true, true, true, false, true,
                false, false, true, false},
            {false, true, false, true, false, true, false, true, true, true,
                false, true, false, true, false, true, true, true, true, true,
                true, false, true, true},
            {true, true, false, true, false, true, false, true, true, true,
                false, true, false, true, false, true, true, true, true, true,
                true, true, false, true},
            {true, true, false, true, true, true, true, true, true, true, false,
                true, true, true, true, true, true, true, true, true, false,
                true, false, false},
            {true, true, true, true, true, true, true, true, true, true, true,
                true, true, true, true, true, false, true, true, true, false,
                true, false, false},
            {true, true, true, true, true, false, false, false, true, true,
                true, true, false, true, false, true, false, true, false, true,
                false, true, false, false},
            {true, true, false, false, false, false, false, false, true, false,
                false, true, false, false, false, true, false, false, false,
                false, false, true, false, false}
        };
        assertArrayEquals(matriceCorrecte, matriceCalculee);
        // calculs à la main...
        double temperMinGlobale = -10.393776655197144;
        assertEquals(temperMinGlobale,
                Meteo.valeurMin(Meteo.temperExtMinMinute), precision);
        double temperMaxGlobale = 39.59191942214966;
        assertEquals(temperMaxGlobale,
                Meteo.valeurMax(Meteo.temperExtMaxMinute), precision);
        double pas = (temperMaxGlobale - temperMinGlobale) / 7;
        assertEquals(temperMaxGlobale, temperMinGlobale + 7. * pas, precision);
        // pour la colonne 23 de l'histogramme
        // = colonnes 0, 1 et 2 pour la température
        double temperMini23 = 14.807575702667236;
        assertEquals(temperMini23,
                Meteo.valeurMin(Arrays.copyOfRange(Meteo.temperExtMinMinute, 0, 3)), precision);
        double temperMaxi23 = 24.483978867530823;
        assertEquals(temperMaxi23,
                Meteo.valeurMax(Arrays.copyOfRange(Meteo.temperExtMaxMinute, 0, 3)), precision);
        double ecart = temperMaxGlobale - temperMinGlobale;
        int ligneMin = 7 - (int) Math.round(
                7. * (temperMini23 - temperMinGlobale) / ecart);
        int ligneMax = 7 - (int) Math.round(
                7. * (temperMaxi23 - temperMinGlobale) / ecart);
        for (int lig = 0; lig < 8; lig++) {
            assertEquals(ligneMin >= lig && lig >= ligneMax,
                    matriceCalculee[lig][23]);
        }
    }

    @Test
    public void testAgreger60vers24() {
        assertArrayEquals(new int[]{58, 59}, Meteo.agreger60vers24(0));
        assertArrayEquals(new int[]{55, 56, 57}, Meteo.agreger60vers24(1));
        assertArrayEquals(new int[]{53, 54}, Meteo.agreger60vers24(2));
        assertArrayEquals(new int[]{5, 6, 7}, Meteo.agreger60vers24(21));
        assertArrayEquals(new int[]{3, 4}, Meteo.agreger60vers24(22));
        assertArrayEquals(new int[]{0, 1, 2}, Meteo.agreger60vers24(23));
    }

    @Test
    public void testTableauDepuisIndices() {
        double precision = .0001;
        double[] tab = {17.3, 45.2, -10.1, 3., 8.3, 8.1, 33.2};
        assertArrayEquals(new double[]{17.3, -10.1},
                Meteo.tableauDepuisIndices(tab, new int[]{0, 2}), precision);
        assertArrayEquals(new double[]{-10.1, 3., 33.2},
                Meteo.tableauDepuisIndices(tab, new int[]{2, 3, 6}), precision);
    }

    @Test
    public void testValeurMax() {
        double precision = .001;
        double[] tab1 = {-6., -9., 4., .2};
        assertEquals(4., Meteo.valeurMax(tab1), precision);
        double[] tab2 = {-6., -9., 0., -.2};
        assertEquals(0., Meteo.valeurMax(tab2), precision);
        double[] tab3 = {-6., -9., -12., -6.3};
        assertEquals(-6., Meteo.valeurMax(tab3), precision);
        double[] tab4 = {3.};
        assertEquals(3., Meteo.valeurMax(tab4), precision);
        double[] tab5 = {};
        assertEquals(0., Meteo.valeurMax(tab5), precision);
    }

    @Test
    public void testValeurMin() {
        double precision = .001;
        double[] tab1 = {-6., -9., 4., .2};
        assertEquals(-9., Meteo.valeurMin(tab1), precision);
        double[] tab2 = {-6., -9., 0., -.2};
        assertEquals(-9., Meteo.valeurMin(tab2), precision);
        double[] tab3 = {-6., -9., -12., -6.3};
        assertEquals(-12., Meteo.valeurMin(tab3), precision);
        double[] tab4 = {3.};
        assertEquals(3., Meteo.valeurMin(tab4), precision);
        double[] tab5 = {};
        assertEquals(0., Meteo.valeurMin(tab5), precision);
    }

    @Test
    public void testAleatoire() {
        for (int i = 0; i < 100; i++) {
            double alea = Meteo.aleatoire(18., 20.);
            assertTrue(alea >= 18. && alea < 20.);
        }
    }

    /**
     * Vérifie qu'un tableau de double contient au moins une valeur différente
     * de zéro.
     *
     * @param tab le tableau à analyser
     * @return vrai ssi ce tableau contient au moins une valeur différente de
     * zéro
     */
    static boolean contientValeurNonNulle(double[] tab) {
        boolean trouveNonNulle = false;
        int i = 0;
        while (i < tab.length && !trouveNonNulle) {
            if (tab[i] != 0.) {
                trouveNonNulle = true;
            }
            i++;
        }
        return trouveNonNulle;
    }
}
