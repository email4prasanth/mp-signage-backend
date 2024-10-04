package com.idw_signage.userauthentication.repository;



import com.idw_signage.userauthentication.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByUsername(String username);

    Optional<Users> findById(String userId);

    Optional<Users> findByEmail(String username);
}

