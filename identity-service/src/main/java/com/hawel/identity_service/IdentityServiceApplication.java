package com.hawel.identity_service;

import com.hawel.identity_service.entity.Role;
import com.hawel.identity_service.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan
public class IdentityServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }


    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {


        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }


    }

}
