package de.hitec.nhplus.model;

import java.time.LocalDate;

public interface Archivable {
    public void archive();

    public void restore();

    public boolean isArchived();

    public boolean canBeDeleted();
}
