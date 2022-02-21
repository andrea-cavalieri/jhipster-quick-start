package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Foo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Foo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FooRepository extends MongoRepository<Foo, String> {}
