package com.loadbalancer.springbootloadbalancer.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")

public class TestController {
    @GetMapping("/greeting")
    public String greet(@RequestParam(value = "name", defaultValue = "John") String name) {
        System.out.println("Access /greeting");

        List<String> greetings = Arrays.asList("Hi there", "Greetings", "Salutations");
        Random rand = new Random();

        int randomNum = rand.nextInt(greetings.size());
        return greetings.get(randomNum)+" "+name;
    }

    @GetMapping("/")
    public String home() {
        System.out.println("Access /");
        return "Hi!";
    }



}
