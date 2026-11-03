package com.rylanhurley.tripplanner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
@Controller
public class GroupController {
    private final TripGroupRepository groupRepository;
    private final TripRepository tripRepository;
    private final CostItemRepository costItemRepository;

    public GroupController(TripGroupRepository groupRepository, TripRepository tripRepository, CostItemRepository costItemRepository) {
        this.groupRepository = groupRepository;
        this.tripRepository = tripRepository;
        this.costItemRepository = costItemRepository;
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

    @GetMapping("/g/{token}/trips/{tripId}")
    public String showTrip(@PathVariable String token, @PathVariable Long tripId, Model model) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) { return "redirect:/"; }
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null || !trip.getTripGroup().getId().equals(group.getId())) { return "redirect:/g/" + token; }
        model.addAttribute("group", group);
        model.addAttribute("trip", trip);
        model.addAttribute("costs", costItemRepository.findByTrip(trip));
        return "trip";
    }

    @PostMapping("/g/{token}/trips/{tripId}/costs")
    public String addCost(@PathVariable String token, @PathVariable Long tripId,
                          @RequestParam String name, @RequestParam BigDecimal amount,
                          @RequestParam SplitType splitType) {
        BigDecimal val = new BigDecimal("10000");
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) { return "redirect:/"; }
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null || !trip.getTripGroup().getId().equals(group.getId())) { return "redirect:/g/" + token; }
        if (name.isBlank() || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(val) > 0) {
            return "redirect:/g/" + token + "/trips/" + tripId;
        }
        CostItem cost = new CostItem();
        cost.setName(name);
        cost.setAmount(amount);
        cost.setSplitType(splitType);
        cost.setTrip(trip);
        costItemRepository.save(cost);
        return "redirect:/g/" + token + "/trips/" + tripId;
    }
}
