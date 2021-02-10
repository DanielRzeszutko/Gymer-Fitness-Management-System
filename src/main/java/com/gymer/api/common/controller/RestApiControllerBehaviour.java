package com.gymer.api.common.controller;

import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;

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
    CollectionModel<K> getAllElementsSortable(Sort sort);

    /**
     * Endpoint with searching functionalities, work with ?sort= and together with ?contains=
     */
    CollectionModel<K> getAllElementsSortable(Sort sort, String searchBy);

}
