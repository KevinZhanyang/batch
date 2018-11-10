package com.synapse.batch.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

/**
 * 同步执行
 * Created by wangyifan on 2017/3/24.
 */
@Component
@DisallowConcurrentExecution
public class RemoteJobSync extends RemoteJob {}
