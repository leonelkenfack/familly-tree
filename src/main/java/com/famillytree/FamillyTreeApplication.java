package com.famillytree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.famillytree.auth",
    "com.famillytree.node",
    "com.famillytree.exception",
    "com.famillytree"
})
@EntityScan(basePackages = {
    "com.famillytree.auth.model",
    "com.famillytree.node.model"
})
@EnableJpaRepositories(basePackages = {
    "com.famillytree.auth.repository",
    "com.famillytree.node.repository"
})
public class FamillyTreeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamillyTreeApplication.class, args);
    }
} 