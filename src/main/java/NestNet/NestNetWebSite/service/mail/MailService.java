package NestNet.NestNetWebSite.service.mail;

import NestNet.NestNetWebSite.api.ApiResult;
import NestNet.NestNetWebSite.exception.CustomException;
import NestNet.NestNetWebSite.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("#{environment['spring.mail.username']}")
    private String hostAddress;                           // 메일을 보내는 호스트 이메일 주소

    /*
    인증 코드를 이메일로 전송
     */
    public ApiResult<?> sendEmailAuthentication(String email){

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(hostAddress);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[CBNU 네스트넷] 이메일 인증 서비스");

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"); // 기본 폰트와 색상 설정
            emailBody.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4; border-radius: 8px;'>"); // 중앙 정렬과 배경 색상

            emailBody.append("<div style='text-align: center; margin-bottom: 20px;'>");
            emailBody.append("<h1 style='color: #0056b3;'>[CBNU Nestnet] 이메일 인증 서비스</h1>");
            emailBody.append("<p style='font-size: 18px; color: #555;'>안녕하세요!</p>");
            emailBody.append("<p style='font-size: 16px;'>다음 링크를 통해 이메일 인증을 완료해주세요. &#x1F648;</p>");
            emailBody.append("</div>");

            emailBody.append("<div style='background-color: #ffffff; padding: 20px; border-radius: 8px;'>");
            emailBody.append("<h2 style='color: #0056b3; font-size: 20px;'>문제:</h2>");
            emailBody.append("<p style='font-size: 18px; line-height: 1.5;'><strong>네스트넷에는 전통 있는 건배사가 있습니다. <br>건배사의 선창에 맞는 후창을 입력하세요.</strong></p><br>");
            emailBody.append("<p style='font-size: 20px; font-weight: bold; text-align: center;'><strong>Who is the BEST !</strong> <span style='font-size: 24px;'>_______</span> (소문자로 입력하세요)</p>");
            emailBody.append("</div>");

            emailBody.append("<div style='text-align: center; margin-top: 20px;'>");
            emailBody.append("<a href='http://nnet.cbnu.ac.kr/' style='display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #0056b3; text-decoration: none; border-radius: 4px;'>홈페이지로 돌아가기</a>");
            emailBody.append("</div>");

            emailBody.append("<div style='text-align: center; margin-top: 20px; font-size: 14px; color: #777;'>");
            emailBody.append("<p>CBNU Nestnet</p>");
            emailBody.append("</div>");

            emailBody.append("</div>"); // 중앙 정렬과 배경 색상
            emailBody.append("</body></html>");

            mimeMessageHelper.setText(emailBody.toString(), true);          //html형식으로 설정

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e){
            log.error("이메일 전송 실패 / 실패 이메일 주소 : {}", email);
            throw new CustomException(ErrorCode.CANNOT_SEND_EMAIL);
        }

        return ApiResult.success(email + " 로 인증 메일을 전송하였습니다.");
    }

    /*
    아이디를 이메일로 전송
     */
    public ApiResult<?> sendEmailLoginId(String email, String loginId){

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(hostAddress);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[CBNU 네스트넷] 회원 아이디 찾기 서비스");

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"); // 기본 폰트와 색상 설정
            emailBody.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4; border-radius: 8px;'>"); // 중앙 정렬과 배경 색상

            emailBody.append("<div style='text-align: center; margin-bottom: 20px;'>");
            emailBody.append("<h1 style='color: #0056b3;'>[CBNU Nestnet] 아이디 찾기 서비스</h1>");
            emailBody.append("</div>");

            emailBody.append("<div style='background-color: #ffffff; padding: 20px; border-radius: 8px;'>");
            emailBody.append("<p style='font-size: 18px; font-weight: bold;'>로그인 ID: <span style='color: #0056b3;'>").append(loginId).append("</span></p><br>");
            emailBody.append("<p style='font-size: 16px;'>홈페이지로 돌아가셔서 로그인 해주세요.</p>");
            emailBody.append("<p style='text-align: center; margin-top: 20px;'><a href='http://nnet.cbnu.ac.kr/' style='display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #0056b3; text-decoration: none; border-radius: 4px;'>홈페이지로 돌아가기</a></p>");
            emailBody.append("</div>");

            emailBody.append("<div style='text-align: center; margin-top: 20px; font-size: 14px; color: #777;'>");
            emailBody.append("<p>CBNU Nestnet</p>");
            emailBody.append("</div>");

            emailBody.append("</div>"); // 중앙 정렬과 배경 색상
            emailBody.append("</body></html>");

            mimeMessageHelper.setText(emailBody.toString(), true);          //html형식으로 설정

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e){
            log.error("이메일 전송 실패 / 실패 이메일 주소 : {}", email);
            throw new CustomException(ErrorCode.CANNOT_SEND_EMAIL);
        }

        return ApiResult.success(email + " 에게 아이디를 전송하였습니다.");
    }

    /*
    임시 비밀번호를 이메일로 전송
     */
    public ApiResult<?> sendEmailTemporaryPassword(String email, String password){

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(hostAddress);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[CBNU 네스트넷] 임시 비밀번호 발급");

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"); // 기본 폰트와 색상 설정
            emailBody.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4; border-radius: 8px;'>"); // 중앙 정렬과 배경 색상

            emailBody.append("<div style='text-align: center; margin-bottom: 20px;'>");
            emailBody.append("<h1 style='color: #0056b3;'>[CBNU Nestnet] 임시 비밀번호 발급 서비스</h1>");
            emailBody.append("</div>");

            emailBody.append("<div style='background-color: #ffffff; padding: 20px; border-radius: 8px;'>");
            emailBody.append("<p style='font-size: 18px; font-weight: bold;'>임시 비밀번호: <span style='color: #0056b3;'>").append(password).append("</span></p><br>");
            emailBody.append("<p style='font-size: 16px;'>홈페이지로 돌아가셔서 로그인 해주세요.</p>");
            emailBody.append("<p style='text-align: center; margin-top: 20px;'><a href='http://nnet.cbnu.ac.kr/' style='display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #0056b3; text-decoration: none; border-radius: 4px;'>홈페이지로 돌아가기</a></p>");
            emailBody.append("</div>");

            emailBody.append("<div style='text-align: center; margin-top: 20px; font-size: 14px; color: #777;'>");
            emailBody.append("<p>CBNU Nestnet</p>");
            emailBody.append("</div>");

            emailBody.append("</div>"); // 중앙 정렬과 배경 색상
            emailBody.append("</body></html>");

            mimeMessageHelper.setText(emailBody.toString(), true);          //html형식으로 설정

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e){
            log.error("이메일 전송 실패 / 실패 이메일 주소 : {}", email);
            throw new CustomException(ErrorCode.CANNOT_SEND_EMAIL);
        }
        return ApiResult.success(email + " 에게 임시 비밀번호를 전송하였습니다.");
    }
}
