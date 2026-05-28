package com.campushub.repository;

import com.campushub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByRequesterIdOrProviderId(Long requesterId, Long providerId);
}
