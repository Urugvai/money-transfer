package org.morozov.transferring.core.services;

import org.jetbrains.annotations.NotNull;

public class ServiceFactory {

    @NotNull
    public static OperationService createOperationService() {
        return new OperationServiceImpl();
    }

}
