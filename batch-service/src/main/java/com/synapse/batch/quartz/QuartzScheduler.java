package com.synapse.batch.quartz;

import com.synapse.batch.jobs.RemoteJob;
import com.synapse.batch.mapper.JobMapper;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.stereotype.Component;
import org.quartz.Scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;


/**
 * Created by wangyifan on 2017/3/24.
 */
@Component
public class QuartzScheduler implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(QuartzScheduler.class);

    private Scheduler scheduler;

    @Autowired
    private JobMapper jobMapper;

    private ConfigurableApplicationContext applicationContext;

    @Value("${quartz.job.scanPackage}")
    private String scanPackage;

    public QuartzScheduler() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            logger.error("Create QuartzScheduler Error!", e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    /**
     * 创建一个新任务，并添加到调度器中，id=beanName
     *
     * @param beanName
     * @param cron
     * @param desc
     */
//    public void newJob(String beanName, String cron, String desc) {
//        newJob(beanName, beanName, cron, desc);
//    }

    /**
     * 创建一个新任务，并添加到调度器中
     *
     * @param beanName
     * @param id
     * @param cron
     * @param desc
     */
    public void newJob(String beanName, String url, String id, String cron, String desc) {
        Job obj = findJobBean(beanName);

        JobDetail jobBean = newJob(id, obj, desc);
        jobBean.getJobDataMap().put("url", url);

        Trigger trigBean = newTrigger(id, cron);

        try {
            scheduler.scheduleJob(jobBean, trigBean);
        } catch (Exception e) {
            logger.error("New Job Error!", e);
        }
    }

    /**
     * 更新Job,id=beanName,主要是为了更新cron
     *
     * @param beanName
     * @param cron
     * @param desc
     */
    public void updateJob(String beanName, String cron, String desc) {
        updateJob(beanName, beanName, cron, desc);
    }

    /**
     * 更新Job，这里的beanName可以是一个全新的Bean
     *
     * @param beanName
     * @param id
     * @param cron
     * @param desc
     */
    public void updateJob(String beanName, String id, String cron, String desc) {
        Job obj = findJobBean(beanName);

        JobDetail jobBean = newJob(id, obj, desc);
        Trigger trigBean = newTrigger(id, cron);

        try {
            scheduler.addJob(jobBean, true);
            scheduler.rescheduleJob(trigBean.getKey(), trigBean);
        } catch (Exception e) {
            logger.error("UpdateJob Error!", e);
        }
    }

    /**
     * 停止Job
     *
     * @param id
     */
    public void stopJob(String id) {
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(id));
        } catch (SchedulerException e) {
            logger.error("StopJob Error!", e);
        }
    }

    /**
     * 启动Job
     *
     * @param id
     * @param cron
     */
    public void startJob(String id, String cron) {
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id)
                .startNow().forJob(JobKey.jobKey(id)).withSchedule(cronSchedule(cron)).build();
        try {
            scheduler.scheduleJob(trigger);
        } catch (SchedulerException e) {
            logger.error("StartJob Error!", e);
        }
    }

    /**
     * 删除Job
     *
     * @param id
     */
    public void removeJob(String id) {
        JobKey jk = JobKey.jobKey(id);

        try {
            com.synapse.batch.model.Job bean = new com.synapse.batch.model.Job();
            bean.setJobName(jk.getName());
            bean.setJobGroup(jk.getGroup());
            bean.setSchedName(scheduler.getSchedulerName());
            jobMapper.deleteByJobNameAndJobGroupAndSchedName(bean);
            scheduler.deleteJob(jk);
        } catch (SchedulerException e) {
            logger.error("RemoveJob Error!", e);
        }
    }

    private Trigger newTrigger(String id, String cron) {
        return TriggerBuilder.newTrigger().withIdentity(id)
                .startNow().withSchedule(cronSchedule(cron)).build();
    }

    private JobDetail newJob(String id, Job obj, String desc) {
        if (desc == null) {
            desc = id;
        }
        return JobBuilder.newJob(obj.getClass())
                .storeDurably().withDescription(desc).withIdentity(id).build();
    }

    /**
     * 检查Spring上下文中是否有此Bean，如果没有重新扫描指定的包
     *
     * @param beanName
     * @return
     */
    private Job findJobBean(String beanName) {
        if (!applicationContext.containsBean(beanName)) {
            ClassPathBeanDefinitionScanner scanner =
                    new ClassPathBeanDefinitionScanner(
                            (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory());
            scanner.scan(scanPackage);
        }
        return applicationContext.getBean(beanName, Job.class);
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void addJob(JobDetail jobDetail, boolean flag) throws SchedulerException {
        scheduler.addJob(jobDetail, flag);
    }

    public void rescheduleJob(TriggerKey triggerKey, Trigger trigger) throws SchedulerException {
        scheduler.rescheduleJob(triggerKey, trigger);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JobApplicationContext jobApplicationContext = new JobApplicationContext();
        jobApplicationContext.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        scheduler.setJobFactory(jobApplicationContext);
        this.scheduler.start();
    }
}