package plub.plubserver.util.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class S3TestController {

    private final AwsS3Service awsS3Service;

    @PostMapping("/test/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        return awsS3Service.upload(file, S3SaveDir.ACCOUNT_PROFILE, null);
    }
}
