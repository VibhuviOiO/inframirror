package vibhuvi.oio.inframirror.service;

import java.util.Optional;
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
}
