package com.rylanhurley.tripplanner;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TripGroup {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String inviteToken;

    public TripGroup() {
    }

}
