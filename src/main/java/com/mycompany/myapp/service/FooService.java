package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Foo;
import com.mycompany.myapp.repository.FooRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Foo}.
 */
@Service
public class FooService {

    private final Logger log = LoggerFactory.getLogger(FooService.class);

    private final FooRepository fooRepository;

    public FooService(FooRepository fooRepository) {
        this.fooRepository = fooRepository;
    }

    /**
     * Save a foo.
     *
     * @param foo the entity to save.
     * @return the persisted entity.
     */
    public Foo save(Foo foo) {
        log.debug("Request to save Foo : {}", foo);
        return fooRepository.save(foo);
    }

    /**
     * Partially update a foo.
     *
     * @param foo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Foo> partialUpdate(Foo foo) {
        log.debug("Request to partially update Foo : {}", foo);

        return fooRepository
            .findById(foo.getId())
            .map(existingFoo -> {
                if (foo.getSurname() != null) {
                    existingFoo.setSurname(foo.getSurname());
                }
                if (foo.getName() != null) {
                    existingFoo.setName(foo.getName());
                }

                return existingFoo;
            })
            .map(fooRepository::save);
    }

    /**
     * Get all the foos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Foo> findAll(Pageable pageable) {
        log.debug("Request to get all Foos");
        return fooRepository.findAll(pageable);
    }

    /**
     * Get one foo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Foo> findOne(String id) {
        log.debug("Request to get Foo : {}", id);
        return fooRepository.findById(id);
    }

    /**
     * Delete the foo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Foo : {}", id);
        fooRepository.deleteById(id);
    }
}
