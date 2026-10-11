package cz.ecis.config.db;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cz.ecis.db.listener.EntityChangeListener;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(
    basePackages = {
        DbConstants.ECIS_REPO_BASE_PACKAGE
    },
    transactionManagerRef = DbConstants.ECIS_TRANSACTION_MANAGER,
    entityManagerFactoryRef = DbConstants.ECIS_ENTITY_MANAGER
)
@EnableTransactionManagement(order = Integer.MAX_VALUE - 1)
public class DbConfig {

    private final EntityChangeListener listener;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(DbConstants.ECIS_ENTITY_MANAGER)
    @DependsOn("liquibase")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("dataSource") DataSource dataSource
    ) {

        Map<String, Object> properties = Map.of(
            "hibernate.hbm2ddl.auto", "validate"
        );

        return builder
            .dataSource(dataSource)
            .packages(DbConstants.ECIS_ENT_BASE_PACKAGE)
            .properties(properties)
            .persistenceUnit(DbConstants.ECIS_PERSISTANCE_UNIT)
            .build();
    }

    @Bean
    @ConditionalOnExpression("${testing:false}==false")
    String postgreContainerDependency() {
        return StringUtils.EMPTY;
    }

    @Bean
    @DependsOn("postgreContainerDependency")
    @ConfigurationProperties(prefix = "spring.liquibase")
    DataSource liquibaseDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean("liquibase")
    @DependsOn("liquibaseDataSource")
    SpringLiquibase liquibase(
        @Qualifier("liquibaseDataSource") DataSource liquibaseDataSource,
        // @Value("${spring.liquibase.changelog}") String changelog,
        @Value("${spring.liquibase.enabled:false}") boolean shouldRun,
        @Value("${spring.liquibase.context:PROD}") String context
    ) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(liquibaseDataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
        liquibase.setShouldRun(shouldRun);
        liquibase.setContexts(context);
        liquibase.setDefaultSchema("public");
        return liquibase;
    }

    @Bean(DbConstants.ECIS_TRANSACTION_MANAGER)
    JpaTransactionManager transactionalManager(
        @Qualifier(DbConstants.ECIS_ENTITY_MANAGER) EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf) {
            @Override
            protected void doBegin(Object transaction, TransactionDefinition definition) {

                super.doBegin(transaction, definition);

                EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);

                if (em != null) {
                    em.unwrap(Session.class).enableFetchProfile("org.hibernate.defaultProfile");
                }
            }
        };
    }

    //TODO: Enable on parameter?
    @Bean
    @DependsOn(DbConstants.ECIS_ENTITY_MANAGER)
    String registerEntityChangeListener(@Qualifier(DbConstants.ECIS_ENTITY_MANAGER) EntityManagerFactory entityManagerFactory) {

        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.appendListeners(EventType.POST_INSERT, listener);
        registry.appendListeners(EventType.POST_UPDATE, listener);
        registry.appendListeners(EventType.POST_DELETE, listener);

        return "EntityChangeListener registered";
    }
}
