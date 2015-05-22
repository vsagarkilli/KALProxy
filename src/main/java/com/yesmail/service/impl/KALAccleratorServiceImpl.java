package com.yesmail.service.impl;

import BitmapEngine.BCBitmap;
import Environment.KAL;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.yesmail.model.KALSegment;
import com.yesmail.model.KALTargetSegment;
import com.yesmail.service.KALAccleratorService;
import database.DataSource;
import mongo.MongoConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import structure.*;

import java.util.*;

@Service("kalAccleratorService")
public class KALAccleratorServiceImpl implements KALAccleratorService, DisposableBean, InitializingBean {

    @Autowired
    private MongoConnection.MConnect mongoDataSoure;

    @Autowired
    private DataSource dataSource;

    @Value("${kal.acclerator.properties}")
    private String kalProperties;

    @Autowired
    private CounterService counterService;


    @Override
    public void createSegment(String projectName, KALSegment segment) {
        ALProject project = null;
        try {
            //delete the project if it exists already
            //ALProject.delete(mongoDataSoure, projectName);
            //create new project
            counterService.increment("Create Segment start: " + new Date().toString());
            if ((project = ALProject.open(mongoDataSoure, projectName)) == null) {
                project = ALProject.create(mongoDataSoure, projectName);
            }
            if (project == null) {
                throw new RuntimeException("Unable to create project for " + projectName);
            }

            //set the project datasource
            project.setDatasource(dataSource);

            //take initial snapshot of the datasource
            ALSnapshot snapshot = project.takeSnapshot("first snapshot");
            if (snapshot != null) {
                snapshot.reduceTo(100); //helper function to reduce snapshot size - N% of each table
                project.addSnapshot(snapshot);
                project.useSnapshot(snapshot);
                ALTable table = project.getTable(segment.getTableName());
                StringBuffer value = new StringBuffer();
                value.append(segment.getAttributeName());

                if(segment.getRangeFrom()!=null && segment.getRangeTo()!=null){
                    //For range  type segments
                    value.append(" between ");
                    try{
                        value.append(Integer.parseInt(segment.getRangeFrom())); // number type no single quotes
                    }catch(Exception e){
                        value.append("'").append(segment.getRangeFrom()).append("'"); // For varchar type
                    }
                    value.append(" and ");
                    try{
                        value.append(Integer.parseInt(segment.getRangeTo()));
                    }catch(Exception e){
                        value.append("'").append(segment.getRangeTo()).append("'");
                    }
                }else{
                    //For single value type of segments
                    value.append(segment.getOperator());
                    if(segment.isNumberType()){
                        value.append(segment.getAttributeValue());
                    }else{
                        value.append("'").append(segment.getAttributeValue()).append("'");
                    }
                }
                ALRDBSegment.createNew(project, table, value.toString(), segment.getSegmentName());
            }
        }finally {
            if(project!=null)
                project.close();
            counterService.increment("Create Segment stop: " + new Date());
        }
    }

    @Override
    public Map<String,List<String>> getSegments(String projectName) {
        ALProject project = null;
        try {
            Map<String,List<String>> segmentNames = new HashMap<>();

            if ((project = ALProject.open(mongoDataSoure, projectName)) == null) {
                throw new RuntimeException("Unable to find project for " + projectName);
            }

            //set the project datasource
            project.setDatasource(dataSource);

            ALSnapshot snapshot = project.findLatestSnapshot();
            if (snapshot != null) {
                project.useSnapshot(snapshot);
                BasicDBObject projectStructure = project.getStructrure();
                BasicDBList tablesList = (BasicDBList) projectStructure.get("Tables");
                for (String tablesKey : tablesList.keySet()) {
                    BasicDBObject tableInfo = (BasicDBObject) tablesList.get(tablesKey);
                    BasicDBList segmentsCollection = (BasicDBList) tableInfo.get("children");
                    String tableName = (String)tableInfo.get("name");
                    for (String segmentKey : segmentsCollection.keySet()) {
                        if(segmentNames.get(tableName)!=null){
                            segmentNames.get(tableName).add((String) ((BasicDBObject) segmentsCollection.get(segmentKey)).get("name"));
                        }else{
                            List<String> segments = new ArrayList<>();
                            segments.add((String) ((BasicDBObject) segmentsCollection.get(segmentKey)).get("name"));
                            segmentNames.put(tableName,segments);
                        }

                    }
                }
            }

            return segmentNames;
        }finally{
            if(project!=null)
                project.close();
        }
    }
    @Override
    public List<String> getProjects() {
        if (mongoDataSoure != null) {
            return mongoDataSoure.listProjects();
        }
        return null;
    }

    @Override
    public long getCount(String projectName, String tableName, String segment) {
        ALProject project = null;
        try {
            //create new project
            if ((project = ALProject.open(mongoDataSoure, projectName)) == null) {
                throw new RuntimeException("Unable to open project for " + projectName);
            }

            //set the project datasource
            project.setDatasource(dataSource);

            ALSnapshot snapshot = project.findLatestSnapshot();
            if (snapshot != null) {
                project.useSnapshot(snapshot);
                ALTable custTable = project.getTable(tableName);
                ALSegment segmentReference = (ALSegment) custTable.findChildByName(segment);
                if(segmentReference!=null){
                    return segmentReference.count();
                }else{
                    throw new RuntimeException("Unable to find the segment and its count");
                }
            }else{
                throw new RuntimeException("Unable to get the snapshot");
            }
        }finally {
            if(project!=null)
                project.close();
        }
    }

    public long getTargetCount(String projectName, List<KALTargetSegment> targetSegments){
        ALProject project = null;
        try {
            //create new project
            if ((project = ALProject.open(mongoDataSoure, projectName)) == null) {
                throw new RuntimeException("Unable to open project for " + projectName);
            }

            //set the project datasource
            project.setDatasource(dataSource);

            ALSnapshot snapshot = project.findLatestSnapshot();
            BCBitmap targetBitMap = null;
            String prevSegmentLogic = null;
            if (snapshot != null) {
                project.useSnapshot(snapshot);
                for(KALTargetSegment targetSegment : targetSegments){
                    ALTable table = project.getTable(targetSegment.getTableName());
                    ALSegment targetedSegment = (ALSegment) table.findChildByName(targetSegment.getSegmentName());
                    BCBitmap temp = project.getBitmap(targetedSegment);
                    if(targetBitMap == null){
                        targetBitMap = temp.copyBitmap("targetBitMap");
                    }else{
                        if(prevSegmentLogic.equalsIgnoreCase("AND")){
                            targetBitMap.and(temp);
                        }else{
                            targetBitMap.or(temp);
                        }
                    }
                    prevSegmentLogic = targetSegment.getLogic();
                }
                return targetBitMap.count();
            }else{
                throw new RuntimeException("Unable to get the snapshot");
            }
        }finally {
            if(project!=null)
                project.close();
        }
    }

    @Override
    public void joinTable(String projectName, String firstTable, String firstJoinColumn, String secondTable,
                          String secondJoinColumn){
        ALProject project = null;
        try {
            //create new project
            if ((project = ALProject.open(mongoDataSoure, projectName)) == null) {
                throw new RuntimeException("Unable to open project for " + projectName);
            }

            //set the project datasource
            project.setDatasource(dataSource);

            ALSnapshot snapshot = project.findLatestSnapshot();
            BCBitmap targetBitMap = null;
            String prevSegmentLogic = null;
            if (snapshot != null) {
                project.useSnapshot(snapshot);
                if (ALJoin.createJoin(project, firstTable, firstJoinColumn, secondTable, secondJoinColumn)) {
                    return;
                }else{
                    throw new RuntimeException("Unable to create join");
                }
            }else{
                throw new RuntimeException("Unable to get the snapshot");
            }
        }finally {
            if(project!=null)
                project.close();
        }
    }


    @Override
    public void destroy(){
        System.out.println("Inside destroy");
        KAL.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        KAL.start(kalProperties);
        if (KAL.ok() == false) {
            throw new RuntimeException("Unable to get the instance of KAL acclerator");
        }
    }
}
