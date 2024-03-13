package com.argusoft.path.tht.systemconfiguration.audit.entity;

import com.argusoft.path.tht.systemconfiguration.audit.config.CustomRevisionListener;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "revision")
@Table(name = "revision")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity {

    @Id
    @RevisionNumber
    @Column(name = "revision_number")
    private int revisionNumber;

    @RevisionTimestamp
    @Column(name = "revision_timestamp")
    private long revisionTimestamp;

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public long getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(long revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }
}
