package com.wallace.demo.services;

import java.io.IOException;

public interface ShellCommandExecutorService {
	String executeCommand(String command, int timeoutInSeconds) throws InterruptedException, IOException;

}
