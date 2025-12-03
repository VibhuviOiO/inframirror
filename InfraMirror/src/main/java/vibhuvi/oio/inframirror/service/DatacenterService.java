package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Datacenter}.
 */
public interface DatacenterService {
    /**
     * Save a datacenter.
     *
     * @param datacenterDTO the entity to save.
     * @return the persisted entity.
     */
    DatacenterDTO save(DatacenterDTO datacenterDTO);

    /**
     * Updates a datacenter.
     *
     * @param datacenterDTO the entity to update.
     * @return the persisted entity.
     */
    DatacenterDTO update(DatacenterDTO datacenterDTO);

    /**
     * Partially updates a datacenter.
     *
     * @param datacenterDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DatacenterDTO> partialUpdate(DatacenterDTO datacenterDTO);

    /**
     * Get the "id" datacenter.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DatacenterDTO> findOne(Long id);

    /**
     * Delete the "id" datacenter.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the datacenter corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DatacenterDTO> search(String query, Pageable pageable);
}
