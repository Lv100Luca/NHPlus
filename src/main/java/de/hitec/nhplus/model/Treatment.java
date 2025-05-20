package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a treatment in the database.
 */
public class Treatment implements Entity {
    private final long id;
    private final long pid;
    private LocalDate date;
    private LocalTime begin;
    private LocalTime end;
    private String description;
    private String remarks;
    private long cid;

    /**
     * Private constructor to initiate an object of class <code>Treatment</code> from the database.
     *
     * @param id          Id of the treatment.
     * @param pid         Id of the treated patient.
     * @param date        Date of the Treatment.
     * @param begin       Time of the start of the treatment in format "hh:MM"
     * @param end         Time of the end of the treatment in format "hh:MM".
     * @param description Description of the treatment.
     * @param remarks     Remarks to the treatment.
     */
    private Treatment(long id, long pid, LocalDate date, LocalTime begin,
                      LocalTime end, String description, String remarks, long cid) {
        this.id = id;
        this.pid = pid;
        this.date = date;
        this.begin = begin;
        this.end = end;
        this.description = description;
        this.remarks = remarks;
        this.cid = cid;
    }

    public static Treatment fromResultSet(ResultSet result) throws SQLException {
        return new Treatment(result.getLong(1), result.getLong(2),
                DateConverter.convertStringToLocalDate(result.getString(3)),
                DateConverter.convertStringToLocalTime(result.getString(4)),
                DateConverter.convertStringToLocalTime(result.getString(5)),
                result.getString(6), result.getString(7), result.getLong(8));
    }


    public long getId() {
        return id;
    }

    public long getPid() {
        return this.pid;
    }

    public String getDate() {
        return date.toString();
    }

    public String getBegin() {
        return begin.toString();
    }

    public String getEnd() {
        return end.toString();
    }

    public void setDate(String date) {
        this.date = DateConverter.convertStringToLocalDate(date);
    }

    public void setBegin(String begin) {
        this.begin = DateConverter.convertStringToLocalTime(begin);
    }

    public void setEnd(String end) {
        this.end = DateConverter.convertStringToLocalTime(end);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String toString() {
        return "\nBehandlung" + "\nTID: " + this.id +
                "\nPID: " + this.pid +
                "\nDate: " + this.date +
                "\nBegin: " + this.begin +
                "\nEnd: " + this.end +
                "\nDescription: " + this.description +
                "\nRemarks: " + this.remarks + "\n";
    }
}
