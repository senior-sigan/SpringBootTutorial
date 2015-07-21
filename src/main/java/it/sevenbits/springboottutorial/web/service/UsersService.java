package it.sevenbits.springboottutorial.web.service;

import it.sevenbits.springboottutorial.core.domain.User;
import it.sevenbits.springboottutorial.core.repository.RepositoryException;
import it.sevenbits.springboottutorial.core.repository.UserRepository;
import it.sevenbits.springboottutorial.web.domain.RegistrationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @Autowired
    private UserRepository repository;

    public void register(RegistrationForm form) throws ServiceException {
        try {
            User user = repository.createUser(form.getEmail(), form.getPassword());
        } catch (Exception e) {
            throw new ServiceException("Can't create user: " + e.getMessage(), e);
        }
    }
}
