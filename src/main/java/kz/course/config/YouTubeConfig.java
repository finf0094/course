package kz.course.config;// YouTubeConfig.java

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import kz.course.entity.User;
import kz.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class YouTubeConfig {
    private UserService userService;

    @Autowired
    public YouTubeConfig(UserService userService) {
        this.userService = userService;
    }

    @Value("${youtube.clientId}")
    private String clientId;

    @Value("${youtube.clientSecret}")
    private String clientSecret;

    @Value("${youtube.redirectUri}")
    private String redirectUri;

    private String userToken;  // This variable will store the user token after authorization

    @Bean
    public YouTube youtube(@Value("${youtube.redirectUri}") String redirectUri)
            throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, GsonFactory.getDefaultInstance(), getCredentials(httpTransport, redirectUri))
                .setApplicationName("Your Application Name")
                .build();
    }

    public String getAuthorizationUrl() throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                clientId,
                clientSecret,
                Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build();
    }

    public GoogleTokenResponse exchangeCodeForToken(String authorizationCode)
            throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                clientId,
                clientSecret,
                Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        GoogleTokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
                .setRedirectUri(redirectUri)
                .execute();

        // Save the user token for later use
        setUserToken(tokenResponse.getAccessToken());

        return tokenResponse;
    }

    private Credential getCredentials(final NetHttpTransport httpTransport, String redirectUri)
            throws IOException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, GsonFactory.getDefaultInstance(), clientId, clientSecret,
                Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        return new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver.Builder().setPort(3002).setHost("localhost").build())
                .authorize("user");
    }


    public void setUserToken(String userToken) {
        this.userToken = userToken;
        // Здесь вы должны сохранить маркер доступа в безопасном месте, например, в базе данных.
        // Пример: userService.saveUserToken(userId, userToken);
        userService.saveYoutubeToken(1L, userToken);
    }

    public String getUserToken() {
        // Здесь вы должны получить маркер доступа из безопасного места, например, из базы данных.
        // Пример: return userService.getUserToken(userId);

        return userService.getYoutubeToken(1L);
    }
}
