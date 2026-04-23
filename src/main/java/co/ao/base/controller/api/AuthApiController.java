package co.ao.base.controller.api;

import co.ao.base.model.LoginRequest;
import co.ao.base.model.SignupRequest;
import co.ao.base.model.UserDTO;
import co.ao.base.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

@Controller
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, Model model) {
        try {
            UserDTO user = authService.login(loginRequest);
            if (user != null) {
                return "redirect:/dashboard?loginSuccess=true";
            }
            model.addAttribute("error", "Credenciais inválidas. Verifique os dados.");
        } catch (HttpStatusCodeException e) {
            model.addAttribute("error", "Erro na API: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            model.addAttribute("error", "Não foi possível conectar ao servidor. Verifique o URL ou a sua conexão.");
        } catch (Exception e) {
            model.addAttribute("error", "Ocorreu um erro inesperado: " + e.getMessage());
        }
        
        model.addAttribute("loginRequest", loginRequest);
        return "index";
    }

    @PostMapping("/inscricao")
    public String register(@ModelAttribute("signupForm") SignupRequest signupRequest, Model model) {
        try {
            UserDTO user = authService.register(signupRequest);
            if (user != null) {
                return "redirect:/dashboard";
            }
            model.addAttribute("error", "Não foi possível criar a conta.");
        } catch (HttpStatusCodeException e) {
            model.addAttribute("error", "Erro ao registrar: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            model.addAttribute("error", "Falha de conexão com o servidor de registro.");
        } catch (Exception e) {
            model.addAttribute("error", "Erro inesperado: " + e.getMessage());
        }
        
        model.addAttribute("signupForm", signupRequest);
        return "inscricao";
    }

    @GetMapping("/logout")
    public String logout() {
        authService.logout();
        return "redirect:/";
    }
}
