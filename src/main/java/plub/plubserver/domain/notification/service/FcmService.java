package plub.plubserver.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.notification.config.NotificationCode;
import plub.plubserver.domain.notification.dto.FcmDto.Notification;
import plub.plubserver.domain.notification.exception.NotificationException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static plub.plubserver.domain.notification.dto.FcmDto.FcmMessage;
import static plub.plubserver.domain.notification.dto.FcmDto.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final ObjectMapper objectMapper;
    private final JSONParser jsonParser;

    private String getAccessToken() {
        try {
            String firebaseConfigPath = "firebase/plub-firebase-private-key.json";
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            log.warn("FCM getAccessToken Error : {}", e.getMessage());
            throw new NotificationException(NotificationCode.GET_FCM_ACCESS_TOKEN_ERROR);
        }
    }

    private String makeMessage(String targetToken, String title, String body) {
        try {
            Notification notification = new Notification(title, body);
            Message message = new Message(targetToken, notification);
            FcmMessage fcmMessage = new FcmMessage(false, message);
            return objectMapper.writeValueAsString(fcmMessage);
        } catch (JsonProcessingException e) {
            log.warn("FCM [makeMessage] Error : {}", e.getMessage());
            throw new NotificationException(NotificationCode.FCM_MESSAGE_JSON_PARSING_ERROR);
        }

    }

    @Async
    public CompletableFuture<Boolean> sendPushMessage(String targetToken, String title, String body) {
        String message = makeMessage(targetToken, title, body);
        String accessToken = getAccessToken();
        OkHttpClient client = new OkHttpClient();
        String FCM_URL = "https://fcm.googleapis.com/v1/projects/plub-1668049761866/messages:send";
        Request request = new Request.Builder()
                .url(FCM_URL)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json; UTF-8")
                .post(okhttp3.RequestBody.create(message, okhttp3.MediaType.parse("application/json; charset=utf-8")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() && response.body() != null) {
                JSONObject responseBody = (JSONObject) jsonParser.parse(response.body().string());
                String errorMessage = ((JSONObject) responseBody.get("error")).get("message").toString();
                log.warn("FCM [sendPushMessage] okHttp response is not OK : {}", errorMessage);
                return CompletableFuture.completedFuture(false);
            }
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.warn("FCM [sendPushMessage] I/O Exception : {}", e.getMessage());
            throw new NotificationException(NotificationCode.SEND_FCM_PUSH_ERROR);
        }
    }
}
