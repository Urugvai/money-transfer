package org.morozov.transferring.core.config;

import io.swagger.jaxrs.config.BeanConfig;
import org.morozov.transferring.core.utils.PersistenceProvider;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Is used as start point of application
 */
@ApplicationPath("/")
public class AmountTransferApplication extends Application {
    public AmountTransferApplication() {
        PersistenceProvider.init("amount_transfer");
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage("org.morozov.transferring.rest.services");
        beanConfig.setScan(true);
    }
}
