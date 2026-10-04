package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.model.dto.LookupBaseDto;
import cz.ecis.core.model.LovDto;
import tools.jackson.core.type.TypeReference;

class LookupControllerTest extends EcisIntegrationTestClient {

    @Test
    @Order(1)
    void testGetAllLkpChildInfo() throws Exception {

        List<LookupBaseDto> childInfo = this.runGet(
            LookupController.PATH_GET_ALL_LKP_CHILD_INFO,
            "test",
            new TypeReference<List<LookupBaseDto>>() {}
        );

        assertThat(childInfo).hasSize(16);
        assertThat(childInfo).extracting(LookupBaseDto::getCode).contains("insurance");
    }

    @Test
    void testGetAllLkpChildInfoLov() throws Exception {

        List<LovDto> czechChildInfo = this.runGetLkpChildInfoLov("cs");
        List<LovDto> englishChildInfo = this.runGetLkpChildInfoLov("en");

        assertThat(czechChildInfo).hasSize(16)
        .anySatisfy(item -> {
            assertThat(item.getId()).isEqualTo(1);
            assertThat(item.getLabel()).isEqualTo("Pojištění");
        });
        assertThat(englishChildInfo).hasSize(16);
        assertThat(englishChildInfo)
            .anySatisfy(item -> {
                assertThat(item.getId()).isEqualTo(1);
                assertThat(item.getLabel()).isEqualTo("Insurance");
            });
    }

    private List<LovDto> runGetLkpChildInfoLov(String acceptLanguage) throws Exception {

        this.withHeader("Accept-Language", acceptLanguage);

        List<LovDto> response = this.runGet(
            LookupController.PATH_GET_ALL_LKP_CHILD_INFO_LOV,
            "test",
            new TypeReference<List<LovDto>>() {}
        );

        this.clearHeaders();

        return  response;
    }
}