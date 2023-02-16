package plub.plubserver.domain.archive.exception;

import plub.plubserver.domain.archive.config.ArchiveCode;

public class ArchiveException extends RuntimeException {
    public ArchiveCode archiveError;
    public ArchiveException(ArchiveCode archiveError) {
        super(archiveError.getMessage());
        this.archiveError = archiveError;
    }
}
