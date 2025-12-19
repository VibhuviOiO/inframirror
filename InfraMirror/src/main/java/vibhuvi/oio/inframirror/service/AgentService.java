package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Agent}.
 */
public interface AgentService {
    /**
     * Save a agent.
     *
     * @param agentDTO the entity to save.
     * @return the persisted entity.
     */
    AgentDTO save(AgentDTO agentDTO);

    /**
     * Updates a agent.
     *
     * @param agentDTO the entity to update.
     * @return the persisted entity.
     */
    AgentDTO update(AgentDTO agentDTO);

    /**
     * Partially updates a agent.
     *
     * @param agentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AgentDTO> partialUpdate(AgentDTO agentDTO);

    /**
     * Get the "id" agent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AgentDTO> findOne(Long id);

    /**
     * Delete the "id" agent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the agent corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgentDTO> search(String query, Pageable pageable);

    /**
     * Register a new agent with auto-creation of region and datacenter.
     *
     * @param request the registration request.
     * @return the registration response.
     */
    vibhuvi.oio.inframirror.service.dto.AgentRegistrationResponseDTO registerAgent(
        vibhuvi.oio.inframirror.service.dto.AgentRegistrationRequestDTO request
    );

    /**
     * Update agent's last seen timestamp.
     *
     * @param agentId the agent ID.
     */
    void updateLastSeen(Long agentId);

    /**
     * Update agent's last seen timestamp by API key.
     *
     * @param apiKey the agent API key.
     */
    void updateLastSeenByApiKey(String apiKey);
}
