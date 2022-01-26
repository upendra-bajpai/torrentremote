
package com.grbworks.videoplayer.data.model.dataModal;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class SeriesModals {

    @SerializedName("series")
    @Expose
    private String series;
    @SerializedName("details")
    @Expose
    private ArrayList<Detail> details = null;

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public ArrayList<Detail> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Detail> details) {
        this.details = details;
    }

}
