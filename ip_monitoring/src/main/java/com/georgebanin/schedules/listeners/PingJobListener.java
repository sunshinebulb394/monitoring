package com.georgebanin.schedules.listeners;

import com.georgebanin.model.PingResult;
import com.georgebanin.repoository.PingResultRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class PingJobListener implements JobListener {

    PingResultRepository pingResultRepository;
    @Override
    public String getName() {
        return "Ping JobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
      var list = (List<PingResult>) context.getMergedJobDataMap().get("pingResultList");
        System.out.println(list.size());
    }

}
