package it.sevenbits.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller // stereotype аннотация, означающая что данный класс может обрабатывать входящие запросы.
public class HomeController {
    
    @RequestMapping(value = "/", method = RequestMethod.GET) // описывает какие запросы, по какому адресу, какого формата данный обработчик может принимать
    public String getIndex() {
        return "home/index";
    }
}
