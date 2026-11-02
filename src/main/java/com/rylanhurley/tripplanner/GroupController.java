package com.rylanhurley.tripplanner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
@Controller
public class GroupController {
    private final TripGroupRepository groupRepository;
    private final TripRepository tripRepository;

    public GroupController(TripGroupRepository groupRepository, TripRepository tripRepository) {
        this.groupRepository = groupRepository;
        this.tripRepository = tripRepository;
    }

    @GetMapping("/groups/new")
    public String newGroupForm() {
        return "new-group";
    }

    @PostMapping("/groups")
    public String createGroup(@RequestParam String name) {
        if (name.isBlank()) {
            return "redirect:/groups/new";
        } else {
            TripGroup group = new TripGroup();
            group.setName(name);
            groupRepository.save(group);
            return "redirect:/g/" + group.getInviteToken();
        }
    }

    @GetMapping("/g/{token}")
    public String showGroup(@PathVariable String token, Model model) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) {
            return "redirect:/";
        }
        model.addAttribute("group", group);
        model.addAttribute("trips", tripRepository.findByTripGroup(group));
        return "group";
    }

    @PostMapping("/g/{token}/trips")
    public String createTrip(@PathVariable String token,
                             @RequestParam String name,
                             @RequestParam LocalDate startDate,
                             @RequestParam LocalDate endDate,
                             @RequestParam int targetSize,
                             @RequestParam LocalDate commitDeadline) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) {
            return "redirect:/";
        }
        if(endDate.isBefore(startDate)) {return "redirect:/g/" + token;}
        Trip trip = new Trip();
        trip.setName(name);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setTargetSize(targetSize);
        trip.setCommitDeadline(commitDeadline);
        trip.setTripGroup(group);
        tripRepository.save(trip);
        return "redirect:/g/" + token;
    }
}
