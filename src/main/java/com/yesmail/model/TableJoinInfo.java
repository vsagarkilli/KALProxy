package com.yesmail.model;

/**
 * Created by vijayask on 5/22/15.
 */
public class TableJoinInfo {

    String firstTableName;
    String firstJoinColumn;
    String secondTableName;
    String secondJoinColumn;

    public String getFirstTableName() {
        return firstTableName;
    }

    public void setFirstTableName(String firstTableName) {
        this.firstTableName = firstTableName;
    }

    public String getFirstJoinColumn() {
        return firstJoinColumn;
    }

    public void setFirstJoinColumn(String firstJoinColumn) {
        this.firstJoinColumn = firstJoinColumn;
    }

    public String getSecondTableName() {
        return secondTableName;
    }

    public void setSecondTableName(String secondTableName) {
        this.secondTableName = secondTableName;
    }

    public String getSecondJoinColumn() {
        return secondJoinColumn;
    }

    public void setSecondJoinColumn(String secondJoinColumn) {
        this.secondJoinColumn = secondJoinColumn;
    }
}
