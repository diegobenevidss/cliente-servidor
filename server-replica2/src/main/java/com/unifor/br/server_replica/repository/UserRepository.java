package com.unifor.br.server_replica.repository;

import com.unifor.br.server_replica.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Fica vazio mesmo! O JpaRepository já traz o .save() e a comunicação com o H2 prontos.
}