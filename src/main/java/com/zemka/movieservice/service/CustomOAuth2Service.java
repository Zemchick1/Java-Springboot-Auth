package com.zemka.movieservice.service;

import com.zemka.movieservice.exception.BadRequestException;
import com.zemka.movieservice.model.entity.User;
import com.zemka.movieservice.repository.UserRepository;
import com.zemka.movieservice.utils.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(oAuth2UserRequest);
        return processOAuth2User(oauth2User);
    }

    private User processOAuth2User(OAuth2User oAuth2User){
        String email = oAuth2User.getAttributes().get("email").toString();
        if (email.isEmpty()){
            throw new BadRequestException("Bad Request"); // TODO INTERNAL ERROR
        }
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElseGet(() -> registerOAuth2User(oAuth2User));
    }

    private User registerOAuth2User(OAuth2User oAuth2User){
        User user = User.builder()
                .email(oAuth2User.getAttributes().get("email").toString())
                .role(Role.User)
                .build();
        userRepository.save(user);
        return user;
    }
}
