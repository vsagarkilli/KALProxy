package com.yesmail.service;

import com.yesmail.model.KALSegment;
import com.yesmail.model.KALTargetSegment;

import java.util.List;
import java.util.Map;

public interface KALAccleratorService {

    public void createSegment(String projectName, KALSegment segment);

    public Map<String,List<String>> getSegments(String projectName);

    public List<String>getProjects();

    public long getCount(String projectName, String tableName,  String segment);

    public long getTargetCount(String projectName, List<KALTargetSegment> targetSegments);

    public void joinTable(String projectName, String firstTable, String firstJoinColumn, String secondTable, String secondJoinColumn);

}
