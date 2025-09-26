package com.grzegorzkartasiewicz.login;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


interface SqlLoginRepository extends Repository<LoginSnapshot,Integer> {
    List<LoginSnapshot> findAll();

    Optional<LoginSnapshot> findById(Integer id);

    Optional<LoginSnapshot> findByNick(String nick);

    LoginSnapshot save(LoginSnapshot entity);
}

@org.springframework.stereotype.Repository
class LoginRepositoryImpl implements LoginRepository {
    private final SqlLoginRepository repository;

    LoginRepositoryImpl(final SqlLoginRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Login> findAll() {
        return repository.findAll().stream().map(Login::restore).toList();
    }

    @Override
    public Optional<Login> findById(Integer id) {
        return repository.findById(id).map(Login::restore);
    }

    @Override
    public Optional<Login> findByNick(String nick) {
        return repository.findByNick(nick).map(Login::restore);
    }

    @Override
    public Login save(Login entity) {
        return Login.restore(repository.save(entity.getSnapshot()));
    }
}