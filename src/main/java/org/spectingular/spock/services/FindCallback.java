package org.spectingular.spock.services;

import org.spectingular.spock.exceptions.NotFoundException;

/**
 * Callback for finding pre requisites*
 * @param <T> The return type of the callback.
 * @param <V> The provided object to the callback method.
 */
public interface FindCallback<T, V> {
    /**
     * Find the object T.
     * @param v The provided object.
     * @return t The result.
     */
    T find(V v) throws NotFoundException;
}