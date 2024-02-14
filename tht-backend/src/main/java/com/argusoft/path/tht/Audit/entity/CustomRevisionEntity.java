package com.argusoft.path.tht.Audit.entity;

import com.argusoft.path.tht.Audit.config.CustomRevisionListener;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;


@Entity(name = "revinfo")
@Table(name = "revinfo")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity {
    @Id
    @RevisionNumber
    @Column(name = "rev")
        private int revisionNumber = 2; // Set the initial revision number

    @RevisionTimestamp
    @Column(name = "revtstmp")
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

