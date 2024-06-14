package NestNet.NestNetWebSite.repository.attachedfile;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.attachedfile.AttachedFile;
import NestNet.NestNetWebSite.domain.post.Post;
import NestNet.NestNetWebSite.domain.post.notice.NoticePost;
import NestNet.NestNetWebSite.repository.post.NoticePostRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

class AttachedFileRepositoryTest extends TestSupport {

    @Autowired
    private AttachedFileRepository attachedFileRepository;

    @Autowired
    private NoticePostRepository noticePostRepository;

    @AfterEach
    void tearDown(){
        attachedFileRepository.deleteAllInBatch();
        noticePostRepository.deleteAllInBatch();
    }

    @DisplayName("첨부파일 PK를 통해 첨부파일을 단건 조회한다.")
    @Test
    void findById() {
        // given
        NoticePost post = noticePostRepository.save(new NoticePost());

        AttachedFile file = createAttachedFile(post, "file1", "file1", "path");

        AttachedFile saveAttachedFile = attachedFileRepository.save(file);

        // when
        Optional<AttachedFile> findAttachedFile = attachedFileRepository.findById(saveAttachedFile.getId());

        // then
        Assertions.assertThat(findAttachedFile.get().getId()).isEqualTo(file.getId());
        Assertions.assertThat(findAttachedFile.get().getOriginalFileName()).isEqualTo(file.getOriginalFileName());
    }

    @DisplayName("특정 게시물과 연관된 모든 첨부파일을 조회한다.")
    @Test
    void findAllByPost() {
        // given
        NoticePost post = noticePostRepository.save(new NoticePost());

        AttachedFile file1 = createAttachedFile(post, "file1", "file1", "path");
        AttachedFile file2 = createAttachedFile(post, "file2", "file2", "path");
        AttachedFile file3 = createAttachedFile(post, "file3", "file3", "path");

        attachedFileRepository.saveAll(List.of(file1, file2, file3));

        // when
        List<AttachedFile> attachedFileList = attachedFileRepository.findAllByPost(post);

        // then
        Assertions.assertThat(attachedFileList).hasSize(3)
                .extracting("originalFileName", "saveFileName")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("file1", "file1"),
                        Tuple.tuple("file2", "file2"),
                        Tuple.tuple("file3", "file3")
                );
    }

    private static AttachedFile createAttachedFile(Post post, String originalFileName, String saveFileName, String saveFilePath){
        return AttachedFile.builder()
                .post(post)
                .originalFileName(originalFileName)
                .saveFileName(saveFileName)
                .saveFilePath(saveFilePath)
                .build();
    }
}