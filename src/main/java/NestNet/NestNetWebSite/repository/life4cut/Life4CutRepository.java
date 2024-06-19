package NestNet.NestNetWebSite.repository.life4cut;

import NestNet.NestNetWebSite.domain.life4cut.Life4Cut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface Life4CutRepository extends JpaRepository<Life4Cut, Long> {

    // 여러 건 페이징 조회 (id 내림차순)
    Page<Life4Cut> findAll(Pageable pageable);

    // 여러 건 랜덤 조회
    @Query(value = "select * from Life4Cut order by rand() limit :size", nativeQuery = true)
    List<Life4Cut> findByRandom(@Param("size") int size);
}
