package com.rylanhurley.tripplanner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class GroupController {
    private final TripGroupRepository groupRepository;

    public GroupController(TripGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
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
}
