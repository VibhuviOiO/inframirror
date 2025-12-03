package vibhuvi.oio.inframirror.service;

import java.util.List;
import java.util.Optional;
import vibhuvi.oio.inframirror.service.dto.ScheduleDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Schedule}.
 */
public interface ScheduleService {
    /**
     * Save a schedule.
     *
     * @param scheduleDTO the entity to save.
     * @return the persisted entity.
     */
    ScheduleDTO save(ScheduleDTO scheduleDTO);

    /**
     * Updates a schedule.
     *
     * @param scheduleDTO the entity to update.
     * @return the persisted entity.
     */
    ScheduleDTO update(ScheduleDTO scheduleDTO);

    /**
     * Partially updates a schedule.
     *
     * @param scheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO);

    /**
     * Get the "id" schedule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScheduleDTO> findOne(Long id);

    /**
     * Delete the "id" schedule.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the schedule corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<ScheduleDTO> search(String query);
}
