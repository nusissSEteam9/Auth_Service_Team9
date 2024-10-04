package nus.iss.se.team9.auth_service_team9.service;

import nus.iss.se.team9.auth_service_team9.model.Admin;
import nus.iss.se.team9.auth_service_team9.model.Member;
import nus.iss.se.team9.auth_service_team9.model.User;
import nus.iss.se.team9.auth_service_team9.repo.MemberRepository;
import nus.iss.se.team9.auth_service_team9.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepo;
    @Autowired
    MemberRepository memberRepo;

    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public boolean checkIfAdmin(User user) {
        if (user instanceof Admin) {
            return true;
        } else if (user instanceof Member) {
            return false;
        }
        return false;
    }

    // Searching and Filtering methods
    public Member getMemberById(Integer id) {
        Optional<Member> member = memberRepo.findById(id);
        return member.orElse(null);
    }

    public void saveMember(Member member) {
        memberRepo.save(member);
    }

    public static String generateVerificationCode() {
        int codeLength = 4;
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            int digit = random.nextInt(10);
            codeBuilder.append(digit);
        }
        return codeBuilder.toString();
    }
}