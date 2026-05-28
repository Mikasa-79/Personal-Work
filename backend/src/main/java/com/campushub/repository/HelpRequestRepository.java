package com.campushub.repository;

import com.campushub.entity.HelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {
    List<HelpRequest> findByStatusAndAuditStatus(String status, String auditStatus);
    List<HelpRequest> findByPublisherIdAndStatus(Long publisherId, String status);
}
