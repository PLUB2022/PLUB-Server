package plub.plubserver.util.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.common.exception.CommonErrorCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AwsS3Exception extends RuntimeException{
    CommonErrorCode awsS3ErrorCode;

    public AwsS3Exception(CommonErrorCode awsS3ErrorCode) {
        super(awsS3ErrorCode.getMessage());
        this.awsS3ErrorCode = awsS3ErrorCode;
    }
}
