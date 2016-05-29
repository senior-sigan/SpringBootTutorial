package it.sevenbits.web.controllers;

import it.sevenbits.web.forms.MessageForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // stereotype аннотация, означающая что данный класс может обрабатывать входящие запросы.
public class HomeController {
    private static Logger log = LoggerFactory.getLogger(HomeController.class);
    
    @RequestMapping(value = "/", method = RequestMethod.GET) // описывает какие запросы, по какому адресу, какого формата данный обработчик может принимать
    public String getIndex() {
        return "home/index";
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public MessageForm writeMessage(
        final Model model,
        @ModelAttribute final MessageForm form
    ) {
        log.info("POST / " + form);
        return form;
    }
}
