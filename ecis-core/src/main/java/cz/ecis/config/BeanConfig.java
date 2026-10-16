package cz.ecis.config;

import java.lang.reflect.InvocationTargetException;

import org.hibernate.Session;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cz.ecis.config.db.DbConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class BeanConfig {

    private static final String PROFILE = "org.hibernate.defaultProfile";

    // @Bean
    // static BeanPostProcessor fetchProfileEntityManagerFactoryPostProcessor() {
    //     return new BeanPostProcessor() {
    //         @Override
    //         public Object postProcessAfterInitialization(Object bean, String beanName) {

    //             if (!DbConstants.ECIS_ENTITY_MANAGER.equals(beanName) || !(bean instanceof EntityManagerFactory emf)) {
    //                 return bean;
    //             }

    //             return Proxy.newProxyInstance(EntityManagerFactory.class.getClassLoader(),
    //                 new Class<?>[] { EntityManagerFactory.class },
    //                 (proxy, method, args) -> {
    //                     try {
    //                         Object result = method.invoke(emf, args);

    //                         if (result instanceof EntityManager em) {
    //                             enableProfile(em);
    //                         }

    //                         return result;
    //                     } catch (InvocationTargetException ex) {
    //                         throw ex.getCause();
    //                     }
    //                 }
    //             );
    //         }
    //     };
    // }

    // private static void enableProfile(EntityManager em) {
    //     Session session = em.unwrap(Session.class);

    //     if (!session.isFetchProfileEnabled(PROFILE)) {
    //         session.enableFetchProfile(PROFILE);
    //     }
    // }
}
