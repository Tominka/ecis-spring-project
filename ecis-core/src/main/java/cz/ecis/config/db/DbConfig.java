package cz.ecis.config.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableJpaRepositories(basePackages = {
    "cz.ecis.db.repo"
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
    // @DependsOn("liquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");

        return builder
            .dataSource(dataSource)
            .packages("cz.ecis.db.ent")
            .properties(properties)
            .persistenceUnit("ecis_persistenceUnit")
            .build();
    }

    // @Bean(name = "liquibaseDataSource")
    // @ConfigurationProperties(prefix = "spring.datasource.liquibase")
    // public DataSource liquibaseDataSource() {
    //     return DataSourceBuilder.create().build();
    // }
    
    // @Bean(name = "liquibase")
    // @DependsOn("liquibaseDataSource")
    // public SpringLiquibase liquibase(
    //     @Qualifier("liquibaseDataSource") DataSource liquibaseDataSource,
    //     @Value("${liquibase.changelog}") String changelog,
    //     @Value("${liquibase.enabled:false}") boolean shouldRun,
    //     @Value("${liquibase.context}") String context
    // ) {
    //     SpringLiquibase liquibase = new SpringLiquibase();
    //     liquibase.setDataSource(liquibaseDataSource);
    //     liquibase.setChangeLog(changelog);
    //     liquibase.setShouldRun(shouldRun);
    //     liquibase.setContexts(context);
    //     liquibase.setDefaultSchema("public");
    //     return liquibase;
    // }

}
