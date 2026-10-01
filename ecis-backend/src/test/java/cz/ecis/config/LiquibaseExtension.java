package cz.ecis.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import java.util.UUID;

import javax.sql.DataSource;

public class LiquibaseExtension implements TestExecutionListener {

    @Override
    public void beforeTestMethod(final TestContext context) throws Exception {
        final TestLiquibaseBefore annotation = context.getTestMethod().getAnnotation(TestLiquibaseBefore.class);
        if (annotation != null) {
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context);
        }
    }

    @Override
    public void afterTestMethod(final TestContext context) throws Exception {
        final TestLiquibaseAfter annotation = context.getTestMethod().getAnnotation(TestLiquibaseAfter.class);
        if (annotation != null) {
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context);
        }
    }

    @Override
    public void beforeTestClass (final TestContext context) throws Exception {
        final TestLiquibaseBefore annotation = context.getTestClass().getAnnotation(TestLiquibaseBefore.class);
        if (annotation != null) {
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context);
        }
    }

    @Override
    public void afterTestClass (final TestContext context) throws Exception {
        final TestLiquibaseAfter annotation = context.getTestClass().getAnnotation(TestLiquibaseAfter.class);
        if (annotation != null) {
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context);
        }
    }

    private void runLiquibase(final String changelog, final String dataSourceId, final TestContext context) {
        final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) context.getApplicationContext();
        final DataSource dataSource = applicationContext.getBean(dataSourceId, DataSource.class);
        final SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);
        liquibase.setChangeLog(changelog);
        final String beanName = UUID.randomUUID().toString();
        applicationContext.getBeanFactory().registerSingleton(beanName, liquibase);
        applicationContext.getBeanFactory().initializeBean(liquibase, beanName);
    }


}
