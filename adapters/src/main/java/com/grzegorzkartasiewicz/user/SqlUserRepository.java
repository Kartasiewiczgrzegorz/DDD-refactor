package com.grzegorzkartasiewicz.user;


import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

interface SqlUserRepository extends Repository<UserSnapshot,Integer> {
    List<UserSnapshot> findAll();

    Optional<UserSnapshot> findById(Integer id);

    UserSnapshot save(UserSnapshot entity);

    List<UserSnapshot> findAllByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
}

@org.springframework.stereotype.Repository
class UserRepositoryImpl implements UserRepository {
    private final SqlUserRepository repository;

    UserRepositoryImpl(final SqlUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> findAll() {
        return  repository.findAll().stream().map(User::restore).toList();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return repository.findById(id).map(User::restore);
    }

    @Override
    public User save(User entity) {
        return User.restore(repository.save(entity.getSnapshot()));
    }

    @Override
    public List<User> findAllByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String query) {
        return repository.findAllByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(query, query).stream()
                .map(User::restore)
                .toList();
    }
}