package it.sevenbits.springboottutorial.web.controllers;

import it.sevenbits.springboottutorial.web.domain.RegistrationForm;
import it.sevenbits.springboottutorial.web.service.RegistrationFormValidator;
import it.sevenbits.springboottutorial.web.service.ServiceException;
import it.sevenbits.springboottutorial.web.service.UsersService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class SessionController {
    private static final Logger LOG = Logger.getLogger(SessionController.class);

    @Autowired
    private RegistrationFormValidator validator;

    @Autowired
    private UsersService service;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin(HttpServletRequest request) {
        return "session/login";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String getRegistration(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "session/registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String register(@ModelAttribute RegistrationForm form, Model model) throws ServiceException {
        final Map<String, String> errors = validator.validate(form);
        if (errors.size() != 0) {
            // Если есть ошибки в форме, то снова рендерим главную страницу
            model.addAttribute("form", form);
            model.addAttribute("errors", errors);
            LOG.info("Subscription form contains errors.");
            return "session/registration";
        }

        service.register(form);
        return "redirect:/";
    }
}
