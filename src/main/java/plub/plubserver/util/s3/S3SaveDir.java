package plub.plubserver.util.s3;

public enum S3SaveDir {
    ACCOUNT_PROFILE("/account/profile");
    public String path;
    S3SaveDir(String path) {
        this.path = path;
    }
}
