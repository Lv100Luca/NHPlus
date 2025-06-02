package de.hitec.nhplus.model;

/**
 * Interface for all entities that can be archived.
 */
public interface Archivable {
    /**
     * Checks if the object is archived.
     *
     * @return <code>true</code> if the object is archived, <code>false</code> otherwise.
     */
    public boolean isArchived();

    /**
     * Checks if the object can be deleted.
     *
     * @return <code>true</code> if the object can be deleted, <code>false</code> otherwise.
     */
    public boolean canBeDeleted();
}
