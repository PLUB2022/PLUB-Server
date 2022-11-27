package plub.plubserver.util.s3;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.common.CommonErrorCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AwsS3Exception extends RuntimeException{
    CommonErrorCode awsS3ErrorCode;

    public AwsS3Exception() {
        super(CommonErrorCode.AWS_S3_ERROR.getMessage());
        this.awsS3ErrorCode = CommonErrorCode.AWS_S3_ERROR;
    }
}
