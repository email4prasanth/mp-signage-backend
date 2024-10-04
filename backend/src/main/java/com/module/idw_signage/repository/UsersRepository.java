package com.module.idw_signage.repository;

import com.module.idw_signage.dto.UsersResponseDTO;
import com.module.idw_signage.model.Stores;
import com.module.idw_signage.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    List<Users> findAll(Sort sort);


    Page<Users> findByUsernameContainingOrFullnameContainingOrEmailContaining(String username,
                                                                              String fullname,
                                                                              String email,
                                                                              Pageable pageable);

    Users findByEmail(String email);

    @Query(value = """
            
            WITH RECURSIVE users_list_cte(userId, username, full_name, email, last_login, role, created_at, created_by, userLevel) AS (
            SELECT DISTINCT u.id as userId, u.username, u.full_name, u.email, u.last_login, u.role, u.created_at, u.created_by, 0 AS userLevel
            FROM signage_db.users AS u
            WHERE u.id = :userIdField
 
    UNION ALL

    SELECT u.id as userId, u.username, u.full_name, u.email, u.last_login, u.role, u.created_at, u.created_by, cte.userLevel + 1
    FROM signage_db.users AS u
    INNER JOIN users_list_cte AS cte ON u.created_by = cte.userId
)
    SELECT *, (SELECT COUNT(*) FROM users_list_cte WHERE (username LIKE CONCAT('%', :searchField, '%')
    OR full_name LIKE CONCAT('%', :searchField, '%')
    OR email LIKE CONCAT('%', :searchField, '%'))) AS total_count
    FROM users_list_cte
    WHERE (username LIKE CONCAT('%', :searchField, '%')
    OR full_name LIKE CONCAT('%', :searchField, '%')
    OR email LIKE CONCAT('%', :searchField, '%'))
    ORDER BY created_at DESC
    LIMIT :limitValue OFFSET :offsetValue
""", nativeQuery = true)
    List<Object[]> customFindUsersWithRecursiveQuery(
            @Param("userIdField") String userIdField,
            @Param("searchField") String searchField,
            @Param("offsetValue") int offsetValue,
            @Param("limitValue") int limitValue);
}
