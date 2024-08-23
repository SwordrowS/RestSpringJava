package swordrows.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swordrows.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	@Query ("SELECT u FROM User WHERE user.username=:userName")
	User findByUsername(@Param("userName") String userName);
	
	
}
