package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.config.liquibase.TestLiquibaseAfter;
import cz.ecis.model.dto.ApplicationCreateDto;
import cz.ecis.model.dto.ApplicationDocumentCreateDto;
import cz.ecis.model.dto.ApplicationDocumentDto;
import cz.ecis.model.dto.ApplicationDto;
import cz.ecis.model.dto.ApplicationOverviewDto;
import cz.ecis.model.dto.ChildDto;
import cz.ecis.model.dto.ChildFilterDto;
import cz.ecis.model.dto.ErrorDto;
import cz.ecis.model.dto.ParentDto;
import tools.jackson.core.type.TypeReference;

@TestLiquibaseAfter(changeLog = "db/changelog/test-applications-cleanup.xml", dataSourceId = "liquibaseDataSource")
class ApplicationControllerTest extends EcisIntegrationTestClient {

    List<ApplicationDto> createdApplications = new ArrayList<>();

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

    @Test
    @Order(3)
    void testGetApplicationById() throws Exception {

        ApplicationDto createdApplication = this.createdApplications.get(0);

        this.addUrlParam("id", createdApplication.getId());

        ApplicationDto dto =  this.withHeader("X-Camp-Id", "1")
            .runGet(ApplicationController.PATH_GET_APPLICATION_BY_ID, "test", ApplicationDto.class);

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

        assertThat(dto.getParents()).isNotNull().hasSize(2);
        ParentDto parentResult1 = dto.getParents().get(0);
        ParentDto parentResult2 = dto.getParents().get(1);

        assertThat(parentResult1).isNotNull();
        assertThat(parentResult1.getName()).isNotBlank();
        assertThat(parentResult1.getSurname()).isNotBlank();
        assertThat(parentResult1.getAddress()).isNotBlank();

        assertThat(parentResult2).isNotNull();
        assertThat(parentResult2.getName()).isNotBlank();
        assertThat(parentResult2.getSurname()).isNotBlank();
        assertThat(parentResult2.getAddress()).isNotBlank();

        List<ApplicationDocumentDto> documents = dto.getDocuments();
        assertThat(documents).isNotEmpty().hasSize(1);
        ApplicationDocumentDto document = documents.get(0);
        assertThat(document).isNotNull();
        assertThat(document.getFileSize()).isNotBlank().endsWith("bytes");
        assertThat(document.getFilename()).isNotBlank().endsWith(".txt");
        assertThat(document.getId()).isNotBlank();
    }

    @Test
    @Order(4)
    void testGetApplicationDocumentById() throws Exception {

        ApplicationDto createdApplication = this.createdApplications.get(0);

        this.addUrlParam("id", createdApplication.getId());
        ApplicationDocumentDto document = createdApplication.getDocuments().get(0);
        this.addUrlParam("documentId", document.getId());

        Resource resource =  this.withHeader("X-Camp-Id", "1")
            .runGet(ApplicationController.PATH_GET_APPLICATION_DOCUMENT_CONTENT_BY_ID, "test", Resource.class);

        assertThat(resource.getFilename()).isEqualTo("neco.txt");
        assertThat(resource.getContentAsByteArray()).isEqualTo(new byte[] {1,2,3,4,5});
    }

    @Test
    @Order(5)
    void testSearchApplications() throws Exception {
        ChildFilterDto filter = new ChildFilterDto();

        List<ApplicationOverviewDto> applications = this.withHeader("X-Camp-Id", "1").runPost(
            ApplicationController.PATH_SEARCH_APPLICATIONS,
            "test",
            filter,
            new TypeReference<List<ApplicationOverviewDto>>() {}
        );
        assertThat(applications).hasSize(1);
        assertThat(applications.get(0).getChild()).isNotNull();
        assertThat(applications.get(0).getChild().getName()).isEqualTo("Jmeno");

        filter.name("Jme");
        applications = this.withHeader("X-Camp-Id", "1").runPost(
            ApplicationController.PATH_SEARCH_APPLICATIONS,
            "test",
            filter,
            new TypeReference<List<ApplicationOverviewDto>>() {}
        );
        assertThat(applications).hasSize(1);

        filter.name("No matching child");
        applications = this.withHeader("X-Camp-Id", "1").runPost(
            ApplicationController.PATH_SEARCH_APPLICATIONS,
            "test",
            filter,
            new TypeReference<List<ApplicationOverviewDto>>() {}
        );
        assertThat(applications).isEmpty();

        filter.name(null)
            .surname("Prijmeni")
            .address("Adresa")
            .birthdate(LocalDate.of(2022, 1, 5));
        applications = this.withHeader("X-Camp-Id", "1").runPost(
            ApplicationController.PATH_SEARCH_APPLICATIONS,
            "test",
            filter,
            new TypeReference<List<ApplicationOverviewDto>>() {}
        );
        assertThat(applications).hasSize(1);
        assertThat(applications.get(0).getChild()).isNotNull();
        assertThat(applications.get(0).getChild().getSurname()).isEqualTo("Prijmeni");

        filter.address("Different address");
        applications = this.withHeader("X-Camp-Id", "1").runPost(
            ApplicationController.PATH_SEARCH_APPLICATIONS,
            "test",
            filter,
            new TypeReference<List<ApplicationOverviewDto>>() {}
        );
        assertThat(applications).isEmpty();
    }

    @Test
    @Order(6)
    void testCancelApplication() throws Exception {

        ApplicationDto createdApplication = this.createdApplications.get(0);

        assertThat(createdApplication.getCanceledAt()).isNull();

        this.addUrlParam("id", createdApplication.getId());
        ApplicationOverviewDto dto =  this.withHeader("X-Camp-Id", "1")
            .runPost(ApplicationController.PATH_CANCEL_APPLICATION, "test", null, ApplicationOverviewDto.class);

        assertThat(dto.getCanceledAt()).isNotNull();

        this.addUrlParam("id", createdApplication.getId());
        ErrorDto errorDto = this.withHeader("X-Camp-Id", "1")
            .withExpectedStatus(HttpStatus.BAD_REQUEST)
            .runPost(ApplicationController.PATH_CANCEL_APPLICATION, "test", null, ErrorDto.class);

        assertThat(errorDto.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(7)
    void testConfirmApplication() throws Exception {

        ApplicationDto createdApplication = this.createdApplications.get(0);

        assertThat(createdApplication.getConfirmedAt()).isNull();

        this.addUrlParam("id", createdApplication.getId());
        ApplicationOverviewDto dto =  this.withHeader("X-Camp-Id", "1")
            .runPost(ApplicationController.PATH_CONFIRM_APPLICATION, "test", null, ApplicationOverviewDto.class);

        assertThat(dto.getConfirmedAt()).isNotNull();

        this.addUrlParam("id", createdApplication.getId());
        ErrorDto errorDto = this.withHeader("X-Camp-Id", "1")
            .withExpectedStatus(HttpStatus.BAD_REQUEST)
            .runPost(ApplicationController.PATH_CONFIRM_APPLICATION, "test", null, ErrorDto.class);

        assertThat(errorDto.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}