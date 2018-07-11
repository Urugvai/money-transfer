package org.morozov.transferring.core.utils;

import javax.persistence.EntityManager;

@FunctionalInterface
public interface TransactionWrapper {
    void run(EntityManager entityManager);
}
