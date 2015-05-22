package com.yesmail.resources;

import com.yesmail.model.KALSegment;
import com.yesmail.model.TableJoinInfo;
import com.yesmail.model.TargetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.yesmail.service.KALAccleratorService;



@RestController
public class KALRestResources {

    @Autowired
    KALAccleratorService kalAccleratorService;

    @RequestMapping("/")
    public String index() {
        return "This is a KAL proxy server endpoint";
    }

    @RequestMapping(value="/projects", method= RequestMethod.GET)
    public @ResponseBody
    List<String> getProjectNames(){
        return kalAccleratorService.getProjects();
    }

    @RequestMapping(value="/project/{projectName}/segments", method= RequestMethod.GET)
    public @ResponseBody Map<String,List<String>> getSegments(@PathVariable String projectName){
        return kalAccleratorService.getSegments(projectName);
    }

    @RequestMapping(value="/project/{projectName}/table/{tableName}/segment/{segmentName}/count", method= RequestMethod.GET)
    public @ResponseBody long getSegmentCount(@PathVariable String projectName, @PathVariable String tableName, @PathVariable String segmentName){
        return kalAccleratorService.getCount(projectName, tableName, segmentName);
    }

    @RequestMapping(value="/project/{projectName}/addSegment", method= RequestMethod.POST)
    public @ResponseBody String createSegment(@PathVariable String projectName, @RequestBody KALSegment segment){
        kalAccleratorService.createSegment(projectName, segment);
        return "Success";
    }

    @RequestMapping(value="/project/{projectName}/target/count", method= RequestMethod.POST)
    public @ResponseBody long getTargetCount(@PathVariable String projectName, @RequestBody TargetDefinition targetDefinition){
        return kalAccleratorService.getTargetCount(projectName, targetDefinition.getTargetSegments());
    }

    @RequestMapping(value="/project/{projectName}/join", method= RequestMethod.POST)
    public @ResponseBody void createTableJoin(@PathVariable String projectName, @RequestBody TableJoinInfo joinInformation){
        kalAccleratorService.joinTable(projectName, joinInformation.getFirstTableName(),
                joinInformation.getFirstJoinColumn(), joinInformation.getSecondTableName(), joinInformation.getSecondJoinColumn());
    }

}
