package com.yesmail.model;

import java.io.Serializable;

/**
 * Created by vijayask on 5/21/15.
 */
public class KALTargetSegment implements Serializable {

    private String tableName;
    private String segmentName;
    private String logic;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}
