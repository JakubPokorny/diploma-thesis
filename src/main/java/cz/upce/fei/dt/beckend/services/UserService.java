package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;

}
