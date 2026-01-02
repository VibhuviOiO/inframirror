package vibhuvi.oio.inframirror.service.base;

import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.service.mapper.EntityMapper;

/**
 * Generic base service for CRUD operations.
 * Eliminates code duplication across all entity services.
 *
 * @param <D> DTO type
 * @param <E> Entity type
 */
public abstract class AbstractCrudService<D, E> {

    protected abstract Logger getLogger();
    protected abstract JpaRepository<E, Long> getRepository();
    protected abstract EntityMapper<D, E> getMapper();
    protected abstract String getEntityName();

    @Transactional
    public D save(D dto) {
        getLogger().debug("Request to save {} : {}", getEntityName(), dto);
        E entity = getMapper().toEntity(dto);
        entity = getRepository().save(entity);
        return getMapper().toDto(entity);
    }

    @Transactional
    public D update(D dto) {
        getLogger().debug("Request to update {} : {}", getEntityName(), dto);
        E entity = getMapper().toEntity(dto);
        entity = getRepository().save(entity);
        return getMapper().toDto(entity);
    }

    @Transactional
    public Optional<D> partialUpdate(D dto, Long id) {
        getLogger().debug("Request to partially update {} : {}", getEntityName(), dto);

        return getRepository()
            .findById(id)
            .map(existingEntity -> {
                getMapper().partialUpdate(existingEntity, dto);
                return existingEntity;
            })
            .map(getRepository()::save)
            .map(getMapper()::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<D> findOne(Long id) {
        getLogger().debug("Request to get {} : {}", getEntityName(), id);
        return getRepository().findById(id).map(getMapper()::toDto);
    }

    @Transactional
    public void delete(Long id) {
        getLogger().debug("Request to delete {} : {}", getEntityName(), id);
        getRepository().deleteById(id);
    }
}
