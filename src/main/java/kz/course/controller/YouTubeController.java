package kz.course.controller;// YouTubeController.java

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import kz.course.config.YouTubeConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/youtube")
public class YouTubeController {

    private final YouTube youTube;
    private final YouTubeConfig youTubeConfig;

    public YouTubeController(YouTube youTube, YouTubeConfig youTubeConfig) {
        this.youTube = youTube;
        this.youTubeConfig = youTubeConfig;
    }

    @PostMapping("/get-authorization-url")
    public ResponseEntity<String> getAuthorizationUrl() {
        try {
            String authorizationUrl = youTubeConfig.getAuthorizationUrl();
            return ResponseEntity.ok(authorizationUrl);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting authorization URL");
        }
    }


    @PostMapping("/exchange-code-for-token")
    public ResponseEntity<String> exchangeCodeForToken(@RequestParam String authorizationCode) {
        try {
            // Use the YouTubeConfig bean to exchange the code for a token
            GoogleTokenResponse tokenResponse = youTubeConfig.exchangeCodeForToken(authorizationCode);

            // You can save tokenResponse for later use, such as uploading videos on behalf of the user.
            return ResponseEntity.ok("Token exchange successful");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error exchanging code for token");
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("code") String code) {
        try {
            // Get user credentials (token) from YouTubeConfig
            String userToken = youTubeConfig.exchangeCodeForToken(code).getAccessToken();  // Replace with your actual method

            Video video = new Video();
            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle("Video Title");
            snippet.setDescription("Video Description");
            video.setSnippet(snippet);

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("public");
            video.setStatus(status);

            // Convert MultipartFile to java.io.File
            java.io.File videoFile = convertMultiPartToFile(file);

            // Create the video insert request
            YouTube.Videos.Insert videoInsert = youTube.videos()
                    .insert("snippet,status", video, new FileContent("video/*", videoFile));

            // Set user credentials (token)
            videoInsert.setOauthToken(userToken);

            // Execute the video insert request
            Video returnedVideo = videoInsert.execute();

            // Delete the temporary video file
            videoFile.delete();

            return ResponseEntity.ok("Video uploaded successfully: " + returnedVideo.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading video");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    // Convert MultipartFile to java.io.File
    private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {
        java.io.File convFile = new java.io.File(file.getOriginalFilename());
        file.transferTo(convFile);
        return convFile;
    }
}
