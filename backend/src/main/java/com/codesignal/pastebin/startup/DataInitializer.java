package com.codesignal.pastebin.startup;

import com.codesignal.pastebin.model.Role;
import com.codesignal.pastebin.model.User;
import com.codesignal.pastebin.repo.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository users) {
        this.users = users;
    }

    @Override
    public void run(ApplicationArguments args) {
        users.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setPassword(encoder.encode("codesignal"));
            u.setRole(Role.ADMIN);
            return users.save(u);
        });
    }
}

