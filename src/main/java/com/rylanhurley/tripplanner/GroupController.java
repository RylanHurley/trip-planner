package com.rylanhurley.tripplanner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Controller
public class GroupController {
    private final TripGroupRepository groupRepository;
    private final TripRepository tripRepository;
    private final CostItemRepository costItemRepository;
    private final ParticipantRepository participantRepository;

    public GroupController(TripGroupRepository groupRepository, TripRepository tripRepository,
                           CostItemRepository costItemRepository, ParticipantRepository participantRepository) {
        this.groupRepository = groupRepository;
        this.tripRepository = tripRepository;
        this.costItemRepository = costItemRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/groups/new")
    public String newGroupForm() {
        return "new-group";
    }

    @PostMapping("/groups")
    public String createGroup(@RequestParam String name) {
        if (name.isBlank() || name.length() > 50) {
            return "redirect:/groups/new";
        } else {
            TripGroup group = new TripGroup();
            group.setName(name);
            group.setInviteToken(TripGroup.makeInviteToken(name));
            groupRepository.save(group);
            return "redirect:/g/" + group.getInviteToken() + "?org=" + group.getOrganizerToken();
        }
    }

    @GetMapping("/g/{token}")
    public String showGroup(@PathVariable String token, @RequestParam(required = false) String org, Model model) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) {
            return "redirect:/";
        }
        model.addAttribute("group", group);
        model.addAttribute("trips", tripRepository.findByTripGroup(group));
        addOrganizer(model, group, org);
        return "group";
    }

    @PostMapping("/g/{token}/trips")
    public String createTrip(@PathVariable String token,
                             @RequestParam(required = false) String org,
                             @RequestParam String name,
                             @RequestParam LocalDate startDate,
                             @RequestParam LocalDate endDate,
                             @RequestParam int targetSize,
                             @RequestParam LocalDate commitDeadline) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) {
            return "redirect:/";
        }
        if (!group.isOrganizer(org)) { return "redirect:/g/" + token; }
        String back = "redirect:/g/" + token + "?org=" + org;
        if(endDate.isBefore(startDate)) {return back;}
        if (name.isBlank() || name.length() > 50 || targetSize < 1 || commitDeadline.isAfter(startDate)) {
            return back;
        }
        if(startDate.isBefore(LocalDate.now())) {return back;}
        Trip trip = new Trip();
        trip.setName(name);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setTargetSize(targetSize);
        trip.setCommitDeadline(commitDeadline);
        trip.setTripGroup(group);
        tripRepository.save(trip);
        return back;
    }

    @GetMapping("/g/{token}/trips/{tripId}")
    public String showTrip(@PathVariable String token, @PathVariable Long tripId,
                           @RequestParam(required = false) String org, Model model) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) { return "redirect:/"; }
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null || !trip.getTripGroup().getId().equals(group.getId())) { return "redirect:/g/" + token; }
        model.addAttribute("group", group);
        model.addAttribute("trip", trip);
        List<CostItem> costs = costItemRepository.findByTrip(trip);
        model.addAttribute("costs", costs);
        List<Participant> participants = participantRepository.findByTrip(trip);
        model.addAttribute("participants", participants);
        model.addAttribute("commitOpen", trip.isCommitOpen(LocalDate.now()));
        model.addAttribute("tripOn", trip.isOn(participants.size()));
        model.addAttribute("perPersonAtTarget", CostCalculator.perPerson(costs, trip.getTargetSize()));
        if (!participants.isEmpty()) {
            model.addAttribute("perPersonNow", CostCalculator.perPerson(costs, participants.size()));
        }
        Map<Integer, BigDecimal> priceTable = new LinkedHashMap<>();
        for (int people = 1; people <= trip.getTargetSize() + 2; people++) {
            priceTable.put(people, CostCalculator.perPerson(costs, people));
        }
        model.addAttribute("priceTable", priceTable);
        addOrganizer(model, group, org);
        return "trip";
    }

    @PostMapping("/g/{token}/trips/{tripId}/costs")
    public String addCost(@PathVariable String token, @PathVariable Long tripId,
                          @RequestParam(required = false) String org,
                          @RequestParam String name, @RequestParam BigDecimal amount,
                          @RequestParam SplitType splitType,
                          @RequestParam(required = false) String link){
        BigDecimal val = new BigDecimal("10000");
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) { return "redirect:/"; }
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null || !trip.getTripGroup().getId().equals(group.getId())) { return "redirect:/g/" + token; }
        if (!group.isOrganizer(org)) { return "redirect:/g/" + token + "/trips/" + tripId; }
        if (name.isBlank() || name.length() > 50 || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(val) > 0) {
            return "redirect:/g/" + token + "/trips/" + tripId + "?org=" + org;
        }
        boolean hasLink = link != null && !link.isBlank();
        if (hasLink && (!link.startsWith("https://") || link.length() > 255)) {
            return "redirect:/g/" + token + "/trips/" + tripId + "?org=" + org;
        }

        CostItem cost = new CostItem();
        cost.setName(name);
        if (hasLink) { cost.setLink(link); }
        cost.setAmount(amount);
        cost.setSplitType(splitType);
        cost.setTrip(trip);
        costItemRepository.save(cost);
        return "redirect:/g/" + token + "/trips/" + tripId + "?org=" + org;
    }

    @PostMapping("/g/{token}/trips/{tripId}/costs/{costId}")
    public String updateCost(@PathVariable String token, @PathVariable Long tripId, @PathVariable Long costId,
                             @RequestParam(required = false) String org,
                             @RequestParam String name, @RequestParam BigDecimal amount,
                             @RequestParam SplitType splitType,
                             @RequestParam(required = false) String link) {
        BigDecimal val = new BigDecimal("10000");
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) { return "redirect:/"; }
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null || !trip.getTripGroup().getId().equals(group.getId())) { return "redirect:/g/" + token; }
        if (!group.isOrganizer(org)) { return "redirect:/g/" + token + "/trips/" + tripId; }
        CostItem cost = costItemRepository.findById(costId).orElse(null);
        if (cost == null || !cost.getTrip().getId().equals(trip.getId())) {
            return "redirect:/g/" + token + "/trips/" + tripId + "?org=" + org;
        }
        if (name.isBlank() || name.length() > 50 || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(val) > 0) {
            return "redirect:/g/" + token + "/trips/" + tripId + "?org=" + org;
        }
        boolean hasLink = link != null && !link.isBlank();
        if (hasLink && (!link.startsWith("https://") || link.length() > 255)) {
            return "redirect:/g/" + token + "/trips/" + tripId + "?org=" + org;
        }
        cost.setName(name);
        cost.setAmount(amount);
        cost.setSplitType(splitType);
        cost.setLink(hasLink ? link : null);
        costItemRepository.save(cost);
        return "redirect:/g/" + token + "/trips/" + tripId + "?org=" + org;
    }

    @PostMapping("/g/{token}/trips/{tripId}/join")
    public String joinTrip(@PathVariable String token, @PathVariable Long tripId,
                           @RequestParam(required = false) String org, @RequestParam String name) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) { return "redirect:/"; }
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null || !trip.getTripGroup().getId().equals(group.getId())) { return "redirect:/g/" + token; }
        String back = "redirect:/g/" + token + "/trips/" + tripId + (group.isOrganizer(org) ? "?org=" + org : "");
        if (!trip.isCommitOpen(LocalDate.now())) {
            return back;
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty() || participantRepository.existsByTripAndNameIgnoreCase(trip, trimmed)|| trimmed.length() > 30) {
            return back;
        }
        Participant participant = new Participant();
        participant.setName(trimmed);
        participant.setTrip(trip);
        participantRepository.save(participant);
        return back;
    }

    private void addOrganizer(Model model, TripGroup group, String org) {
        boolean isOrganizer = group.isOrganizer(org);
        model.addAttribute("isOrganizer", isOrganizer);
        model.addAttribute("orgQuery", isOrganizer ? "?org=" + org : "");
    }
}