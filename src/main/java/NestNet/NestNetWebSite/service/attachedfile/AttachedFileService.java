package NestNet.NestNetWebSite.service.attachedfile;

import NestNet.NestNetWebSite.domain.attachedfile.AttachedFile;
import NestNet.NestNetWebSite.domain.post.Post;
import NestNet.NestNetWebSite.exception.CustomException;
import NestNet.NestNetWebSite.exception.ErrorCode;
import NestNet.NestNetWebSite.repository.attachedfile.AttachedFileRepository;
import NestNet.NestNetWebSite.repository.post.PostRepository;
import jakarta.persistence.Transient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AttachedFileService {

    private final AttachedFileRepository attachedFileRepository;

    @Value("${filePath}")
    private String baseFilePath;

    /*
    첨부파일 저장
     */
    @Transactional
    public List<AttachedFile> save(Post post, List<MultipartFile> files){

        List<AttachedFile> attachedFileList = files.stream()
                .map(file -> new AttachedFile(post, getOriginalFileName(file), UUID.randomUUID().toString(), createSavePath(post)))
                .collect(Collectors.toList());

        attachedFileRepository.saveAll(attachedFileList);

        saveRealFile(attachedFileList, files);

        return attachedFileList;
    }

    /*
    MultipartFile에서 파일 이름 추출
     */
    private String getOriginalFileName(MultipartFile file){

        return Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC);    //Mac, Window 한글 처리 다른 이슈 처리
    }

    /*
    파일 저장 경로 생성
     */
    private String createSavePath(Post post){

        StringBuilder folderBuilder = new StringBuilder()
                .append(post.getPostCategory().toString())
                .append(File.separator).append(post.getCreatedTime().getYear());

        File folder = new File(baseFilePath + folderBuilder.toString());

        if(!folder.exists()){       // 폴더 그냥 만들고 테스트 끝나면 삭제하는 로직 넣기..
            try {
                folder.mkdirs();
            }catch (Exception e){
                throw new CustomException(ErrorCode.CANNOT_SAVE_FILE);
            }
        }

        return folderBuilder.toString();
    }

//    /*
//    게시물에 해당된 첨부파일 중 썸네일만 조회
//     */
//    public AttachedFile findThumbNailFileByPost(Post post){
//
//        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"));
//
//        Page<AttachedFile> filePage = attachedFileRepository.findThumbNailByPost(post, pageRequest);
//
//        List<AttachedFile> attachedFileList = filePage.getContent();
//
//        if(attachedFileList.isEmpty()) throw new CustomException(ErrorCode.THUMBNAIL_FILE_NOT_FOUND);
//
//        return attachedFileList.get(0);
//    }

    /*
    실제 파일 전송
     */
    public InputStreamResource findFile(Long fileId){

        AttachedFile file = attachedFileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        InputStreamResource resource = null;

        StringBuilder filePathBuilder = new StringBuilder(baseFilePath)
                .append(file.getSaveFilePath())
                .append(File.separator)
                .append(file.getSaveFileName());

        Path filePath = Paths.get(filePathBuilder.toString());
        File realFile = new File(filePath.toString());

        try{
            resource = new InputStreamResource(new FileInputStream(realFile));
        } catch (FileNotFoundException e){
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

        return resource;
    }

    /*
    첨부파일 수정
     */
    @Transactional
    public void modifyFiles(Post post, List<Long> existFileIdList, List<MultipartFile> fileList){

        List<AttachedFile> deleteFileList = attachedFileRepository.findAllByPost(post);      //이전에 있던 파일 중 삭제할 파일

        // 이전에 있던 파일 중 삭제될 파일만 남김
        for(int i = 0; i < deleteFileList.size(); i++){

            if(existFileIdList == null) break;

            for(Long existFileId : existFileIdList){

                if(existFileId.equals(deleteFileList.get(i).getId())){
                    deleteFileList.remove(deleteFileList.get(i));
                }
            }
        }

        // Delete From DB
        attachedFileRepository.deleteAll(deleteFileList);

        // Delete Real File
        deleteRealFile(deleteFileList);

        if(fileList == null) return;

        List<AttachedFile> newFileList = fileList.stream()
                .map(file -> new AttachedFile(post, getOriginalFileName(file), UUID.randomUUID().toString(), createSavePath(post)))
                .collect(Collectors.toList());

        // Save To DB
        attachedFileRepository.saveAll(newFileList);

        // Save Real File
        saveRealFile(newFileList, fileList);
    }

    /*
    첨부파일 삭제
     */
    @Transactional
    public void deleteFiles(Post post){

        List<AttachedFile> files = attachedFileRepository.findAllByPost(post);

        // Delete From DB
        attachedFileRepository.deleteAll(files);

        // Delete Real File
        deleteRealFile(files);
    }

    //==============================물리적인 파일 컨트롤==============================//

    // 실제 파일 저장
    private void saveRealFile(List<AttachedFile> attachedFiles, List<MultipartFile> files){

        for(int i = 0; i < attachedFiles.size(); i++){

            AttachedFile attachedFile = attachedFiles.get(i);
            MultipartFile file = files.get(i);

            StringBuilder filePathBuilder = new StringBuilder(baseFilePath)
                    .append(attachedFile.getSaveFilePath())
                    .append(File.separator)
                    .append(attachedFile.getSaveFileName());

            Path saveFilePath = Paths.get(filePathBuilder.toString());

            try {
                System.out.println("파일경로 : " + saveFilePath.toString());

                file.transferTo(saveFilePath);
            } catch (IOException | IllegalStateException e){
                throw new CustomException(ErrorCode.CANNOT_SAVE_FILE);
            }
        }
    }

    // 실제 파일 삭제
    private void deleteRealFile(List<AttachedFile> files){

        for(AttachedFile file : files){
            StringBuilder filePathBuilder = new StringBuilder(baseFilePath)
                    .append(file.getSaveFilePath())
                    .append(File.separator)
                    .append(file.getSaveFileName());

            Path deleteFilePath = Paths.get(filePathBuilder.toString());
            File deleteFile = new File(deleteFilePath.toString());

            if(!deleteFile.exists()){
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }

            deleteFile.delete();
        }
    }

}
