package com.testing.harness.tool.systemconfiguration.utils;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * CommonGenericIdGenerator.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class CommonGenericIdGenerator implements IdentifierGenerator, Configurable {

    private String prefix;
    private String formatString;

    // get id prefix from param
    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        prefix = params.getProperty("prefix");
        formatString = params.getProperty("formatString");

    }

    // generate sequential id using with prefix attached
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String query = String.format("select %s from %s",
                session.getEntityPersister(object.getClass().getName(), object)
                        .getIdentifierPropertyName(),
                object.getClass().getSimpleName());

        Stream ids = session.createQuery(query).stream();

        Long max = ids.map(o -> o.toString().replace(prefix, ""))
                .mapToLong(num -> Long.valueOf(num.toString()))
                .max()
                .orElse(0L);

        if (this.formatString == null) {
            return prefix + (max + 1);
        } else {
            return prefix + String.format(this.formatString, (max + 1));
        }
    }
}
