package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.BrandingDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Branding}.
 */
public interface BrandingService {
    /**
     * Save a branding.
     *
     * @param brandingDTO the entity to save.
     * @return the persisted entity.
     */
    BrandingDTO save(BrandingDTO brandingDTO);

    /**
     * Updates a branding.
     *
     * @param brandingDTO the entity to update.
     * @return the persisted entity.
     */
    BrandingDTO update(BrandingDTO brandingDTO);

    /**
     * Partially updates a branding.
     *
     * @param brandingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BrandingDTO> partialUpdate(BrandingDTO brandingDTO);

    /**
     * Get all the brandings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BrandingDTO> findAll(Pageable pageable);

    /**
     * Get the "id" branding.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BrandingDTO> findOne(Long id);

    /**
     * Delete the "id" branding.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the branding corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BrandingDTO> search(String query, Pageable pageable);
}
