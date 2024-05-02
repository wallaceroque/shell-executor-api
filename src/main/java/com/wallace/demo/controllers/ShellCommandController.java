package com.wallace.demo.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wallace.demo.services.ShellCommandExecutorService;

@RestController
public class ShellCommandController {
	
	private final ShellCommandExecutorService shellCommandExecutorService;
	
	@Autowired
    public ShellCommandController(ShellCommandExecutorService shellCommandExecutorService) {
        this.shellCommandExecutorService = shellCommandExecutorService;
    }

    @RequestMapping(value = "/execute", method = RequestMethod.GET)
    public String executeShellCommand(@RequestParam("timeout") int timeoutInSeconds, @RequestParam("duration") int duration) throws InterruptedException {
        // Implementação do método que executa o comando shell com o tempo limite
    	try {
			return shellCommandExecutorService.executeCommand("./shell_command.sh " + duration, timeoutInSeconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
    }
}
