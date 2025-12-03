package vibhuvi.oio.inframirror.service;

import java.util.List;
import java.util.Optional;
import vibhuvi.oio.inframirror.service.dto.AgentMonitorDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.AgentMonitor}.
 */
public interface AgentMonitorService {
    /**
     * Save a agentMonitor.
     *
     * @param agentMonitorDTO the entity to save.
     * @return the persisted entity.
     */
    AgentMonitorDTO save(AgentMonitorDTO agentMonitorDTO);

    /**
     * Updates a agentMonitor.
     *
     * @param agentMonitorDTO the entity to update.
     * @return the persisted entity.
     */
    AgentMonitorDTO update(AgentMonitorDTO agentMonitorDTO);

    /**
     * Partially updates a agentMonitor.
     *
     * @param agentMonitorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AgentMonitorDTO> partialUpdate(AgentMonitorDTO agentMonitorDTO);

    /**
     * Get all the agentMonitors.
     *
     * @return the list of entities.
     */
    List<AgentMonitorDTO> findAll();

    /**
     * Get the "id" agentMonitor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AgentMonitorDTO> findOne(Long id);

    /**
     * Delete the "id" agentMonitor.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the agentMonitor corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<AgentMonitorDTO> search(String query);
}
