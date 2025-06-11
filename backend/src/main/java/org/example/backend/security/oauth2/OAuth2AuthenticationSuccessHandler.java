package org.example.backend.security.oauth2;

import org.example.backend.security.jwt.JwtUtils;
import org.example.backend.security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${app.oauth2.authorizedRedirectUris}")
    private List<String> authorizedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Extract user information
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Generate JWT token (you'll need to implement this)
        String token = jwtUtils.generateJwtToken(authentication);

        // Store user info if needed
        // userService.createOrUpdateOAuth2User(email, name);

        // Redirect to frontend callback with user data
        String redirectUrl = String.format(
                "http://localhost:5500/home.html?token=%s&username=%s&email=%s&oauth=true",
                token,
                URLEncoder.encode(name, StandardCharsets.UTF_8),
                URLEncoder.encode(email, StandardCharsets.UTF_8)
        );
        response.sendRedirect(redirectUrl);
    }
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        String targetUrl = getDefaultTargetUrl();

        // Generate JWT token
        String token = jwtUtils.generateJwtToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return authorizedRedirectUris
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}