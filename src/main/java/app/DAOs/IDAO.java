package app.DAOs;

import jakarta.persistence.EntityManager;

import java.util.Set;

public interface IDAO <T> {
    T create(T t , EntityManager e);
    Set<T> getAll(EntityManager e);
    T getByID(int id, EntityManager e);
    T update(T t, EntityManager e);
    int delete(T t, EntityManager e);
}
