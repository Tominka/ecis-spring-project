package cz.ecis.config.liquibase;

import liquibase.integration.spring.SpringLiquibase;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

public class LiquibaseExtension implements TestExecutionListener {

    @Override
    public void beforeTestClass(final TestContext context) throws Exception {
        MergedAnnotation<LiquibaseTest> liquibaseTest =
        MergedAnnotations.from(
            context.getTestClass(),
            MergedAnnotations.SearchStrategy.TYPE_HIERARCHY
        ).get(LiquibaseTest.class);

        String uuid = liquibaseTest.isPresent()
            ? liquibaseTest.synthesize().uuid()
            : null;

        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString();
        }

        final String testUuid = uuid;

        context.setAttribute("liquibase.test.uuid", uuid);

        findBeforeAnnotationInClass(context).ifPresent(annotation ->
            runLiquibase(
                annotation.changeLog(),
                annotation.dataSourceId(),
                context,
                testUuid
            )
        );
    }

    @Override
    public void afterTestClass(final TestContext context) throws Exception {
        final String uuid = (String) context.getAttribute("liquibase.test.uuid");
        findAfterAnnotationInClass(context).ifPresent(annotation ->
            runLiquibase(
                annotation.changeLog(),
                annotation.dataSourceId(),
                context,
                uuid
            )
        );
    }

    @Override
    public void beforeTestMethod (final TestContext context) throws Exception {
        final TestLiquibaseBefore annotation = context.getTestMethod().getAnnotation(TestLiquibaseBefore.class);
        final String uuid = (String) context.getAttribute("liquibase.test.uuid");
        if (annotation != null) {
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context, uuid);
        }
    }

    @Override
    public void afterTestMethod (final TestContext context) throws Exception {
        final TestLiquibaseAfter annotation = context.getTestMethod().getAnnotation(TestLiquibaseAfter.class);
        final String uuid = (String) context.getAttribute("liquibase.test.uuid");
        if (annotation != null) {
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context, uuid);
        }
    }

    private void runLiquibase(final String changelog, final String dataSourceId, final TestContext context, final String testUuid) {
        final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) context.getApplicationContext();
        final DataSource dataSource = applicationContext.getBean(dataSourceId, DataSource.class);
        final SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);
        liquibase.setChangeLog(changelog);
        liquibase.setChangeLogParameters(
            Map.of("test.uuid", testUuid)
        );
        final String beanName = UUID.randomUUID().toString();
        applicationContext.getBeanFactory().registerSingleton(beanName, liquibase);
        applicationContext.getBeanFactory().initializeBean(liquibase, beanName);
    }

    private Optional<TestLiquibaseBefore> findBeforeAnnotationInClass(TestContext context) {
        MergedAnnotation<TestLiquibaseBefore> annotation =
            MergedAnnotations.from(
                context.getTestClass(),
                MergedAnnotations.SearchStrategy.TYPE_HIERARCHY
            )
            .get(TestLiquibaseBefore.class);

        return annotation.isPresent()
            ? Optional.of(annotation.synthesize())
            : Optional.empty();
    }

    private Optional<TestLiquibaseAfter> findAfterAnnotationInClass(TestContext context) {
        MergedAnnotation<TestLiquibaseAfter> annotation =
            MergedAnnotations.from(
                context.getTestClass(),
                MergedAnnotations.SearchStrategy.TYPE_HIERARCHY
            )
            .get(TestLiquibaseAfter.class);

        return annotation.isPresent()
            ? Optional.of(annotation.synthesize())
            : Optional.empty();
    }
}
