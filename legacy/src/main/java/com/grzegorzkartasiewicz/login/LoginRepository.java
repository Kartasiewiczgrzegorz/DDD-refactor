package com.grzegorzkartasiewicz.login;



import java.util.List;
import java.util.Optional;

interface LoginRepository {
    List<Login> findAll();

    Optional<Login> findById(Integer id);

    Optional<Login> findByNick(String nick);

    Login save(Login entity);
}
