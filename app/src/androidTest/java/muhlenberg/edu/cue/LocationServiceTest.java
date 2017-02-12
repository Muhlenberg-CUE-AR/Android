package muhlenberg.edu.cue;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by Jalal on 2/11/2017.
 */

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {

    @Mock
    Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testConnection() {

    }
}
