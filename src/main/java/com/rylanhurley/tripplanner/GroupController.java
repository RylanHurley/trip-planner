package com.rylanhurley.tripplanner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/g/{token}")
    public String showGroup(@PathVariable String token, Model model) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) {
            return "redirect:/";
        }
        model.addAttribute("group", group);
        return "group";
    }
}
