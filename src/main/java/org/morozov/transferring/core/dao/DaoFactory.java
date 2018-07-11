package org.morozov.transferring.core.dao;

import org.jetbrains.annotations.NotNull;

public class DaoFactory {

    @NotNull
    public static DataDaoAPI createDataDao() {
        return new DataDao();
    }

}
