package kz.course.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import kz.course.config.YouTubeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CallbackController {
    private final YouTubeConfig youTubeConfig;

    @GetMapping("/Callback")
    public ResponseEntity<String> handleCallback(@RequestParam("code") String authorizationCode) {
        try {
            log.info(authorizationCode);
            // Exchange the received authorization code for a token
            GoogleTokenResponse tokenResponse = youTubeConfig.exchangeCodeForToken(authorizationCode);

            // Optionally, you can save the tokenResponse or extract relevant information from it.

            return ResponseEntity.ok("Authorization successful. You can now use the token for further requests.");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling authorization callback");
        }
    }
}
