package com.gymer.api.common.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class AbstractRestApiService<T, V> implements RestApiServiceBehaviour<T, V> {

    protected final PagingAndSortingRepository<T, V> repository;

    public AbstractRestApiService(PagingAndSortingRepository<T, V> repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> getAllElements(Sort sort) {
        return repository.findAll(sort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getElementById(V elementId) {
        return repository.findById(elementId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateElement(T element) {
        repository.save(element);
    }

    public boolean isElementExistById(V elementId) {
        return repository.existsById(elementId);
    }

}
