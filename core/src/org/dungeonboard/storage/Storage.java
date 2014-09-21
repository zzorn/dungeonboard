package org.dungeonboard.storage;

import com.badlogic.gdx.utils.LongMap;

/**
 * Keeps track of all game objects of some type with an associated id.
 */
public class Storage<T extends Entity> {

    private LongMap<T> entities = new LongMap<T>();
    private long nextFreeId = 1;
    private final Class<T> type;

    public Storage(Class<T> type) {
        this.type = type;
    }

    /**
     * @return the entity with the specified id.
     */
    public T get(long id) {
        return entities.get(id);
    }

    /**
     * @return an iterator over all the entities of this type.
     */
    public LongMap.Entries<T> getAll() {
        return entities.entries();
    }

    /**
     * Creates a new entity of this type.
     */
    public T create() {
        return create(createNextFreeId());
    }

    /**
     * Creates a new entity of this type with the specified id (should not be the same as an existing id).
     */
    public T create(long id) {
        if (entities.containsKey(id)) throw new IllegalArgumentException("The id " + id + " already exists!");

        final T entity = createInstance();
        entity.setId(createNextFreeId());
        entities.put(entity.getId(), entity);
        return entity;
    }


    /**
     * Deletes the most recently created entity and returns its id to free ids.
     */
    public void unCreate() {
        nextFreeId--;
        delete(nextFreeId);
    }


    /**
     * Delete the specified entity.
     */
    public void delete(T entity) {
        entities.remove(entity.getId());
    }

    /**
     * Delete the entity with the specified id.
     */
    public void delete(long id) {
        entities.remove(id);
    }

    /**
     * @return a new instance of the type stored here.
     */
    protected T createInstance() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't create entity of type " + type.getName() + ": " + e.getMessage(), e);
        }
    }

    private long createNextFreeId() {
        return nextFreeId++;
    }
}
