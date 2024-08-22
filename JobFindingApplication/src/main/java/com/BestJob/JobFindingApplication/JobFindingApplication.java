package com.BestJob.JobFindingApplication;

import com.BestJob.JobFindingApplication.controller.JobController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class JobFindingApplication{
	public static void main(String[] args) {
		SpringApplication.run(JobFindingApplication.class, args);
	}
}
