package com.synapse.batch.mapper;

import com.synapse.batch.model.Job;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JobMapper {
    int deleteByPrimaryKey(Long recId);

    int insert(Job record);

    Job selectByPrimaryKey(@Param("recId") Long recId);

    int updateByPrimaryKey(Job record);

    void deleteByJobNameAndJobGroupAndSchedName(Job bean);

    void updateByJobNameAndJobGroupAndSchedName(Job bean);

    List<Job> listJob();

    List<Job> selectAllStartedJob();

    List<Job> selectByUrl(@Param("url") String url);
}