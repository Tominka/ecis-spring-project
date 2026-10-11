package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.config.liquibase.TestLiquibaseAfter;
import cz.ecis.model.dto.ApplicationCreateDto;
import cz.ecis.model.dto.ApplicationDocumentCreateDto;
import cz.ecis.model.dto.ApplicationDocumentDto;
import cz.ecis.model.dto.ApplicationDto;
import cz.ecis.model.dto.ApplicationOverviewDto;
import cz.ecis.model.dto.CampDto;
import cz.ecis.model.dto.ChildDto;
import cz.ecis.model.dto.ParentDto;
import tools.jackson.core.type.TypeReference;

@TestLiquibaseAfter(changeLog = "db/changelog/test-applications-cleanup.xml", dataSourceId = "liquibaseDataSource")
class ApplicationControllerTest extends EcisIntegrationTestClient {

    List<ApplicationDto> createdApplications = new ArrayList<>();

    Integer campIdTemp = -1;

    @Test
    @Order(1)
    void testApplicationCreate() throws Exception {

        ApplicationCreateDto dto = new ApplicationCreateDto();
        ChildDto child = new ChildDto()
            .name("Jmeno")
            .surname("Prijmeni")
            .address("Adresa")
            .birthdate(LocalDate.of(2022, 1, 5));

        ParentDto parent1 =  new ParentDto()
            .name("Jmeno1")
            .surname("Prijmeni1")
            .address("Adresa1");

        ParentDto parent2 =  new ParentDto()
            .name("Jmeno2")
            .surname("Prijmeni2")
            .address("Adresa2");

        dto.setChild(child);
        dto.setParents(List.of(parent1, parent2));
        dto.addDocumentsItem(
            new ApplicationDocumentCreateDto()
            .filename("neco.txt")
            .data(new byte[] {1,2,3,4,5})
        );

        ApplicationDto result = this.withHeader("X-Camp-Id", "1")
            .withExpectedStatus(HttpStatus.CREATED)
            .runPost(ApplicationController.PATH_CREATE_APPLICATION, "test", dto, ApplicationDto.class);

        this.createdApplications.add(result);

        ChildDto childResult = result.getChild();
        assertThat(childResult).isNotNull();
        assertThat(childResult.getId()).isNotNull();
        assertThat(childResult.getName()).isEqualTo(child.getName());
        assertThat(childResult.getSurname()).isEqualTo(child.getSurname());
        assertThat(childResult.getAddress()).isEqualTo(child.getAddress());
        assertThat(childResult.getBirthdate()).isEqualTo(child.getBirthdate());

        assertThat(result.getParents()).isNotNull().hasSize(2);
        ParentDto parentResult1 = result.getParents().get(0);
        ParentDto parentResult2 = result.getParents().get(1);

        assertThat(parentResult1).isNotNull();
        assertThat(parentResult1.getName()).isEqualTo(parent1.getName());
        assertThat(parentResult1.getSurname()).isEqualTo(parent1.getSurname());
        assertThat(parentResult1.getAddress()).isEqualTo(parent1.getAddress());

        assertThat(parentResult2).isNotNull();
        assertThat(parentResult2.getName()).isEqualTo(parent2.getName());
        assertThat(parentResult2.getSurname()).isEqualTo(parent2.getSurname());
        assertThat(parentResult2.getAddress()).isEqualTo(parent2.getAddress());

        List<ApplicationDocumentDto> ad = result.getDocuments();
        assertThat(ad).isNotNull().hasSize(1);
        assertThat(Files.exists(Path.of(EcisIntegrationTestClient.TEMP_DIR.toString(), "neco.txt")));
    }

    @Test
    @Order(2)
    void testGetApplicationOverview() throws Exception {

        List<ApplicationOverviewDto> list =  this.withHeader("X-Camp-Id", "1")
            .runGet(ApplicationController.PATH_GET_ALL_APPLICATIONS, "test", new TypeReference<List<ApplicationOverviewDto>>() {});

        assertThat(list).isNotNull().hasSize(1);

        ApplicationOverviewDto dto = list.get(0);

        assertThat(dto.getCanceledAt()).isNull();
        assertThat(dto.getConfirmedAt()).isNull();
        assertThat(dto.getCreatedAt()).isNotNull();

        ChildDto child = dto.getChild();
        assertThat(child).isNotNull();
        assertThat(child.getId()).isNotNull();
        assertThat(child.getName()).isNotNull();
        assertThat(child.getSurname()).isNotNull();
        assertThat(child.getAddress()).isNotNull();
        assertThat(child.getBirthdate()).isNotNull();

        assertThat(dto.getDocumentCount()).isEqualTo(1);
    }

}