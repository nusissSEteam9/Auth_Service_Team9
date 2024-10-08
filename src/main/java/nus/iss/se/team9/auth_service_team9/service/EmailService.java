package nus.iss.se.team9.auth_service_team9.service;

import nus.iss.se.team9.auth_service_team9.model.EmailDetails;
import nus.iss.se.team9.auth_service_team9.model.Member;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public EmailDetails generateVerifyCodeEmailObj(Member newMember, String code){
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setTo(newMember.getEmail());
        emailDetails.setSubject("Email Verify");
        emailDetails.setBody("Your Email Verification Code is :" + code);
        return emailDetails;
    }
}
