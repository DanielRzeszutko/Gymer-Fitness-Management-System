package com.gymer.api.common.controller;

import com.gymer.api.common.service.RestApiServiceBehaviour;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRestApiController<K, T, V> implements RestApiControllerBehaviour<K, T, V> {

    protected final RestApiServiceBehaviour<T, V> service;

    public AbstractRestApiController(RestApiServiceBehaviour<T, V> service) {
        this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionModel<K> getAllElementsSortable(Sort sort, String searchBy) {
        if (searchBy == null) return getAllElementsSortable(sort);
        List<T> elements = (List<T>) service.findAllContaining(sort, searchBy);
        return getAllElements(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionModel<K> getAllElementsSortable(Sort sort) {
        List<T> elements = (List<T>) service.getAllElements(sort);
        return getAllElements(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K getElementById(V id) {
        return convertToDTO(service.getElementById(id));
    }

    private CollectionModel<K> getAllElements(List<T> elements) {
        return CollectionModel.of(elements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

}
