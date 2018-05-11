package controller.web.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/loginPage")
    public String showLoginPage() {

        return "login";
    }

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "accessDeny";
    }
}
