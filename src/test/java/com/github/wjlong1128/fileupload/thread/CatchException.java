package com.github.wjlong1128.fileupload.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/22
 * @desc
 */
public class CatchException {

    @Test
    void testCompletable() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("bbb....");
            int i = 1 / 0;
        });

        System.out.println("aaa....");
        // 利用此方法感知到子线程的异常
        try {
            future.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testThread() {
        final AtomicReference<Throwable> exRef = new AtomicReference<>();
        System.out.println("aaa...");
        Thread thread = new Thread(() -> {
            System.out.println("bbb...");
            int i = 1 / 0;
        }, "b");
        // 设置异常处理器
        thread.setUncaughtExceptionHandler((currentThread, e) -> {
            exRef.set(e);
            System.out.println("线程b:" + currentThread.getName() + "  捕获到异常:" + e);
        });
        thread.start();
        try {
            thread.join();
            if (exRef.get() != null) {
                throw new RuntimeException(exRef.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
