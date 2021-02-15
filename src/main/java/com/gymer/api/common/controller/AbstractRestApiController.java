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
        return searchBy == null
                ? getAllElementsSortable(sort)
                : getCollectionModel((List<T>) service.findAllContaining(sort, searchBy));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionModel<K> getAllElementsSortable(Sort sort) {
        List<T> elements = (List<T>) service.getAllElements(sort);
        return getCollectionModel(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K getElementById(V id) {
        return convertToDTO(service.getElementById(id));
    }

    protected CollectionModel<K> getCollectionModel(List<T> elements) {
        return CollectionModel.of(elements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

}
