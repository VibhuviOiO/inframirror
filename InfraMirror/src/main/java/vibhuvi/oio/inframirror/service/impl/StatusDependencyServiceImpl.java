package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.StatusDependency;
import vibhuvi.oio.inframirror.repository.StatusDependencyRepository;
import vibhuvi.oio.inframirror.repository.search.StatusDependencySearchRepository;
import vibhuvi.oio.inframirror.service.StatusDependencyService;
import vibhuvi.oio.inframirror.service.dto.StatusDependencyDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusDependencyMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.StatusDependency}.
 */
@Service
@Transactional
public class StatusDependencyServiceImpl implements StatusDependencyService {

    private static final Logger LOG = LoggerFactory.getLogger(StatusDependencyServiceImpl.class);

    private final StatusDependencyRepository statusDependencyRepository;

    private final StatusDependencyMapper statusDependencyMapper;

    private final StatusDependencySearchRepository statusDependencySearchRepository;

    public StatusDependencyServiceImpl(
        StatusDependencyRepository statusDependencyRepository,
        StatusDependencyMapper statusDependencyMapper,
        StatusDependencySearchRepository statusDependencySearchRepository
    ) {
        this.statusDependencyRepository = statusDependencyRepository;
        this.statusDependencyMapper = statusDependencyMapper;
        this.statusDependencySearchRepository = statusDependencySearchRepository;
    }

    @Override
    public StatusDependencyDTO save(StatusDependencyDTO statusDependencyDTO) {
        LOG.debug("Request to save StatusDependency : {}", statusDependencyDTO);
        StatusDependency statusDependency = statusDependencyMapper.toEntity(statusDependencyDTO);
        statusDependency = statusDependencyRepository.save(statusDependency);
        statusDependencySearchRepository.index(statusDependency);
        return statusDependencyMapper.toDto(statusDependency);
    }

    @Override
    public StatusDependencyDTO update(StatusDependencyDTO statusDependencyDTO) {
        LOG.debug("Request to update StatusDependency : {}", statusDependencyDTO);
        StatusDependency statusDependency = statusDependencyMapper.toEntity(statusDependencyDTO);
        statusDependency = statusDependencyRepository.save(statusDependency);
        statusDependencySearchRepository.index(statusDependency);
        return statusDependencyMapper.toDto(statusDependency);
    }

    @Override
    public Optional<StatusDependencyDTO> partialUpdate(StatusDependencyDTO statusDependencyDTO) {
        LOG.debug("Request to partially update StatusDependency : {}", statusDependencyDTO);

        return statusDependencyRepository
            .findById(statusDependencyDTO.getId())
            .map(existingStatusDependency -> {
                statusDependencyMapper.partialUpdate(existingStatusDependency, statusDependencyDTO);

                return existingStatusDependency;
            })
            .map(statusDependencyRepository::save)
            .map(savedStatusDependency -> {
                statusDependencySearchRepository.index(savedStatusDependency);
                return savedStatusDependency;
            })
            .map(statusDependencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatusDependencyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all StatusDependencies");
        return statusDependencyRepository.findAll(pageable).map(statusDependencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StatusDependencyDTO> findOne(Long id) {
        LOG.debug("Request to get StatusDependency : {}", id);
        return statusDependencyRepository.findById(id).map(statusDependencyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StatusDependency : {}", id);
        statusDependencyRepository.deleteById(id);
        statusDependencySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatusDependencyDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of StatusDependencies for query {}", query);
        return statusDependencySearchRepository.search(query, pageable).map(statusDependencyMapper::toDto);
    }
}
