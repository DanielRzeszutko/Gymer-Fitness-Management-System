package com.gymer.api.common.service;

import org.springframework.data.domain.Sort;

public interface RestApiServiceBehaviour<T, V> {

    /**
     * Service method responsible for getting all elements only with sort parameter
     */
    Iterable<T> getAllElements(Sort sort);

    /**
     * Service method responsible for getting element from selected repository
     * throws NOT_FOUND if element with specified ID don't exist
     */
    T getElementById(V elementId);

    /**
     * Service method responsible for adding or updating elements in database
     */
    void updateElement(T element);

    /**
     * Service method responsible for searching for elements with sort and searchBy parameters
     */
    Iterable<T> findAllContaining(Sort sort, String searchBy);

    /**
     * Service method responsible for returning true if object exists in databse
     */
    boolean isElementExistById(V elementId);

}
