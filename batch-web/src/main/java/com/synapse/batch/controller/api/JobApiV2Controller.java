package com.synapse.batch.controller.api;

import com.synapse.batch.model.Job;
import com.synapse.batch.param.JobParam;
import com.synapse.batch.service.JobService;
import com.synapse.common.exception.BusinessException;
import com.synapse.common.trans.Result;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供给内部调用的创建日终的接口
 * Created by ivaneye on 18-1-18.
 */
@RestController
@RequestMapping("/batch/api")
public class JobApiV2Controller {

    private Logger logger = LoggerFactory.getLogger(JobApiV2Controller.class);

    @Autowired
    private JobService jobService;

    @ApiOperation(value = "创建Job", notes = "创建Job")
    @RequestMapping(value = "/v2/job", method = RequestMethod.POST
            , produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
    public Result create(@RequestBody JobParam param) {
        try {
            logger.error("create job[url:{},cron:{},desc{},type:{}]!", param.getUrl(), param.getCron(), param.getDesc(), param.getType());
            Job job = new Job();
            job.setCron(param.getCron());
            job.setUrl(param.getUrl());
            if ("sync".equals(param.getType())) {
                job.setBeanName("remoteJobSync");
            } else {
                job.setBeanName("remoteJobASync");
            }
            job.setJobGroup("system");
            job.setSchedName("system");
            job.setJobName(param.getDesc());//描述
            jobService.add(job);
            logger.error("create job[url:{},cron:{},desc{},type:{}] success!", param.getUrl(), param.getCron(), param.getDesc(), param.getType());
            return Result.ok(job.getRecId());
        } catch (BusinessException e) {
            logger.error("create job[url:{},cron:{},desc{},type:{}] error!", param.getUrl(), param.getCron(), param.getDesc(), param.getType(), e);
            return Result.error(e);
        } catch (Exception e) {
            logger.error("create job[url:{},cron:{},desc{},type:{}] error!", param.getUrl(), param.getCron(), param.getDesc(), param.getType(), e);
            return Result.error(500, e.getMessage());
        }
    }

    @ApiOperation(value = "根据URL删除Job", notes = "")
    @RequestMapping(value = "/v2/job", method = RequestMethod.DELETE)
    public Result delete(String url) {
        try {
            logger.info("Start delete job url = {}", url);
            int code = jobService.deleteByUrl(url);
            return Result.ok(code);
        } catch (BusinessException e) {
            logger.error("delete job[url:{}] error!", url, e);
            return Result.error(e);
        } catch (Exception e) {
            logger.error("delete job[url:{}] error!", url, e);
            return Result.error(500, e.getMessage());
        }
    }
}
