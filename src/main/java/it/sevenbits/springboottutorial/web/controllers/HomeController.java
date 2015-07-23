package it.sevenbits.springboottutorial.web.controllers;

import it.sevenbits.springboottutorial.web.domain.CommonResponse;
import it.sevenbits.springboottutorial.web.domain.SubscriptionForm;
import it.sevenbits.springboottutorial.web.domain.SubscriptionModel;
import it.sevenbits.springboottutorial.web.domain.SubscriptionsJsonResponse;
import it.sevenbits.springboottutorial.web.service.ServiceException;
import it.sevenbits.springboottutorial.web.service.SubscriptionFormValidator;
import it.sevenbits.springboottutorial.web.service.SubscriptionsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    private static Logger LOG = Logger.getLogger(HomeController.class);

    @Autowired
    private SubscriptionFormValidator validator;

    @Autowired
    private SubscriptionsService service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model) {
        // В модель добавим новый объект формы подписки
        model.addAttribute("subscription", new SubscriptionForm());
        // Так как нет аннотации @ResponseBody, то spring будет искать шаблон по адресу home/index
        // Если шаблона не будет найдено, то вернется 404 ошибка
        return "home/index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String subscribe(@ModelAttribute SubscriptionForm form, final Model model) throws ServiceException {
        final Map<String, String> errors = validator.validate(form);
        if (errors.size() != 0) {
            // Если есть ошибки в форме, то снова рендерим главную страницу
            model.addAttribute("subscription", form);
            model.addAttribute("errors", errors);
            LOG.info("Subscription form contains errors.");
            return "home/index";
        }
        service.save(form);
        // В запросе пришла заполненная форма. Отправим в модель этот объект и отрендерим ее на другом шаблоне.
        model.addAttribute("subscription", form);
        return "home/subscribed";
    }

    @RequestMapping(value = "/subscriptions.json", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<SubscriptionsJsonResponse> getSubscriptionsJson(final Model model) {
        List<SubscriptionModel> subscriptions = null;
        HttpStatus status = HttpStatus.OK;
        Integer count = 0;
        
        try {
            subscriptions = service.findAll();
            count = subscriptions.size();
        } catch (ServiceException e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            LOG.error("Can't load subscriptions: " + e.getMessage());
        }
        
        return new ResponseEntity<>(new SubscriptionsJsonResponse(count, subscriptions), status);
    }
    
    @RequestMapping(value = "/subscriptions/{id}.json", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<CommonResponse> deleteSubscription(@PathVariable String id) {
        CommonResponse response = new CommonResponse();
        HttpStatus status = HttpStatus.OK;
        String errorMessage = null;
        
        try {
            Long idL = Long.parseLong(id);
            service.delete(idL);
            response.setSuccess(true);
        } catch (Exception e) {
            LOG.error("Can't delete subscription due error: " + e.getMessage());
            response.setError("Can't delete subscription");
            status = HttpStatus.NOT_FOUND;
        }
        
        return new ResponseEntity<>(response, status);
    } 

    @RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
    public String getSubscriptions() {
        return "home/subscriptionsLayout";
    }
}
