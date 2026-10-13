package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.config.liquibase.TestLiquibaseAfter;
import cz.ecis.config.liquibase.TestLiquibaseBefore;
import cz.ecis.core.model.LovDto;
import cz.ecis.model.dto.ChildDto;
import cz.ecis.model.dto.ChildFilterDto;
import tools.jackson.core.type.TypeReference;

@TestLiquibaseBefore(changeLog = "db/changelog/test-child-parent-application.xml", dataSourceId = "liquibaseDataSource")
@TestLiquibaseAfter(changeLog = "db/changelog/test-child-parent-application-cleanup.xml", dataSourceId = "liquibaseDataSource")
class ChildControllerTest extends EcisIntegrationTestClient {

    @Test
    @Order(1)
    void testGetAllChildren() throws Exception {

        List<ChildDto> children = this.withHeader("X-Camp-Id", "1").runGet(
            ChildController.PATH_GET_ALL_CHILDREN,
            "test",
            new TypeReference<List<ChildDto>>() {}
        );

        assertThat(children.get(0)).satisfies(child -> {
            assertThat(child.getId()).isEqualTo(1);
            assertThat(child.getName()).isEqualTo("Test");
            assertThat(child.getSurname()).isEqualTo("Child");
            assertThat(child.getAddress()).isEqualTo("Test address");
        });
    }

    @Test
    @Order(2)
    void testGetAllChildrenLov() throws Exception {

        List<LovDto> children = this.withHeader("X-Camp-Id", "1").runGet(
            ChildController.PATH_GET_ALL_CHILDREN_LOV,
            "test",
            new TypeReference<List<LovDto>>() {}
        );

        assertThat(children.get(0)).satisfies(child -> {
            assertThat(child.getId()).isEqualTo(1);
            assertThat(child.getLabel()).contains("Test", "Child");
        });
    }

    @Test
    @Order(3)
    void testSearchChildren() throws Exception {

        ChildFilterDto filter = new ChildFilterDto();

        List<ChildDto> children = this.withHeader("X-Camp-Id", "1").runPost(
            ChildController.PATH_SEARCH_CHILDREN,
            "test", filter,
            new TypeReference<List<ChildDto>>() {}
        );

        assertThat(children).isNotNull().hasSize(2);

        filter.name("Test");
        children = this.withHeader("X-Camp-Id", "1").runPost(
            ChildController.PATH_SEARCH_CHILDREN,
            "test", filter,
            new TypeReference<List<ChildDto>>() {}
        );

        assertThat(children).isNotNull().hasSize(2);

        filter.name("Test2");
        children = this.withHeader("X-Camp-Id", "1").runPost(
            ChildController.PATH_SEARCH_CHILDREN,
            "test", filter,
            new TypeReference<List<ChildDto>>() {}
        );

        assertThat(children).isNotNull().hasSize(1);
        ChildDto child = children.get(0);
        assertThat(child.getId()).isEqualTo(2);
        assertThat(child.getName()).isEqualTo("Test2");

        filter.name(null).surname("Child");
        children = this.withHeader("X-Camp-Id", "1").runPost(
            ChildController.PATH_SEARCH_CHILDREN,
            "test", filter,
            new TypeReference<List<ChildDto>>() {}
        );

        assertThat(children).isNotNull().hasSize(1);
        child = children.get(0);
        assertThat(child.getId()).isEqualTo(1);
        assertThat(child.getName()).isEqualTo("Test");

        filter.surname(null).birthdate(LocalDate.of(2018, 1, 3));
        children = this.withHeader("X-Camp-Id", "1").runPost(
            ChildController.PATH_SEARCH_CHILDREN,
            "test", filter,
            new TypeReference<List<ChildDto>>() {}
        );

        assertThat(children).isNotNull().hasSize(1);
        child = children.get(0);
        assertThat(child.getId()).isEqualTo(2);
        assertThat(child.getName()).isEqualTo("Test2");

        filter.birthdate(null).address("Test address 2");
        children = this.withHeader("X-Camp-Id", "1").runPost(
            ChildController.PATH_SEARCH_CHILDREN,
            "test", filter,
            new TypeReference<List<ChildDto>>() {}
        );

        assertThat(children).isNotNull().hasSize(1);
        child = children.get(0);
        assertThat(child.getId()).isEqualTo(2);
        assertThat(child.getName()).isEqualTo("Test2");
    }
}