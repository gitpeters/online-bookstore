package com.peters.userservice.config;


import com.peters.userservice.entity.User;
import com.peters.userservice.entity.UserRole;
import com.peters.userservice.repository.IUserRepository;
import com.peters.userservice.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Optional;


@Configuration

public class BeanConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    public CommandLineRunner createDefaultUser(PlatformTransactionManager transactionManager) {
        return args -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

            transactionTemplate.execute(status -> {

                if (!userRepository.findUserByEmail("admin@techiebros.com").isPresent()) {
                    Optional<UserRole> role = roleRepository.findByName("ROLE_ADMIN");
                    UserRole adminRole = null;
                    if(!role.isPresent()){
                        UserRole newAdminUser= UserRole.builder()
                                .name("ROLE_ADMIN")
                                .build();
                         adminRole= roleRepository.save(newAdminUser);
                    }else{
                        adminRole = role.get();
                    }

                    User newUser = User.builder()
                            .email("admin@techiebros.com")
                            .firstName("Abraham")
                            .lastName("Peter")
                            .roles(Collections.singleton(adminRole))
                            .password(passwordEncoder().encode("admin"))
                            .build();

//                    entityManager.persist(role); // Save the UserRole entity
                    userRepository.save(newUser); // Save the UserEntity entity
                }
                return null;
            });
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
