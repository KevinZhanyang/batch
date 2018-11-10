package com.synapse.batch.controller.api;

import com.synapse.batch.model.Job;
import com.synapse.batch.service.JobService;
import com.synapse.common.trans.BizHeader;
import com.synapse.common.trans.BizTrans;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提供给内部调用的创建日终的接口
 * Created by ivaneye on 17-3-8.
 */
@RestController
@RequestMapping("/batch/api")
public class JobApiController {

    private Logger logger = LoggerFactory.getLogger(JobApiController.class);

    @Autowired
    private JobService jobService;

    @ApiOperation(value = "创建Job", notes = "原/batch/job/api/v1/create")
    @RequestMapping(value = "/v1/job", method = RequestMethod.POST)
    public BizTrans<String> create(String url, String cron, String desc, String type) {
        BizTrans<String> bizTrans = new BizTrans<String>();
        BizHeader header = new BizHeader();
        bizTrans.setBizHead(header);
        try {
            logger.error("create job[url:{},cron:{},desc{},type:{}]!", url, cron, desc, type);
            Job job = new Job();
            job.setCron(cron);
            job.setUrl(url);
            if ("sync".equals(type)) {
                job.setBeanName("remoteJobSync");
            } else {
                job.setBeanName("remoteJobASync");
            }
            job.setJobGroup("system");
            job.setSchedName("system");
            job.setJobName(desc);//描述
            jobService.add(job);
            header.setBizRetCode("1000");
            header.setBizRetMsg(job.getRecId() + "");
            logger.error("create job[url:{},cron:{},desc{},type:{}] success!", url, cron, desc, type);
        } catch (Exception e) {
            logger.error("create job[url:{},cron:{},desc{},type:{}] error!", url, cron, desc, type, e);
            header.setBizRetCode("1001");
            header.setBizRetMsg(e.getMessage());
        }
        return bizTrans;
    }

    @ApiOperation(value = "根据URL删除Job", notes = "")
    @RequestMapping(value = "/v1/job", method = RequestMethod.DELETE)
    public int delete(String url) {
        try {
            logger.info("Start delete job url = {}", url);
            return jobService.deleteByUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
