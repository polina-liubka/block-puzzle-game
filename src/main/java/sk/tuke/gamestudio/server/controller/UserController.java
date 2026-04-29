package sk.tuke.gamestudio.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.User; // Перевір, чи є цей клас в entity

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserController {
    private User loggedUser;

    @RequestMapping("/login")
    public String login(String login, String password) {
        if ("qwerty".equals(password)) {
            loggedUser = new User(login);
            return "redirect:/block-puzzle-home";
        }
        return "redirect:/";
    }

    public void logout() {
        loggedUser = null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean isLogged() {
        return loggedUser != null;
    }
}