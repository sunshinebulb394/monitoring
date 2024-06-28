package com.georgebanin.factory;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


@RequiredArgsConstructor
@Data
public class CustomThreadFactory implements ThreadFactory {

    private final String prefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = defaultFactory.newThread(r);
        thread.setName(prefix + "-thread-" + threadNumber.getAndIncrement());
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }


}
