package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.AgentLockDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.AgentLock}.
 */
public interface AgentLockService {
    /**
     * Save a agentLock.
     *
     * @param agentLockDTO the entity to save.
     * @return the persisted entity.
     */
    AgentLockDTO save(AgentLockDTO agentLockDTO);

    /**
     * Updates a agentLock.
     *
     * @param agentLockDTO the entity to update.
     * @return the persisted entity.
     */
    AgentLockDTO update(AgentLockDTO agentLockDTO);

    /**
     * Partially updates a agentLock.
     *
     * @param agentLockDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AgentLockDTO> partialUpdate(AgentLockDTO agentLockDTO);

    /**
     * Get all the agentLocks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgentLockDTO> findAll(Pageable pageable);

    /**
     * Get the "id" agentLock.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AgentLockDTO> findOne(Long id);

    /**
     * Delete the "id" agentLock.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the agentLock corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgentLockDTO> search(String query, Pageable pageable);
}
