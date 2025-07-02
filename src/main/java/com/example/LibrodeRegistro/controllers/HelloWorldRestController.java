package com.example.LibrodeRegistro.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HelloWorldRestController {
    @GetMapping("/hello")
    public String helloworld(){
        System.out.println("Solicitud ejecutada");
        return "Hola Danielito";
    }
}
