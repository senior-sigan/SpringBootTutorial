package it.sevenbits.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // stereotype аннотация, означающая что данный класс может обрабатывать входящие запросы.
public class HomeController {
    
    @RequestMapping(value = "/", method = RequestMethod.GET) // описывает какие запросы, по какому адресу, какого формата данный обработчик может принимать
    @ResponseBody // возвращаемое функцией значение отдается клиенту как есть.
    public String getIndex() {
        return "Hello World";
    }
}
