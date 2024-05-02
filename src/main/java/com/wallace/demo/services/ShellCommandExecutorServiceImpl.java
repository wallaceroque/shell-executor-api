package com.wallace.demo.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

@Service
public class ShellCommandExecutorServiceImpl implements ShellCommandExecutorService {
	
	@Override
	public String executeCommand(String command, int timeoutInSeconds) throws InterruptedException, IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
		final Process process = processBuilder.start();
		final int pid = getUnixProcessPid(process);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> exitCodeFuture = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return process.waitFor();
            }
        });
        Future<?> outputFuture = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    printOutput(process.getInputStream());
                } catch (IOException e) {
                	String msg = "******************** Error when output about process " + pid;
                	System.out.println(msg);
                    e.printStackTrace();
                }
            }
        });

        try {
        	String msg = "O processo " + pid + " foi executado com sucesso!";
            exitCodeFuture.get(timeoutInSeconds, TimeUnit.SECONDS);
            System.out.println(msg);
            return msg;
        } catch (TimeoutException e) {
            process.destroy();
            String msg = "Tempo limite atingido. O processo " + pid + " foi encerrado.";
            System.out.println(msg);
            return msg;
        } catch (ExecutionException e) {
        	e.printStackTrace();
        	return "Erro durante a execução do comando.";
        } finally {
            executor.shutdownNow();
        }
	}
	
	private void printOutput(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }
	
	private static int getUnixProcessPid(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return f.getInt(process);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

}
