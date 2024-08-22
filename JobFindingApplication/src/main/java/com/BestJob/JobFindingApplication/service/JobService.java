package com.BestJob.JobFindingApplication.service;

import com.BestJob.JobFindingApplication.entity.Job;
import com.BestJob.JobFindingApplication.repository.JobRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class JobService {
    private final JobRepository jobRepository;
    private final JavaMailSender mailSender;
    @Autowired
    public JobService(JobRepository jobRepository, JavaMailSender mailSender) {
        this.jobRepository = jobRepository;
        this.mailSender = mailSender;
    }

    public void addJob(Job job) {
        jobRepository.save(job);
    }

    public List<Job> searchJobs(String searchTerm) {
        return jobRepository.searchJobsByTerm(searchTerm);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

/*
    GET /api/jobs/advancefilter?keyword=Developer
    GET /api/jobs/advancefilter?minSalary=1500000&maxSalary=2000000
    GET /api/jobs/advancefilter?minExperience=3&maxExperience=5
    GET /api/jobs/advancefilter?keyword=Engineer&minSalary=1500000&maxSalary=2000000
    GET /api/jobs/advancefilter?keyword=Developer&minSalary=1500000&maxSalary=2000000&minExperience=3&maxExperience=5
    GET /api/jobs/advancefilter    ->Fetch all Records
*/

    public List<Job> advancedFilter(String keyword, Long minSalary, Long maxSalary, Integer minExperience, Integer maxExperience) {
        return jobRepository.findAll().stream()
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
    }

    public boolean applyForJob(Long jobId, String applicantEmail) {
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job != null) {
            // Logic to save the job application would go here

            // Send email notification
            sendEmailNotification(job, applicantEmail);
            return true;
        }
        return false;
    }

    private void sendEmailNotification(Job job, String applicantEmail) {
        // Create a MimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            // Create a MimeMessageHelper
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(applicantEmail);
            helper.setSubject("Application Received for " + job.getJobDesignation());

            // Attach image using ClassPathResource
            Resource imageResource = new ClassPathResource("static/images/emailSignature.png");
            helper.addAttachment("emailSignature.jpg", imageResource);

            // Email Content
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

            // Attach PDF using ClassPathResource
            Resource pdfResource = new ClassPathResource("static/pdfs/interviewDetails.pdf");
            helper.addAttachment("interviewDetails.pdf", pdfResource);

            // Send the email
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
    }

    public boolean deleteJobById(Long id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return true;
        }
        return false;
    }


}

