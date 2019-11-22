package com.example.hmmsbeta1.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AboutController {
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public ModelAndView about(){
        return new ModelAndView("about");
    }

}
