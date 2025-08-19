package com.example.Literalura;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootRunner implements CommandLineRunner {
    private final MenuApp menuApp;
    public BootRunner(MenuApp menuApp) { this.menuApp = menuApp; }
    @Override public void run(String... args) { menuApp.menu(); }
}