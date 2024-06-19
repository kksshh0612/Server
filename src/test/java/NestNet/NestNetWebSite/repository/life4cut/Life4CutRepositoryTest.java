package NestNet.NestNetWebSite.repository.life4cut;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.life4cut.Life4Cut;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

class Life4CutRepositoryTest extends TestSupport {

    @Autowired
    private Life4CutRepository life4CutRepository;

    @AfterEach
    void tearDown(){
        life4CutRepository.deleteAllInBatch();
    }

    @DisplayName("지정한 개수와 정렬된 순서대로 인생네컷을 다건 조회한다.")
    @Test
    void findAll() {
        // given
        Life4Cut life4Cut1 = createLife4Cut("photo1");
        Life4Cut life4Cut2 = createLife4Cut("photo2");
        Life4Cut life4Cut3 = createLife4Cut("photo3");
        Life4Cut life4Cut4 = createLife4Cut("photo4");

        life4CutRepository.saveAll(List.of(life4Cut1, life4Cut2, life4Cut3, life4Cut4));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));

        // when
        List<Life4Cut> life4CutList = life4CutRepository.findAll(pageRequest).getContent();

        // then
        Assertions.assertThat(life4CutList).hasSize(3)
                .extracting("saveFileName")
                .containsExactly("photo4", "photo3", "photo2");
    }

    private static Life4Cut createLife4Cut(String name){
        return Life4Cut.builder()
                .saveFilePath("path")
                .saveFileName(name)
                .build();
    }

}