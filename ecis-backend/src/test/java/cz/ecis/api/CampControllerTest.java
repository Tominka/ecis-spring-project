package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import cz.ecis.config.EcisIntegrationTest;
import cz.ecis.core.model.LovDto;
import cz.ecis.model.dto.CampDto;
import tools.jackson.core.type.TypeReference;

class CampControllerTest extends EcisIntegrationTest {

    List<CampDto> createdCamps = new ArrayList<>();

    Integer campIdTemp = -1;

    @Test
    @Order(1)
    void testGetAllCamps() throws Exception {

        List<CampDto> camps = this.runGet(CampController.PATH_GET_ALL_CAMPS, "test", new TypeReference<List<CampDto>>() {});

        assertThat(camps).hasSize(1);
        assertThat(camps.get(0).getId()).isEqualTo(1);
    }

    @Test
    @Order(2)
    void testGetAllCampsLov() throws Exception {

        List<LovDto> camps = this.runGet(CampController.PATH_GET_ALL_CAMPS_LOV, "test", new TypeReference<List<LovDto>>() {});

        assertThat(camps).hasSize(1);
        assertThat(camps.get(0).getId()).isEqualTo(1);
    }

    @Test
    @Order(3)
    void testGetCampById() throws Exception {

        this.addUrlParam("id", 1);
        CampDto camp = this.runGet(CampController.PATH_GET_CAMP_BY_ID, "test", CampDto.class);

        assertThat(camp).isNotNull();
        assertThat(camp.getId()).isEqualTo(1);
    }

    @Test
    @Order(4)
    void testCreateNewCamp() throws Exception {

        LocalDate dateFrom = LocalDate.now(ZoneId.systemDefault());

        CampDto body = new CampDto()
            .name("Test camp")
            .dateFrom(dateFrom)
            .location("Junit test")
            .price(2000d)
            .enabled(true)
            .createdBy("Junit test");

        CampDto camp = this.runPost(CampController.PATH_CREATE_CAMP, "test", body, CampDto.class);
        createdCamps.add(camp);

        assertThat(camp).isNotNull();
        assertThat(camp.getName()).isEqualTo("Test camp");
        assertThat(camp.getDateFrom()).isEqualTo(dateFrom);
        assertThat(camp.getLocation()).isEqualTo("Junit test");
        assertThat(camp.getPrice()).isEqualTo(2000);
        assertThat(camp.getEnabled()).isTrue();
        assertThat(camp.getCreatedBy()).isEqualTo("test");

        List<CampDto> camps = this.runGet(CampController.PATH_GET_ALL_CAMPS, "test", new TypeReference<List<CampDto>>() {});

        assertThat(camps).hasSize(2);
    }

    @Test
    @Order(5)
    void testUpdateCamp() throws Exception {

        LocalDate dateFrom = LocalDate.now(ZoneId.systemDefault());

        CampDto body = this.createdCamps.getFirst();

        body.setName("Updated test camp");

        this.addUrlParam("id", body.getId());
        CampDto camp = this.runPut(CampController.PATH_UPDATE_CAMP_BY_ID, "test", body, CampDto.class);

        assertThat(camp).isNotNull();
        assertThat(camp.getId()).isEqualTo(body.getId());
        assertThat(camp.getName()).isEqualTo("Updated test camp");
        assertThat(camp.getDateFrom()).isEqualTo(dateFrom);
        assertThat(camp.getLocation()).isEqualTo("Junit test");
        assertThat(camp.getPrice()).isEqualTo(2000);
        assertThat(camp.getEnabled()).isTrue();
        assertThat(camp.getCreatedBy()).isEqualTo("test");
        assertThat(camp.getVersion()).isEqualTo(body.getVersion()+1);

    }

}
