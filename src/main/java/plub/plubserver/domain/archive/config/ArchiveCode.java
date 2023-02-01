package plub.plubserver.domain.archive.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArchiveCode {
    NOT_FOUND_ARCHIVE(404, 3010, "not found archive error."),
    IS_NOT_WRITER(403, 3020, "this account is not this archive writer."),;

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
