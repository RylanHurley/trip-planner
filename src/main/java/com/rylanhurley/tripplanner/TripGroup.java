package com.rylanhurley.tripplanner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.UUID;


@Entity
public class TripGroup {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Column(unique = true)
    private String inviteToken = UUID.randomUUID().toString();

    public TripGroup() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getInviteToken() {
        return inviteToken;
    }


}
