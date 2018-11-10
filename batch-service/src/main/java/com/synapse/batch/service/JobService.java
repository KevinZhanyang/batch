package com.synapse.batch.service;

import com.synapse.batch.mapper.JobMapper;
import com.synapse.batch.model.Job;
import com.synapse.batch.quartz.QuartzScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangyifan on 17-3-28.
 */
@Service
@Transactional
public class JobService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private QuartzScheduler quartzScheduler;

    public List<Job> list() {
        return jobMapper.listJob();
    }

    public void add(Job job) {
        job.setStatus(1);
        jobMapper.insert(job);
        quartzScheduler.newJob(job.getBeanName(), job.getUrl(), job.getRecId() + "", job.getCron(), job.getJobName());
    }

    public Job queryById(long recId) {
        return jobMapper.selectByPrimaryKey(recId);
    }

    public void update(Job job) {
        quartzScheduler.updateJob(job.getBeanName(), job.getRecId() + "", job.getCron(), job.getJobName());
        jobMapper.updateByPrimaryKey(job);
    }

    public void deleteById(long recId) {
        quartzScheduler.removeJob(recId + "");
        jobMapper.deleteByPrimaryKey(recId);
    }

    public void start(long recId) {
        Job job = jobMapper.selectByPrimaryKey(recId);
        job.setStatus(1);
        jobMapper.updateByPrimaryKey(job);
        quartzScheduler.startJob(recId + "", job.getCron());
    }

    public void stop(long recId) {
        Job job = jobMapper.selectByPrimaryKey(recId);
        job.setStatus(0);
        jobMapper.updateByPrimaryKey(job);
        quartzScheduler.stopJob(recId + "");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Job> list = jobMapper.selectAllStartedJob();
        if (list != null) {
            for (Job job : list) {
                quartzScheduler.newJob(job.getBeanName(), job.getUrl(), job.getRecId() + "", job.getCron(), job.getJobName());
            }
        }
    }

    public int deleteByUrl(String url) {
        List<Job> jobs = jobMapper.selectByUrl(url);
        logger.info("Find jobs [{}]",jobs);
        if (jobs != null && jobs.size() > 0) {
            int i = 0;
            for (Job job : jobs) {
                try {
                    deleteById(job.getRecId());
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return i;
        }
        return 0;
    }
}
