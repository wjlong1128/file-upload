package com.github.wjlong1128.fileupload.utils;

import cn.hutool.core.util.RuntimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/27
 * @desc
 */
public class ShellUtils {

    private ShellUtils() {

    }

    public static String executeCommand(String command) {
        try {
            ProcessBuilder builder = null;
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("win")) {
                builder = new ProcessBuilder().command("cmd", "/c", command);
            } else {
                builder = new ProcessBuilder().command("/bin/sh", "-c", command);
            }
            // 任何错误输出都将与标准输出合并，以便可以使用该方法 Process.getInputStream() 读取两者。
            builder.redirectErrorStream(true);
            Process process = builder.start();
            // 如有必要，使当前线程等待，直到此 Process 对象表示的进程终止。如果进程已终止，则此方法将立即返回。如果进程尚未终止，则调用线程将被阻塞，直到进程退出。
            process.waitFor();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("gbk")));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }
            process.waitFor();
            is.close();
            reader.close();
            process.destroy();
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("call error", e);
        }
    }

    public static String executeCommand(String path, String command) {
        try {
            ProcessBuilder builder = null;
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("win")) {
                builder = new ProcessBuilder().command("cmd", "/c", command).directory(new File(path));
            } else {
                builder = new ProcessBuilder().command("/bin/sh", "-c", command).directory(new File(path));
            }
            // 任何错误输出都将与标准输出合并，以便可以使用该方法 Process.getInputStream() 读取两者。
            builder.redirectErrorStream(true);
            Process process = builder.start();
            // 如有必要，使当前线程等待，直到此 Process 对象表示的进程终止。如果进程已终止，则此方法将立即返回。如果进程尚未终止，则调用线程将被阻塞，直到进程退出。
            process.waitFor();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("gbk")));
            StringBuilder result = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }
            process.waitFor();
            process.destroy();
            is.close();
            reader.close();
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("call error", e);
        }
    }

    public static void main(String[] args) {
        String str = RuntimeUtil.execForStr("ffmpeg");
        System.out.println(str);
    }
}
