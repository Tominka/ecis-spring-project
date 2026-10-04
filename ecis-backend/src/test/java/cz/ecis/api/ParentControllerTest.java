package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import cz.ecis.config.EcisIntegrationTest;
import cz.ecis.config.liquibase.TestLiquibaseAfter;
import cz.ecis.config.liquibase.TestLiquibaseBefore;
import cz.ecis.core.model.LovDto;
import cz.ecis.model.dto.ParentDto;
import tools.jackson.core.type.TypeReference;

@TestLiquibaseBefore(changeLog = "db/changelog/test-child-parent-application.xml", dataSourceId = "liquibaseDataSource")
@TestLiquibaseAfter(changeLog = "db/changelog/test-child-parent-application-cleanup.xml", dataSourceId = "liquibaseDataSource")
class ParentControllerTest extends EcisIntegrationTest {

    @Test
    @Order(1)
    void testGetAllParents() throws Exception {

        List<ParentDto> parents = this.withHeader("X-Camp-Id", "1")
        .runGet(
            ParentController.PATH_GET_ALL_PARENTS,
            "test",
            new TypeReference<List<ParentDto>>() {}
        );

        assertThat(parents).singleElement().satisfies(parent -> {
            assertThat(parent.getId()).isEqualTo(1);
            assertThat(parent.getName()).isEqualTo("Test");
            assertThat(parent.getSurname()).isEqualTo("Parent");
            assertThat(parent.getPhone()).isEqualTo("1234567890");
            assertThat(parent.getEmail()).isEqualTo("parent@example.test");
        });
    }

    @Test
    @Order(2)
    void testGetAllParentsLov() throws Exception {

        List<LovDto> parents = this.withHeader("X-Camp-Id", "1")
        .runGet(
            ParentController.PATH_GET_ALL_PARENTS_LOV,
            "test",
            new TypeReference<List<LovDto>>() {}
        );

        assertThat(parents).singleElement().satisfies(parent -> {
            assertThat(parent.getId()).isEqualTo(1);
            assertThat(parent.getLabel()).contains("Test", "Parent");
        });
    }
}