package NestNet.NestNetWebSite.repository.executive;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.executive.ExecutiveInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

class ExecutiveInfoRepositoryTest extends TestSupport {

    @Autowired
    private ExecutiveInfoRepository executiveInfoRepository;

    @AfterEach
    void tearDown(){
        executiveInfoRepository.deleteAllInBatch();
    }

    @DisplayName("올해를 제외한 역대 모든 임원 정보를 조회한다.")
    @Test
    void findPrevExecutiveInfo() {
        // given
        int currYear = LocalDate.now().getYear();       // 현재 년도

        ExecutiveInfo executiveInfo1 = createExecutiveInfo(currYear, "test1");
        ExecutiveInfo executiveInfo2 = createExecutiveInfo(currYear - 1, "test2");
        ExecutiveInfo executiveInfo3 = createExecutiveInfo(currYear - 2, "test3");

        executiveInfoRepository.saveAll(List.of(executiveInfo1, executiveInfo2, executiveInfo3));

        // when
        List<ExecutiveInfo> prevExecutiveInfo = executiveInfoRepository.findPrevExecutiveInfo();

        // then
        Assertions.assertThat(prevExecutiveInfo).hasSize(2)
                .extracting("year", "name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(currYear - 1, "test2"),
                        Tuple.tuple(currYear - 2, "test3")
                );
    }

    @DisplayName("올해 모든 임원 정보를 조회한다.")
    @Test
    void findCurrentExecutiveInfo() {
        // given
        int currYear = LocalDate.now().getYear();       // 현재 년도

        ExecutiveInfo executiveInfo1 = createExecutiveInfo(currYear, "test1");
        ExecutiveInfo executiveInfo2 = createExecutiveInfo(currYear - 1, "test2");
        ExecutiveInfo executiveInfo3 = createExecutiveInfo(currYear - 2, "test3");

        executiveInfoRepository.saveAll(List.of(executiveInfo1, executiveInfo2, executiveInfo3));

        // when
        List<ExecutiveInfo> prevExecutiveInfo = executiveInfoRepository.findCurrentExecutiveInfo();

        // then
        Assertions.assertThat(prevExecutiveInfo).hasSize(1)
                .extracting("year", "name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(currYear, "test1")
                );
    }

    private static ExecutiveInfo createExecutiveInfo(int year, String name){
        return ExecutiveInfo.builder()
                .year(year)
                .name(name)
                .studentId("2018000000")
                .role("test")
                .priority(1)
                .build();
    }
}