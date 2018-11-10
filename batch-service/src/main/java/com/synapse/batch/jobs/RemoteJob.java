package com.synapse.batch.jobs;

import com.synapse.common.sso.context.CookieContext;
import com.synapse.common.trans.BizTrans;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 执行Job
 * Created by wangyifan on 2017/3/24.
 */
@Component
@Scope(value = "prototype")
public class RemoteJob implements Job {


    private RestTemplate restTemplate  = new RestTemplate();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
        String url = (String) map.get("url");
        logger.info("Start Invoke url[{}] !", url);
        CookieContext.setCookie("quartz");
        String result = restTemplate.getForObject(url, String.class);
        logger.info("Invoke url[{}] result is {}", url, result);
    }


}
