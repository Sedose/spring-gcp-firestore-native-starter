package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(Main.class, args);
    }
}
