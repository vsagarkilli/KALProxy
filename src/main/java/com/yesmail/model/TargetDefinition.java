package com.yesmail.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vijayask on 5/21/15.
 */
public class TargetDefinition implements Serializable{
    List<KALTargetSegment> targetSegments;

    public List<KALTargetSegment> getTargetSegments() {
        return targetSegments;
    }

    public void setTargetSegments(List<KALTargetSegment> targetSegments) {
        this.targetSegments = targetSegments;
    }
}
