package nus.iss.se.team9.auth_service_team9.repo;

import nus.iss.se.team9.auth_service_team9.model.Member;
import nus.iss.se.team9.auth_service_team9.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Integer>{
	List<Member> findByMemberStatusNot(Status status);

	Member findByUsername(String username);
}
