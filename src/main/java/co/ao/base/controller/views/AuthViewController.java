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
        model.addAttribute("signupForm", new SignupRequest());
        return "inscricao";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                return "redirect:/admin/dashboard?success=Login+efetuado+com+sucesso";
            }
            
            boolean isParceiro = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARCEIRO"));
            
            if (isParceiro) {
                return "redirect:/parceiro/dashboard?success=Login+efetuado+com+sucesso";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/userLogin")
    public String userLogin() {
        return "redirect:/dashboard";
    }

    @GetMapping("/noauth")
    public String loginFailed() {
        return "redirect:/?error=Credenciais incorretas. Verifique o seu email e senha.";
    }
}
