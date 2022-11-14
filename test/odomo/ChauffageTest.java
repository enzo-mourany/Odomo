package odomo;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests des méthodes de la classe Chauffage.
 */
public class ChauffageTest {
    
    @Test
    public void testInitialiser() {
        Chauffage.initialiser();
        assertEquals(19, Chauffage.temperNormal);
        assertEquals(16, Chauffage.temperEco);
    }
    
    @Test
    public void testDansCreneauNormal() {
        Chauffage.initialiser();
        Chauffage.creneau1 = 
                new int[][]{{6, 9}, {6, 9}, {6, 22}, {6, 9}, {6, 9}, {7, 23}, {7, 23}};
        Chauffage.creneau2 = 
                new int[][]{{17, 22}, {17, 22}, {1, 0}, {17, 22}, {17, 22}, {1, 0}, {1, 0}};
        assertFalse(Chauffage.dansCreneauNormal(0, 0));
        assertTrue(Chauffage.dansCreneauNormal(0, 6));
        assertTrue(Chauffage.dansCreneauNormal(1, 7));
        assertTrue(Chauffage.dansCreneauNormal(1, 8));
        assertTrue(Chauffage.dansCreneauNormal(1, 9));
        assertFalse(Chauffage.dansCreneauNormal(1, 10));
        assertFalse(Chauffage.dansCreneauNormal(1, 16));
        assertTrue(Chauffage.dansCreneauNormal(1, 17));
        assertTrue(Chauffage.dansCreneauNormal(1, 22));
        assertFalse(Chauffage.dansCreneauNormal(1, 23));
        assertFalse(Chauffage.dansCreneauNormal(2, 0));
        assertFalse(Chauffage.dansCreneauNormal(6, 0));
        assertFalse(Chauffage.dansCreneauNormal(6, 1));
        assertFalse(Chauffage.dansCreneauNormal(6, 6));
        assertTrue(Chauffage.dansCreneauNormal(6, 7));
        assertTrue(Chauffage.dansCreneauNormal(6, 8));
        assertTrue(Chauffage.dansCreneauNormal(6, 22));
        assertTrue(Chauffage.dansCreneauNormal(6, 23));
    }
    
    @Test
    public void testTemperatureSouhaitee() {
        Chauffage.temperEco = 14;
        Chauffage.temperNormal = 18;
        Chauffage.creneau1 = 
                new int[][]{{6, 9}, {6, 9}, {6, 22}, {6, 9}, {6, 9}, {7, 23}, {7, 23}};
        Chauffage.creneau2 = 
                new int[][]{{17, 22}, {17, 22}, {1, 0}, {17, 22}, {17, 22}, {1, 0}, {1, 0}};
        assertEquals(14, Chauffage.temperatureSouhaitee(0, 0));
        assertEquals(14, Chauffage.temperatureSouhaitee(0, 5));
        assertEquals(18, Chauffage.temperatureSouhaitee(0, 6));
        assertEquals(18, Chauffage.temperatureSouhaitee(0, 9));
        assertEquals(14, Chauffage.temperatureSouhaitee(0, 10));
        assertEquals(18, Chauffage.temperatureSouhaitee(2, 10));
        assertEquals(18, Chauffage.temperatureSouhaitee(2, 22));
        assertEquals(14, Chauffage.temperatureSouhaitee(2, 23));
        assertEquals(14, Chauffage.temperatureSouhaitee(6, 6));
        assertEquals(18, Chauffage.temperatureSouhaitee(6, 12));
    }
    
    @Test
    public void testMatriceCreneaux() {
        Chauffage.creneau1 = 
                new int[][]{{6, 9}, {6, 9}, {6, 22}, {6, 9}, {6, 9}, {7, 23}, {7, 23}};
        Chauffage.creneau2 = 
                new int[][]{{17, 22}, {17, 22}, {1, 0}, {17, 22}, {17, 22}, {1, 0}, {1, 0}};
        boolean attendu[][] = new boolean[8][24];
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < 24; col++) {
                attendu[ligne][col] =
                        ligne < 5 && ((6 <= col && col <= 9) || (17 <= col && col <= 22));
                attendu[ligne][col] |=
                        ligne == 2 && (6 <= col && col <= 22);
                attendu[ligne][col] |=
                        (ligne == 5 || ligne == 6) && (7 <= col && col <= 23);
            }
        }
        assertArrayEquals(attendu, Chauffage.matriceCreneaux());
    }
    
    @Test
    public void testTraitementSaisieCreneaux() {
        Chauffage.initialiser();
        Odomo.mode = Odomo.MODE_SAISIE_CHAUFFAGE;
        assertTrue(Chauffage.traitementSaisieCreneaux("jeu;6;8"));
        assertTrue(Chauffage.traitementSaisieCreneaux("mar;6;8;10;12"));
        assertTrue(Chauffage.traitementSaisieCreneaux("mar;10;12;6;8"));
        assertTrue(Chauffage.traitementSaisieCreneaux("dim;10;12;6;11"));
        assertTrue(Chauffage.traitementSaisieCreneaux("lun;10;10"));
        assertTrue(Chauffage.traitementSaisieCreneaux("ven;10;10;10;10"));
        assertTrue(Chauffage.traitementSaisieCreneaux("jeu;1;0;0;23"));
        assertTrue(Chauffage.traitementSaisieCreneaux("dim;1;0;1;0"));
        assertFalse(Chauffage.traitementSaisieCreneaux("sam;11;10"));
        assertFalse(Chauffage.traitementSaisieCreneaux("mer;1;0;11;10"));
        assertFalse(Chauffage.traitementSaisieCreneaux("8;9"));
        assertFalse(Chauffage.traitementSaisieCreneaux("lundi;8;9"));
        assertFalse(Chauffage.traitementSaisieCreneaux("lun,mar;8;9"));
        assertFalse(Chauffage.traitementSaisieCreneaux("ven;8"));
        assertFalse(Chauffage.traitementSaisieCreneaux("jeu;3;7;5"));
        assertFalse(Chauffage.traitementSaisieCreneaux("mer;2;;5"));
        assertFalse(Chauffage.traitementSaisieCreneaux("lun;4;"));
    }
    
    @Test
    public void testNettoyageCreneaux() {
        Chauffage.initialiser();
        Chauffage.creneau1 = 
                new int[][]{{6, 9}, {6, 9}, {6, 22}, {6, 9}, {6, 9}, {7, 23}, {7, 23}};
        Chauffage.creneau2 = 
                new int[][]{{17, 22}, {17, 22}, {1, 0}, {17, 22}, {17, 22}, {1, 0}, {1, 0}};
        // cas 6h-9h et 8h-10h -> devient 6h-10h
        Chauffage.creneau1[1] = new int[]{6, 9};
        Chauffage.creneau2[1] = new int[]{8, 10};
        Chauffage.nettoyageCreneaux();
        assertEquals(6, Chauffage.creneau1[1][0]);
        assertEquals(10, Chauffage.creneau1[1][1]);
        assertEquals(1, Chauffage.creneau2[1][0]);
        assertEquals(0, Chauffage.creneau2[1][1]);
        // cas 6h-9h et 3h-5h -> devient 3h-9h
        Chauffage.creneau1[3] = new int[]{6, 9};
        Chauffage.creneau2[3] = new int[]{3, 5};
        Chauffage.nettoyageCreneaux();
        assertEquals(3, Chauffage.creneau1[3][0]);
        assertEquals(9, Chauffage.creneau1[3][1]);
        assertEquals(1, Chauffage.creneau2[3][0]);
        assertEquals(0, Chauffage.creneau2[3][1]);
        // cas 6h-9h et 3h-4h -> pas de changement
        Chauffage.creneau1[0] = new int[]{6, 9};
        Chauffage.creneau2[0] = new int[]{3, 4};
        Chauffage.nettoyageCreneaux();
        assertEquals(6, Chauffage.creneau1[0][0]);
        assertEquals(9, Chauffage.creneau1[0][1]);
        assertEquals(3, Chauffage.creneau2[0][0]);
        assertEquals(4, Chauffage.creneau2[0][1]);
        // cas 6h-9h et 7h-8h -> suppression second créneau
        Chauffage.creneau1[4] = new int[]{6, 9};
        Chauffage.creneau2[4] = new int[]{7, 8};
        Chauffage.nettoyageCreneaux();
        assertEquals(6, Chauffage.creneau1[4][0]);
        assertEquals(9, Chauffage.creneau1[4][1]);
        assertEquals(1, Chauffage.creneau2[4][0]);
        assertEquals(0, Chauffage.creneau2[4][1]);
        // cas 6h-9h et 10h-10h -> devient 6h-10h
        Chauffage.creneau1[4] = new int[]{6, 9};
        Chauffage.creneau2[4] = new int[]{10, 10};
        Chauffage.nettoyageCreneaux();
        assertEquals(6, Chauffage.creneau1[4][0]);
        assertEquals(10, Chauffage.creneau1[4][1]);
        assertEquals(1, Chauffage.creneau2[4][0]);
        assertEquals(0, Chauffage.creneau2[4][1]);
        // cas 6h-9h et 5h-5h -> devient 5h-9h
        Chauffage.creneau1[4] = new int[]{6, 9};
        Chauffage.creneau2[4] = new int[]{5, 5};
        Chauffage.nettoyageCreneaux();
        assertEquals(5, Chauffage.creneau1[4][0]);
        assertEquals(9, Chauffage.creneau1[4][1]);
        assertEquals(1, Chauffage.creneau2[4][0]);
        assertEquals(0, Chauffage.creneau2[4][1]);
    }
}
