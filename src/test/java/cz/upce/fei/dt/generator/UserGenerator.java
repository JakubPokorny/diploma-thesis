package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.backend.entities.Role;
import cz.upce.fei.dt.backend.entities.User;

public class UserGenerator {
    public static User generateUser(Long id) {
        return User.builder()
                .id(id)
                .firstName("firstName " + id)
                .lastName("lastName " + id)
                .email("email " + id)
                .role(Role.ADMIN)
                .password("pass")
                .build();
    }
}
