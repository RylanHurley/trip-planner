package com.rylanhurley.tripplanner;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TripGroupRepository extends JpaRepository<TripGroup, Long> {

    TripGroup findByInviteToken(String inviteToken);

}
