package com.example.hmmsbeta1.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ServicesController {
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public ModelAndView home(){
        return new ModelAndView("services");
    }
}
