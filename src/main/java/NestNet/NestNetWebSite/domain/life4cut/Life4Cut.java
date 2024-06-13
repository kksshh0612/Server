package NestNet.NestNetWebSite.domain.life4cut;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 인생네컷 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
public class Life4Cut {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "life_4_cut_id")
    private Long id;

    private String saveFilePath;
    private String saveFileName;

    /*
    생성자
     */
    @Builder
    public Life4Cut(String saveFilePath, String saveFileName) {
        this.saveFilePath = saveFilePath;
        this.saveFileName = saveFileName;
    }

    //== 비지니스 로직 ==//

}
