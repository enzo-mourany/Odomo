package odomo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests portant sur la classe Odomo.
 */
public class OdomoTest {
    
    @Test
    public void testTraiterChoix() {
        assertFalse(Odomo.traiterChoix('m'));
        assertEquals(Odomo.MODE_METEO, Odomo.mode);
        assertEquals(Ecran.TYPE_HISTO_PLUVIO_HEURE, Ecran.typeHisto);
        assertFalse(Odomo.traiterChoix('M'));
        assertEquals(Odomo.MODE_METEO, Odomo.mode);
        assertEquals(Ecran.TYPE_HISTO_PLUVIO_HEURE, Ecran.typeHisto);
        assertFalse(Odomo.traiterChoix('c'));
        assertEquals(Odomo.MODE_CHAUFFAGE, Odomo.mode);
        assertEquals(Ecran.TYPE_HISTO_CHAUFFAGE, Ecran.typeHisto);
        assertFalse(Odomo.traiterChoix('C'));
        assertEquals(Odomo.MODE_CHAUFFAGE, Odomo.mode);
        assertEquals(Ecran.TYPE_HISTO_CHAUFFAGE, Ecran.typeHisto);
        assertFalse(Odomo.traiterChoix('j'));
        assertEquals(Odomo.MODE_JARDIN, Odomo.mode);
        assertFalse(Odomo.traiterChoix('J'));
        assertEquals(Odomo.MODE_JARDIN, Odomo.mode);
        assertFalse(Odomo.traiterChoix('p'));
        assertEquals(Odomo.MODE_METEO, Odomo.mode);
        assertEquals(Ecran.TYPE_HISTO_PLUVIO_HEURE, Ecran.typeHisto);
        assertFalse(Odomo.traiterChoix('P'));
        assertEquals(Odomo.MODE_METEO, Odomo.mode);
        assertEquals(Ecran.TYPE_HISTO_PLUVIO_HEURE, Ecran.typeHisto);
        assertTrue(Odomo.traiterChoix('q'));
        assertTrue(Odomo.traiterChoix('Q'));
    }
    
    @Test
    public void testNumeroJour() {
        assertEquals(0, Odomo.numeroJour("lun"));
        assertEquals(1, Odomo.numeroJour("mar"));
        assertEquals(2, Odomo.numeroJour("mer"));
        assertEquals(3, Odomo.numeroJour("jeu"));
        assertEquals(4, Odomo.numeroJour("ven"));
        assertEquals(5, Odomo.numeroJour("sam"));
        assertEquals(6, Odomo.numeroJour("dim"));
        assertEquals(-1, Odomo.numeroJour("Lun"));
        assertEquals(-1, Odomo.numeroJour("lundi"));
    }
}
