package com.BestJob.JobFindingApplication.controller;

import com.BestJob.JobFindingApplication.entity.Job;
import com.BestJob.JobFindingApplication.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bestjob/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Search jobs by designation, company name, job description, or keywords
    @GetMapping("/search/{term}")
    public ResponseEntity<List<Job>> searchJobs(@PathVariable String term) {
        List<Job> jobs = jobService.searchJobs(term);
        return ResponseEntity.ok(jobs);
    }

    // Get all jobs
    @GetMapping("/alljobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    // Advanced filter for jobs by keyword, salary range, and experience range
    @GetMapping("/advancefilter")
    public ResponseEntity<List<Job>> advancedFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience) {

        List<Job> jobs = jobService.advancedFilter(keyword, minSalary, maxSalary, minExperience, maxExperience);
        return ResponseEntity.ok(jobs);
    }

    // Add a new job
    @PostMapping("/add")
    public ResponseEntity<String> addJob(@RequestBody Job job) {
        jobService.addJob(job);
        return ResponseEntity.ok("Job added successfully!");
    }

    // Delete a job by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        boolean isRemoved = jobService.deleteJobById(id);
        if (!isRemoved) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found with ID: " + id);
        }
        return ResponseEntity.ok("Job deleted successfully!");
    }

    // Apply for a job by ID and send an email notification
    @PostMapping("/apply/{id}")
    public ResponseEntity<String> applyForJob(
            @PathVariable Long id,
            @RequestParam String applicantEmail) {
        boolean applied = jobService.applyForJob(id, applicantEmail);
        if (applied) {
            return ResponseEntity.ok("Application submitted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
        }
    }
}
