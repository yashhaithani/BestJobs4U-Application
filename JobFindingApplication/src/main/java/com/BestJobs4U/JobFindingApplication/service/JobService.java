package com.BestJobs4U.JobFindingApplication.service;

import com.BestJobs4U.JobFindingApplication.entity.Job;
import com.BestJobs4U.JobFindingApplication.repository.JobRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public JobService(JobRepository jobRepository, JavaMailSender mailSender) {
        this.jobRepository = jobRepository;
        this.mailSender = mailSender;
    }

    public void addJob(Job job) {
        try {
            jobRepository.save(job);
            logger.info("Job added successfully: {}", job.getJobDesignation());
        } catch (Exception e) {
            logger.error("Error adding job: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add job", e);
        }
    }

    public Job getJobById(Long id) {
        try {
            Optional<Job> job = jobRepository.findById(id);
            if (job.isPresent()) {
                logger.info("Job found with id: {}", id);
                return job.get();
            } else {
                logger.warn("Job not found with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching job with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch job", e);
        }
    }

    public List<Job> getAllJobs() {
        try {
            List<Job> jobs = jobRepository.findAll();
            logger.info("Fetched {} jobs", jobs.size());
            return jobs;
        } catch (Exception e) {
            logger.error("Error fetching all jobs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch all jobs", e);
        }
    }

    public boolean updateJob(Long id, Job updatedJob) {
        try {
            Optional<Job> jobOptional = jobRepository.findById(id);
            if (jobOptional.isPresent()) {
                Job existingJob = jobOptional.get();
                existingJob.setJobDesignation(updatedJob.getJobDesignation());
                existingJob.setJobDescription(updatedJob.getJobDescription());
                existingJob.setKeyWords(updatedJob.getKeyWords());
                existingJob.setCompanyName(updatedJob.getCompanyName());
                existingJob.setWorkExperience(updatedJob.getWorkExperience());
                existingJob.setSalary(updatedJob.getSalary());

                jobRepository.save(existingJob);
                logger.info("Job updated successfully: {}", id);
                return true;
            }
            logger.warn("Job not found for update: {}", id);
            return false;
        } catch (Exception e) {
            logger.error("Error updating job {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update job", e);
        }
    }

    public boolean deleteJobById(Long id) {
        try {
            if (jobRepository.existsById(id)) {
                jobRepository.deleteById(id);
                logger.info("Job deleted successfully: {}", id);
                return true;
            }
            logger.warn("Job not found for deletion: {}", id);
            return false;
        } catch (Exception e) {
            logger.error("Error deleting job {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete job", e);
        }
    }

    public List<Job> searchJobs(String searchTerm) {
        try {
            List<Job> jobs = jobRepository.searchJobsByTerm(searchTerm);
            logger.info("Found {} jobs for search term: {}", jobs.size(), searchTerm);
            return jobs;
        } catch (Exception e) {
            logger.error("Error searching jobs with term {}: {}", searchTerm, e.getMessage(), e);
            throw new RuntimeException("Failed to search jobs", e);
        }
    }

    public List<Job> advancedFilter(String keyword, Long minSalary, Long maxSalary, Integer minExperience, Integer maxExperience) {
        try {
            List<Job> filteredJobs = jobRepository.findAll().stream()
                    .filter(job -> (keyword == null ||
                            job.getJobDesignation().toLowerCase().contains(keyword.toLowerCase()) ||
                            job.getCompanyName().toLowerCase().contains(keyword.toLowerCase()) ||
                            job.getJobDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                            job.getKeyWords().toLowerCase().contains(keyword.toLowerCase())))
                    .filter(job -> (minSalary == null || job.getSalary() >= minSalary))
                    .filter(job -> (maxSalary == null || job.getSalary() <= maxSalary))
                    .filter(job -> (minExperience == null || job.getWorkExperience() >= minExperience))
                    .filter(job -> (maxExperience == null || job.getWorkExperience() <= maxExperience))
                    .collect(Collectors.toList());
            logger.info("Advanced filter returned {} jobs", filteredJobs.size());
            return filteredJobs;
        } catch (Exception e) {
            logger.error("Error in advanced filter: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to perform advanced filter", e);
        }
    }

    public boolean applyForJob(Long jobId, String applicantEmail) {
        try {
            Job job = jobRepository.findById(jobId).orElse(null);
            if (job != null) {
                sendEmailNotification(job, applicantEmail);
                logger.info("Job application successful for job {} by applicant {}", jobId, applicantEmail);
                return true;
            }
            logger.warn("Job not found for application: {}", jobId);
            return false;
        } catch (Exception e) {
            logger.error("Error applying for job {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Failed to apply for job", e);
        }
    }

    private void sendEmailNotification(Job job, String applicantEmail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(applicantEmail);
            helper.setSubject("Application Received for " + job.getJobDesignation());

            // Verify the image resource
            Resource imageResource = new ClassPathResource("static/images/emailSignature.png");
            if (!imageResource.exists()) {
                logger.error("Image resource not found: {}", imageResource.getFilename());
                throw new RuntimeException("Email signature image not found");
            }
            helper.addAttachment("emailSignature.jpg", imageResource);

            // Verify the PDF resource
            Resource pdfResource = new ClassPathResource("static/pdfs/interviewDetails.pdf");
            if (!pdfResource.exists()) {
                logger.error("PDF resource not found: {}", pdfResource.getFilename());
                throw new RuntimeException("Interview details PDF not found");
            }
            helper.addAttachment("interviewDetails.pdf", pdfResource);

            helper.setText(
                    "Dear Applicant,\n\n" +
                            "Thank you for applying for the " + job.getJobDesignation() + " position at " + job.getCompanyName() + ". We have received your application and appreciate your interest in joining our team.\n\n" +
                            "Please find attached the job description for your reference. We encourage you to review it to better understand the role and the responsibilities associated with it.\n\n" +
                            "Our hiring team is currently reviewing all applications. If your qualifications match our requirements, we will contact you to discuss the next steps in the hiring process.\n\n" +
                            "Thank you once again for your interest in " + job.getCompanyName() + ". We look forward to the possibility of working together.\n\n" +
                            "Best regards,\n" +
                            "[Your Name]\n" +
                            "HR Department\n" +
                            job.getCompanyName()
            );

            mailSender.send(mimeMessage);
            logger.info("Email notification sent successfully to {}", applicantEmail);
        } catch (MessagingException e) {
            logger.error("Error sending email notification to {} for job {}: {}", applicantEmail, job.getJobDesignation(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email notification", e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending email to {}: {}", applicantEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

}