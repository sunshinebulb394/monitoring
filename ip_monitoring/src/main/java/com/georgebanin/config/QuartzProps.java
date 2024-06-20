package com.georgebanin.config;

import io.smallrye.common.constraint.Nullable;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

import java.util.Optional;


@ConfigMapping(prefix = "org.quartz",namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface QuartzProps {

    Scheduler scheduler();
    ThreadPool threadPool();
    JobStore jobStore();
    Datasource dataSource();



    interface Scheduler{
        String instanceName();
        String instanceId();

    }

    interface ThreadPool{
        @WithName("class")
        String className();
        String threadCount();

    }

    interface JobStore{
        @WithName("class")
        String className();

        String dataSource();

        String driverDelegateClass();
    }

    interface Datasource{

        QuartzDatasource quartzDataSource();
    }

    interface QuartzDatasource{
        String driver();
        String URL();
        String user();
        String password();
    }

}
