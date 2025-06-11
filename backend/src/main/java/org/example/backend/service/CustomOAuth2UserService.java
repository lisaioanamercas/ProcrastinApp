package org.example.backend.service;

import org.example.backend.model.AuthProvider;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.user.GoogleOAuth2UserInfo;
import org.example.backend.security.user.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);

        }catch (Exception ex) {
                OAuth2Error error = new OAuth2Error("invalid_token", "Processing OAuth2 user failed", null);
                throw new OAuth2AuthenticationException(error, ex);
            }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        if(!StringUtils.hasText(userInfo.getEmail())) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
        User user;

        if(userOptional.isPresent()) {
            user = userOptional.get();

            if(!user.getProvider().equals(AuthProvider.GOOGLE)) {
                throw new RuntimeException("You're signed up with " + user.getProvider() +
                        ". Please use your " + user.getProvider() + " account to login.");
            }

            user = updateExistingUser(user, userInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, userInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, GoogleOAuth2UserInfo userInfo) {
        User user = new User();

        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(userInfo.getId());
        user.setUsername(userInfo.getEmail()); // Use email as username
        user.setEmail(userInfo.getEmail());
        user.setFirstName(userInfo.getName().split(" ")[0]);
        if(userInfo.getName().split(" ").length > 1) {
            user.setLastName(userInfo.getName().split(" ")[1]);
        }
        user.setImageUrl(userInfo.getImageUrl());
        user.setPassword(""); // No password for OAuth users

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, GoogleOAuth2UserInfo userInfo) {
        existingUser.setFirstName(userInfo.getName().split(" ")[0]);
        if(userInfo.getName().split(" ").length > 1) {
            existingUser.setLastName(userInfo.getName().split(" ")[1]);
        }
        existingUser.setImageUrl(userInfo.getImageUrl());

        return userRepository.save(existingUser);
    }
}