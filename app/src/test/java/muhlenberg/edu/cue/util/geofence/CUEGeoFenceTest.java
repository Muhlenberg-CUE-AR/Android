package muhlenberg.edu.cue.util.geofence;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by Jalal on 2/16/2017.
 */
@RunWith(JUnit4.class)
public class CUEGeoFenceTest {
    
    @Test
    public void testConstructor() {
        Location w = new Location("");
        w.setLatitude(0);
        w.setLongitude(0);

        Location x = new Location("");
        x.setLatitude(0);
        x.setLongitude(0);

        Location y = new Location("");
        y.setLatitude(0);
        y.setLongitude(0);

        Location z = new Location("");
        z.setLatitude(0);
        z.setLongitude(0);

        CUEGeoFence fence = new CUEGeoFence(w, x, y, z);
        assertEquals(w.getLatitude(), fence.getCorners()[0].getLatitude());
        assertEquals(x.getLatitude(), fence.getCorners()[1].getLatitude());
        assertEquals(y.getLatitude(), fence.getCorners()[2].getLatitude());
        assertEquals(z.getLatitude(), fence.getCorners()[3].getLatitude());

        assertEquals(w.getLongitude(), fence.getCorners()[0].getLongitude());
        assertEquals(x.getLongitude(), fence.getCorners()[1].getLongitude());
        assertEquals(y.getLongitude(), fence.getCorners()[2].getLongitude());
        assertEquals(z.getLongitude(), fence.getCorners()[3].getLongitude());


        Location[] arr = {w, x, y, z};
        CUEGeoFence fence2 = new CUEGeoFence(arr);

        assertEquals(w.getLatitude(), fence2.getCorners()[0].getLatitude());
        assertEquals(x.getLatitude(), fence2.getCorners()[1].getLatitude());
        assertEquals(y.getLatitude(), fence2.getCorners()[2].getLatitude());
        assertEquals(z.getLatitude(), fence2.getCorners()[3].getLatitude());

        assertEquals(w.getLongitude(), fence2.getCorners()[0].getLongitude());
        assertEquals(x.getLongitude(), fence2.getCorners()[1].getLongitude());
        assertEquals(y.getLongitude(), fence2.getCorners()[2].getLongitude());
        assertEquals(z.getLongitude(), fence2.getCorners()[3].getLongitude());

        CUEGeoFence fence3 = new CUEGeoFence();
        assertEquals(4, fence3.getCorners().length);
        assertNull(fence3.getCorners()[0]);
        assertNull(fence3.getCorners()[1]);
        assertNull(fence3.getCorners()[2]);
        assertNull(fence3.getCorners()[3]);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testSort() {
        // north west
        Location w = new Location("");
        w.setLatitude(-90);
        w.setLongitude(90);

        // north east
        Location x = new Location("");
        x.setLatitude(90);
        x.setLongitude(90);

        //south west
        Location y = new Location("");
        y.setLatitude(-90);
        y.setLongitude(-90);

        // south east
        Location z = new Location("");
        z.setLatitude(90);
        z.setLongitude(-90);

        ArrayList<Location> list = new ArrayList();
        list.add(w);
        list.add(x);
        list.add(y);
        list.add(z);
        Collections.shuffle(list);

        Location[] corners = new Location[4];
        for(int i=0; i<4; i++)
            corners[i] = list.get(i);

        Location[] sorted = CUEGeoFence.sort(corners);
        assertEquals(w, sorted[0]);
        assertEquals(x, sorted[1]);
        assertEquals(y, sorted[2]);
        assertEquals(z, sorted[3]);
    }

}