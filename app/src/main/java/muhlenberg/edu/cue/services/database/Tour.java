package muhlenberg.edu.cue.services.database;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import muhlenberg.edu.cue.util.location.CUELocation;

/**
 * Created by shielwill on 3/21/2017.
 */

public class Tour {
    private List<CUELocation> pointList;
    private String name;
    private int tourId;

    public Tour(String name, int tourId, List<CUELocation> pointList){
        this.name = name;
        this.tourId = tourId;
        this.pointList = pointList;
    }

    public void setTourId(int tourId) { this.tourId = tourId; }

    public int getTourId(){ return this.tourId; }

    public void setName(String name) { this.name = name; }

    public String getName(){ return this.name; }

    public void setPointList(List<CUELocation> pointList) { this.pointList = pointList; }

    public List<CUELocation> getPointList() { return this.pointList; }
}
