package cz.ecis.config.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableJpaRepositories(basePackages = {
    DbConstants.ECIS_REPO_BASE_PACKAGE
})
@EnableTransactionManagement(order = Integer.MAX_VALUE - 1)
public class DbConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @DependsOn("liquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("dataSource") DataSource dataSource
    ) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");

        return builder
            .dataSource(dataSource)
            .packages(DbConstants.ECIS_ENT_BASE_PACKAGE)
            .properties(properties)
            .persistenceUnit("ecis_persistenceUnit")
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
    public DataSource liquibaseDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "liquibase")
    @DependsOn("liquibaseDataSource")
    public SpringLiquibase liquibase(
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

}
