package com.BestJobs4U.JobFindingApplication.controller;

import com.BestJobs4U.JobFindingApplication.entity.Job;
import com.BestJobs4U.JobFindingApplication.service.JobService;
import com.BestJobs4U.JobFindingApplication.utils.GlobalExceptionHandler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/best-jobs4u/jobs")
@RequiredArgsConstructor
public class JobController {
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
    private final JobService jobService;

    @PostMapping("/add")
    public ResponseEntity<String> addJob(@RequestBody Job job) {
        jobService.addJob(job);
        logger.info("Job added successfully: {}", job.getJobDesignation());
        return ResponseEntity.ok("Job added successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        if (job == null) {
            throw new ResourceNotFoundException("Job not found with ID: " + id);
        }
        logger.info("Job retrieved successfully: {}", id);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/all-jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        logger.info("Retrieved {} jobs", jobs.size());
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateJob(@PathVariable Long id, @RequestBody Job updatedJob) {
        boolean isUpdated = jobService.updateJob(id, updatedJob);
        if (!isUpdated) {
            throw new ResourceNotFoundException("Job not found with ID: " + id);
        }
        logger.info("Job updated successfully: {}", id);
        return ResponseEntity.ok("Job updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        boolean isRemoved = jobService.deleteJobById(id);
        if (!isRemoved) {
            throw new ResourceNotFoundException("Job not found with ID: " + id);
        }
        logger.info("Job deleted successfully: {}", id);
        return ResponseEntity.ok("Job deleted successfully!");
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<Job>> searchJobs(@PathVariable String term) {
        List<Job> jobs = jobService.searchJobs(term);
        logger.info("Search completed for term '{}', found {} jobs", term, jobs.size());
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/advance-filter")
    public ResponseEntity<List<Job>> advancedFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience) {
        List<Job> jobs = jobService.advancedFilter(keyword, minSalary, maxSalary, minExperience, maxExperience);
        logger.info("Advanced filter completed, found {} jobs", jobs.size());
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/apply/{id}")
    public ResponseEntity<String> applyForJob(@PathVariable Long id, @RequestParam String applicantEmail) {
        boolean applied = jobService.applyForJob(id, applicantEmail);
        if (!applied) {
            throw new ResourceNotFoundException("Job not found with ID: " + id);
        }
        logger.info("Application submitted successfully for job {} by applicant {}", id, applicantEmail);
        return ResponseEntity.ok("Application submitted successfully!");
    }
}
