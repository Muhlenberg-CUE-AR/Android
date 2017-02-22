package muhlenberg.edu.cue.util.geofence;

import android.gesture.GestureOverlayView;
import android.location.Location;
import android.location.LocationManager;
import android.test.AndroidTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import muhlenberg.edu.cue.util.location.CUELocation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Jalal on 2/16/2017.
 */
@RunWith(JUnit4.class)
public class CUEGeoFenceTest {
    //correct coordinates of Muhlenberg campus
    CUELocation nw = new CUELocation(40.598346, -75.514447);
    CUELocation ne = new CUELocation(40.600081, -75.508160);
    CUELocation sw = new CUELocation(40.595600, -75.513138);
    CUELocation se = new CUELocation(40.597343, -75.506840);


    @Test
    public void testValidate() {
        CUELocation nw = new CUELocation(0, 1);
        CUELocation ne = new CUELocation(1, 1);
        CUELocation sw = new CUELocation(0, 0);

        assertTrue(CUEGeoFence.validate(nw, ne, sw));

        CUELocation bad = new CUELocation(10, 10);
        assertFalse(CUEGeoFence.validate(nw, sw, bad));
    }

    @Test
    public void testInsideBoundingBox() {
        CUELocation validInside = new CUELocation(40.597571, -75.510402);
        CUELocation[] corners = {nw, ne, sw, se};
        assertTrue(CUEGeoFence.isInsideBoundingBox(corners, validInside));
        CUELocation invalidInside = new CUELocation(0,0);
        assertFalse(CUEGeoFence.isInsideBoundingBox(corners, invalidInside));

        CUELocation[] nullCorners = new CUELocation[4];
        assertFalse(CUEGeoFence.isInsideBoundingBox(nullCorners, invalidInside));

    }

    @Test
    public void testCaclulateFourthCorner() {
        CUELocation nw = new CUELocation(0, 0.1);
        CUELocation ne = new CUELocation(0.1, 0.1);
        CUELocation sw = new CUELocation(0, 0);

        CUELocation se = CUEGeoFence.calcLastCorner(nw, ne, sw);
        assertEquals(se.getLatitude(), 0.1, 0.1);
        assertEquals(se.getLongitude(), 0, 0.1);

        nw = new CUELocation(-1.0, 1.0);
        ne = new CUELocation(0, 1.0);
        sw = new CUELocation(-1.0, 0);
        se = CUEGeoFence.calcLastCorner(nw, ne, sw);
        assertEquals(se.getLatitude(), 0, 0.1);
        assertEquals(se.getLongitude(), 0, 0.1);

    }
}