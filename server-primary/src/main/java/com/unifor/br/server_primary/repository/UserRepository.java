package com.unifor.br.server_primary.repository;

import com.unifor.br.server_primary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Fica vazio mesmo! O Spring cria o método save() automaticamente devolvendo o User.
}