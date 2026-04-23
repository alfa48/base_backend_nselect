package co.ao.base.controller.views;

import co.ao.base.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private AuthService authService;

    @GetMapping("/dashboard")
    public String dashboard() {
        if (!authService.isAuthenticated()) return "redirect:/";
        return "fiinika/dashboard";
    }
}
