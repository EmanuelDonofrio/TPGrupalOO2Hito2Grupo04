package com.epicentroGourmet.repositories;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epicentroGourmet.entities.User;

@Repository("userRepository")
public interface IUserRepository extends JpaRepository<User, Serializable> {

	boolean existsByUsername(String username);

	@Query("SELECT u FROM User u JOIN FETCH u.userRole WHERE u.username = (:username)")
	public abstract User findByUsernameAndFetchUserRolesEagerly(@Param("username") String username);

	User findByUsername(String username);
}
