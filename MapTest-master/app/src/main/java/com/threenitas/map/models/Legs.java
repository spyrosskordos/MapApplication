package com.threenitas.map.models;

import java.util.List;

public class Legs {
private List<Steps> steps;
    private Distance distance;
    private Duration duration;

    public Distance getDistance() {
        return distance;
    }


    public void setDistance(Distance distance) {
        this.distance = distance;
    }


    public Duration getDuration() {
        return duration;
    }


    public void setDuration(Duration duration) {
        this.duration = duration;
    }


public List<Steps> getSteps() {
    return steps;
}
}