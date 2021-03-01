package com.gymer.resources.common.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
public abstract class AbstractRestApiService<T, V> implements RestApiServiceBehaviour<T, V> {

    protected final PagingAndSortingRepository<T, V> repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> getAllElements(Pageable pageable) {
        return repository.findAll(pageable);
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

    /**
     * Service method returning true if element with specified Id exist in database
     */
    @Override
    public boolean isElementExistById(V elementId) {
        return repository.existsById(elementId);
    }

}
