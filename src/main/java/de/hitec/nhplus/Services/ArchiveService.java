package de.hitec.nhplus.Services;

import de.hitec.nhplus.model.Archivable;

public class ArchiveService {
    public static void archive(Archivable archivable) {
        if (archivable.isArchived())
            return;

        archivable.archive();
    }
}
