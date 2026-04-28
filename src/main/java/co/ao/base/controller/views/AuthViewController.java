package co.ao.base.controller.views;

import co.ao.base.model.LoginRequest;
import co.ao.base.model.SignupRequest;
import co.ao.base.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index(Model model) {
        if (authService.isAuthenticated()) return "redirect:/dashboard";
        model.addAttribute("loginRequest", new LoginRequest());
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/";
    }

    @GetMapping("/inscricao")
    public String signupPage(Model model) {
        if (authService.isAuthenticated()) return "redirect:/dashboard";
        
        // Usando um nome diferente para evitar qualquer conflito
        model.addAttribute("signupForm", new SignupRequest());
        return "inscricao";
    }
    @GetMapping("/userLogin")
    public String userLogin() {
        return "redirect:/dashboard";
    }
}
