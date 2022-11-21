package plub.plubserver.util.s3;

public enum S3SaveDir {
    ACCOUNT_PROFILE("/account/profileImage"),

    PLUBBING_MAIN_IMAGE("/plubbing/mainImage");
    public final String path;
    S3SaveDir(String path) {
        this.path = path;
    }
}
