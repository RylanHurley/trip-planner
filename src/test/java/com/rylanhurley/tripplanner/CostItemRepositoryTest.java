package com.rylanhurley.tripplanner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CostItemRepositoryTest {
    @Autowired TripGroupRepository groupRepository;
    @Autowired TripRepository tripRepository;
    @Autowired CostItemRepository costItemRepository;

    @Test
    void findsCostItemsByTrip() {
        TripGroup group = new TripGroup();
        group.setName("Ski crew");
        groupRepository.save(group);

        Trip trip = new Trip();
        trip.setName("Breck");
        trip.setTripGroup(group);
        tripRepository.save(trip);

        CostItem lodging = new CostItem();
        lodging.setName("Lodging");
        lodging.setAmount(new BigDecimal("1200.00"));
        lodging.setSplitType(SplitType.SPLIT);
        lodging.setTrip(trip);
        costItemRepository.save(lodging);

        List<CostItem> found = costItemRepository.findByTrip(trip);

        assertEquals(1, found.size());
        assertEquals("Lodging", found.get(0).getName());
        assertEquals(new BigDecimal("1200.00"), found.get(0).getAmount());
    }
}
