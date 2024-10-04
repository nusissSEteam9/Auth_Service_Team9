package nus.iss.se.team9.auth_service_team9.repo;


import nus.iss.se.team9.auth_service_team9.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
	User findByUsername(String username);

}
