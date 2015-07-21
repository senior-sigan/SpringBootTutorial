package it.sevenbits.springboottutorial.web.service;

import it.sevenbits.springboottutorial.web.domain.RegistrationForm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationFormValidator {
    @Autowired
    private CommonFieldValidator validator;

    private static final Logger LOG = Logger.getLogger(SubscriptionFormValidator.class);

    public HashMap<String, String> validate(final RegistrationForm form) {
        LOG.info("SubscriptionFormValidator started for: " + form.toString());
        HashMap<String, String> errors = new HashMap<>();

        validator.isNotNullOrEmpty(form.getEmail(), errors, "email", "Поле не должно быть пустым");
        validator.isNotNullOrEmpty(form.getPassword(), errors, "password", "Поле не должно быть пустым");
        validator.isNotNullOrEmpty(form.getPasswordConfirmation(), errors, "password confirmation", "Поле не должно быть пустым");

        validator.isEmail(form.getEmail(), errors, "email", "Введите правильный email");

        validator.shorterThan(form.getEmail(), 255, errors, "email", "Поле должно быть кроче чем 255 символов");
        validator.shorterThan(form.getPassword(), 255, errors, "password", "Поле должно быть кроче чем 255 символов");
        validator.shorterThan(form.getPasswordConfirmation(), 255, errors, "password confirmation", "Поле должно быть кроче чем 255 символов");

        if (!form.getPassword().equals(form.getPasswordConfirmation())) {
            errors.put("password", "Пароли должны совпадать");
        }

        for (Map.Entry<String, String> entry : errors.entrySet()) {
            LOG.info(String.format("Error found: Filed=%s, Error=%s",
            entry.getKey(), entry.getValue()));
        }

        return errors;
    }
}
