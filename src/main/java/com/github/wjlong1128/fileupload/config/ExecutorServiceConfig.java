package com.github.wjlong1128.fileupload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/29
 * @desc desc.
 */
@Data
@ConfigurationProperties("file-server.executor")
@Configuration
public class ExecutorServiceConfig {

    private int coreSize = Runtime.getRuntime().availableProcessors() * 2;
    private int maxPoolSize = Integer.MAX_VALUE;
    private int keepAliveSeconds = 60;
    private int queueCapacity = Integer.MAX_VALUE;

    private Class<? extends RejectedExecutionHandler> rejectedExecutionHandler = ThreadPoolExecutor.CallerRunsPolicy.class;

    @Bean("executorService")
    public ExecutorService executorService() throws InstantiationException, IllegalAccessException {
        return new ThreadPoolExecutor(
                coreSize,
                maxPoolSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueCapacity),
                new DefaultThreadFactory(),
                rejectedExecutionHandler.newInstance()
        );
    }

    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "file-server-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
