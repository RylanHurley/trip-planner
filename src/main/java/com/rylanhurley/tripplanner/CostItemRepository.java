package com.rylanhurley.tripplanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostItemRepository extends JpaRepository<CostItem, Long> {
    List<CostItem> findByTrip(Trip trip);
}
