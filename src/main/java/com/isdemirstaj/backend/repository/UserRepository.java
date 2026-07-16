package com.isdemirstaj.backend.repository;

import com.isdemirstaj.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // Kullanıcı adı kalktığı için aramayı e-postaya göre yapacak metod:
    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByOper(String oper);  
}