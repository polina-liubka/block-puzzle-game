package sk.tuke.gamestudio.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.User;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserController {
    private User loggedUser;

    @RequestMapping("/login")
    public String login(String login, String password) {
        if (login == null || login.trim().isEmpty()) {
            return "redirect:/block-puzzle-home?error=empty_fields";
        }

        User user = new User(login);
        user.setLogin(login);

        this.loggedUser = user;

        return "redirect:/block-puzzle-home";
    }

    @RequestMapping("/logout")
    public String logout() {
        loggedUser = null;
        return "redirect:/block-puzzle-home";
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean isLogged() {
        return loggedUser != null;
    }
}