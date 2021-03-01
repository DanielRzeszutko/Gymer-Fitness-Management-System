package com.gymer.resources.common.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

public interface RestApiControllerBehaviour<K, T, V> {

    /**
     * Endpoint only showing one resource with selected ID
     */
    K getElementById(V id);

    /**
     * Service method responsible for converting Entity to DTO
     */
    K convertToDTO(T element);

    /**
     * Service method responsible for converting DTO to Entity
     */
    T convertToEntity(K element);

    /**
     * Endpoint with searching functionalities, work with ?sort=
     */
    PagedModel<EntityModel<K>> getAllElementsSortable(Pageable pageable, PagedResourcesAssembler<K> assembler);

    /**
     * Endpoint with searching functionalities, work with ?sort= and together with ?contains=
     */
    PagedModel<EntityModel<K>> getAllElementsSortable(Pageable pageable, String searchBy, PagedResourcesAssembler<K> assembler);

}
