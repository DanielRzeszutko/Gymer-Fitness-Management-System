package com.gymer.resources.common.controller;

import com.gymer.resources.common.service.RestApiServiceBehaviour;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@AllArgsConstructor
public abstract class AbstractRestApiController<K, T, V> implements RestApiControllerBehaviour<K, T, V> {

    protected final RestApiServiceBehaviour<T, V> service;

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedModel<EntityModel<K>> getAllElementsSortable(Pageable pageable, String searchBy, PagedResourcesAssembler<K> assembler) {
        return searchBy == null
                ? getAllElementsSortable(pageable, assembler)
                : getCollectionModel((Page<T>) service.findAllContaining(pageable, searchBy), assembler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedModel<EntityModel<K>> getAllElementsSortable(Pageable pageable, PagedResourcesAssembler<K> assembler) {
        Page<T> elements = (Page<T>) service.getAllElements(pageable);
        return getCollectionModel(elements, assembler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K getElementById(V id) {
        return convertToDTO(service.getElementById(id));
    }

    protected PagedModel<EntityModel<K>> getCollectionModel(Page<T> elements, PagedResourcesAssembler<K> assembler) {
        return assembler.toModel(elements.map(this::convertToDTO));
    }

}
