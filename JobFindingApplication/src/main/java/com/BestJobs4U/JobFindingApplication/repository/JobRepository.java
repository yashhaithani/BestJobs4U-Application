package com.BestJobs4U.JobFindingApplication.repository;

import com.BestJobs4U.JobFindingApplication.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT j FROM Job j WHERE LOWER(j.jobDesignation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(j.jobDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(j.keyWords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Job> searchJobsByTerm(@Param("searchTerm") String searchTerm);
}
