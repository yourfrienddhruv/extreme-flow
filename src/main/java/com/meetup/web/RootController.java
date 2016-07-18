package com.meetup.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Root controller.
 */
@RestController
public class RootController {
    /**
     * Home string.
     *
     * @return the string
     */
    @RequestMapping("/")
    public String home(){
        return "Hello, World";
    }
}
