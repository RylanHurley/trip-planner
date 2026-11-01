package com.rylanhurley.tripplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TripGroupRepositoryTest {

    @Autowired
    TripGroupRepository groupRepository;

    @Test
    void findsGroupByInviteToken() {
        // Arrange
        TripGroup group = new TripGroup();
        group.setName("Ski crew");

        // Act
        groupRepository.save(group);
        TripGroup found = groupRepository.findByInviteToken(group.getInviteToken());

        // Assert
        assertEquals("Ski crew", found.getName());
    }
}
