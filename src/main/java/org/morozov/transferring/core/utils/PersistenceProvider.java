package org.morozov.transferring.core.utils;

import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class PersistenceProvider {

    private static EntityManagerFactory factory;

    public static void init(String unitName) {
        factory = Persistence.createEntityManagerFactory(unitName);
    }

    @NotNull
    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void runInTransaction(@NotNull TransactionWrapper wrapper) {
        EntityManager em = factory.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            wrapper.run(em);
            transaction.commit();
        } finally {
            em.close();
        }
    }
}
