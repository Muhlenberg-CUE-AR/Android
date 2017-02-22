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


//    @Test
//    public void testConstructor() {
//        CUELocation w = new CUELocation(0,0);
//        CUELocation x = new CUELocation(0,0);
//        CUELocation y = new CUELocation(0,0);
//        CUELocation z = new CUELocation(0,0);
//
//        CUEGeoFence fence = new CUEGeoFence(w, x, y, z);
//        assertEquals(w.getLatitude(), fence.getCorners()[0].getLatitude());
//        assertEquals(x.getLatitude(), fence.getCorners()[1].getLatitude());
//        assertEquals(y.getLatitude(), fence.getCorners()[2].getLatitude());
//        assertEquals(z.getLatitude(), fence.getCorners()[3].getLatitude());
//
//        assertEquals(w.getLongitude(), fence.getCorners()[0].getLongitude());
//        assertEquals(x.getLongitude(), fence.getCorners()[1].getLongitude());
//        assertEquals(y.getLongitude(), fence.getCorners()[2].getLongitude());
//        assertEquals(z.getLongitude(), fence.getCorners()[3].getLongitude());
//
//
//        CUELocation[] arr = {w, x, y, z};
//        CUEGeoFence fence2 = new CUEGeoFence(arr);
//
//        assertEquals(w.getLatitude(), fence2.getCorners()[0].getLatitude());
//        assertEquals(x.getLatitude(), fence2.getCorners()[1].getLatitude());
//        assertEquals(y.getLatitude(), fence2.getCorners()[2].getLatitude());
//        assertEquals(z.getLatitude(), fence2.getCorners()[3].getLatitude());
//
//        assertEquals(w.getLongitude(), fence2.getCorners()[0].getLongitude());
//        assertEquals(x.getLongitude(), fence2.getCorners()[1].getLongitude());
//        assertEquals(y.getLongitude(), fence2.getCorners()[2].getLongitude());
//        assertEquals(z.getLongitude(), fence2.getCorners()[3].getLongitude());
//
//        CUEGeoFence fence3 = new CUEGeoFence();
//        assertEquals(4, fence3.getCorners().length);
//        assertNull(fence3.getCorners()[0]);
//        assertNull(fence3.getCorners()[1]);
//        assertNull(fence3.getCorners()[2]);
//        assertNull(fence3.getCorners()[3]);
//    }
//
//
    @Test
    public void testValidate() {
        CUELocation[] correctCorners = {nw, ne, sw, se};
        CUEGeoFence correctFence = new CUEGeoFence(correctCorners);

        assertTrue(correctFence.validate(correctCorners, CUEGeoFence.VALIDATION_TOLERANCE));

        CUELocation[] incorrectCorners = {sw, nw, se, new CUELocation(0,0)};
        CUEGeoFence incorrectFence = new CUEGeoFence(incorrectCorners);
        assertFalse(incorrectFence.validate(incorrectCorners, CUEGeoFence.VALIDATION_TOLERANCE));

    }
//
//    @Test
//    public void testInsideBoundingBox() {
//        CUELocation validInside = new CUELocation(40.597571, -75.510402);
//        CUELocation[] corners = {nw, ne, sw, se};
//        CUEGeoFence fence = new CUEGeoFence(corners);
//        assertTrue(fence.isInsideBoundingBox(corners, validInside));
//
//        CUELocation invalidInside = new CUELocation(0,0);
//        assertFalse(fence.isInsideBoundingBox(corners, invalidInside));
//
//        CUELocation[] nullCorners = new CUELocation[4];
//        CUEGeoFence invalidFence = new CUEGeoFence(nullCorners);
//        assertFalse(invalidFence.isInsideBoundingBox(nullCorners, invalidInside));
//    }


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