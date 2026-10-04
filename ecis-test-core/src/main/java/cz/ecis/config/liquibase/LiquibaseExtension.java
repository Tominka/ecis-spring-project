package cz.ecis.config.liquibase;

import liquibase.integration.spring.SpringLiquibase;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        for (TestLiquibaseBefore annotation : findBeforeAnnotationsInClass(context)) {
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context, testUuid);
        }
    }

    @Override
    public void afterTestClass(final TestContext context) throws Exception {
        final String uuid = (String) context.getAttribute("liquibase.test.uuid");
        List<TestLiquibaseAfter> annotations = findAfterAnnotationsInClass(context);
        for (int index = annotations.size() - 1; index >= 0; index--) {
            TestLiquibaseAfter annotation = annotations.get(index);
            runLiquibase(annotation.changeLog(), annotation.dataSourceId(), context, uuid);
        }
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

    private List<TestLiquibaseBefore> findBeforeAnnotationsInClass(TestContext context) {
        List<TestLiquibaseBefore> annotations = new ArrayList<>();
        for (Class<?> testClass : getClassHierarchy(context)) {
            TestLiquibaseBefore annotation = testClass.getDeclaredAnnotation(TestLiquibaseBefore.class);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    private List<TestLiquibaseAfter> findAfterAnnotationsInClass(TestContext context) {
        List<TestLiquibaseAfter> annotations = new ArrayList<>();
        for (Class<?> testClass : getClassHierarchy(context)) {
            TestLiquibaseAfter annotation = testClass.getDeclaredAnnotation(TestLiquibaseAfter.class);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    private List<Class<?>> getClassHierarchy(TestContext context) {
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> type = context.getTestClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            hierarchy.add(type);
        }
        Collections.reverse(hierarchy);
        return hierarchy;
    }
}
