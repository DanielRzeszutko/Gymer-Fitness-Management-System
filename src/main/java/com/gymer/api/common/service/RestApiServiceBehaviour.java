package com.gymer.api.common.service;

import org.springframework.data.domain.Sort;

public interface RestApiServiceBehaviour<T, V> {

    Iterable<T> getAllElements(Sort sort);

    T getElementById(V elementId);

    void updateElement(T element);

}
