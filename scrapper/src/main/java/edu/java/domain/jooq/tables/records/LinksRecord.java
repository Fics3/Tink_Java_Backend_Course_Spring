/*
 * This file is generated by jOOQ.
 */

package edu.java.domain.jooq.tables.records;

import edu.java.domain.jooq.tables.Links;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class LinksRecord extends UpdatableRecordImpl<LinksRecord>
    implements Record4<UUID, String, OffsetDateTime, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a detached LinksRecord
     */
    public LinksRecord() {
        super(Links.LINKS);
    }

    /**
     * Create a detached, initialised LinksRecord
     */
    @ConstructorProperties({"linkId", "link", "lastUpdate", "lastCheck"})
    public LinksRecord(
        @NotNull UUID linkId,
        @Nullable String link,
        @NotNull OffsetDateTime lastUpdate,
        @NotNull OffsetDateTime lastCheck
    ) {
        super(Links.LINKS);

        setLinkId(linkId);
        setLink(link);
        setLastUpdate(lastUpdate);
        setLastCheck(lastCheck);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LinksRecord
     */
    public LinksRecord(edu.java.domain.jooq.tables.pojos.Links value) {
        super(Links.LINKS);

        if (value != null) {
            setLinkId(value.getLinkId());
            setLink(value.getLink());
            setLastUpdate(value.getLastUpdate());
            setLastCheck(value.getLastCheck());
            resetChangedOnNotNull();
        }
    }

    /**
     * Getter for <code>LINKS.LINK_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public UUID getLinkId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>LINKS.LINK_ID</code>.
     */
    public void setLinkId(@NotNull UUID value) {
        set(0, value);
    }

    /**
     * Getter for <code>LINKS.LINK</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getLink() {
        return (String) get(1);
    }

    /**
     * Setter for <code>LINKS.LINK</code>.
     */
    public void setLink(@Nullable String value) {
        set(1, value);
    }

    /**
     * Getter for <code>LINKS.LAST_UPDATE</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getLastUpdate() {
        return (OffsetDateTime) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>LINKS.LAST_UPDATE</code>.
     */
    public void setLastUpdate(@NotNull OffsetDateTime value) {
        set(2, value);
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>LINKS.LAST_CHECK</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getLastCheck() {
        return (OffsetDateTime) get(3);
    }

    /**
     * Setter for <code>LINKS.LAST_CHECK</code>.
     */
    public void setLastCheck(@NotNull OffsetDateTime value) {
        set(3, value);
    }

    @Override
    @NotNull
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    @Override
    @NotNull
    public Row4<UUID, String, OffsetDateTime, OffsetDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    @NotNull
    public Row4<UUID, String, OffsetDateTime, OffsetDateTime> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    @NotNull
    public Field<UUID> field1() {
        return Links.LINKS.LINK_ID;
    }

    @Override
    @NotNull
    public Field<String> field2() {
        return Links.LINKS.LINK;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field3() {
        return Links.LINKS.LAST_UPDATE;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field4() {
        return Links.LINKS.LAST_CHECK;
    }

    @Override
    @NotNull
    public UUID component1() {
        return getLinkId();
    }

    @Override
    @Nullable
    public String component2() {
        return getLink();
    }

    @Override
    @NotNull
    public OffsetDateTime component3() {
        return getLastUpdate();
    }

    @Override
    @NotNull
    public OffsetDateTime component4() {
        return getLastCheck();
    }

    @Override
    @NotNull
    public UUID value1() {
        return getLinkId();
    }

    @Override
    @Nullable
    public String value2() {
        return getLink();
    }

    @Override
    @NotNull
    public OffsetDateTime value3() {
        return getLastUpdate();
    }

    @Override
    @NotNull
    public OffsetDateTime value4() {
        return getLastCheck();
    }

    @Override
    @NotNull
    public LinksRecord value1(@NotNull UUID value) {
        setLinkId(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord value2(@Nullable String value) {
        setLink(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public LinksRecord value3(@NotNull OffsetDateTime value) {
        setLastUpdate(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord value4(@NotNull OffsetDateTime value) {
        setLastCheck(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord values(
        @NotNull UUID value1,
        @Nullable String value2,
        @NotNull OffsetDateTime value3,
        @NotNull OffsetDateTime value4
    ) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }
}