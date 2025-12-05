package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.StatusPage;
import vibhuvi.oio.inframirror.repository.StatusPageRepository;
import vibhuvi.oio.inframirror.repository.search.StatusPageSearchRepository;
import vibhuvi.oio.inframirror.service.StatusPageService;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusPageMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.StatusPage}.
 */
@Service
@Transactional
public class StatusPageServiceImpl implements StatusPageService {

    private static final Logger LOG = LoggerFactory.getLogger(StatusPageServiceImpl.class);

    private final StatusPageRepository statusPageRepository;

    private final StatusPageMapper statusPageMapper;

    private final StatusPageSearchRepository statusPageSearchRepository;

    public StatusPageServiceImpl(
        StatusPageRepository statusPageRepository,
        StatusPageMapper statusPageMapper,
        StatusPageSearchRepository statusPageSearchRepository
    ) {
        this.statusPageRepository = statusPageRepository;
        this.statusPageMapper = statusPageMapper;
        this.statusPageSearchRepository = statusPageSearchRepository;
    }

    @Override
    public StatusPageDTO save(StatusPageDTO statusPageDTO) {
        LOG.debug("Request to save StatusPage : {}", statusPageDTO);
        StatusPage statusPage = statusPageMapper.toEntity(statusPageDTO);
        statusPage = statusPageRepository.save(statusPage);
        statusPageSearchRepository.index(statusPage);
        return statusPageMapper.toDto(statusPage);
    }

    @Override
    public StatusPageDTO update(StatusPageDTO statusPageDTO) {
        LOG.debug("Request to update StatusPage : {}", statusPageDTO);
        StatusPage statusPage = statusPageMapper.toEntity(statusPageDTO);
        statusPage = statusPageRepository.save(statusPage);
        statusPageSearchRepository.index(statusPage);
        return statusPageMapper.toDto(statusPage);
    }

    @Override
    public Optional<StatusPageDTO> partialUpdate(StatusPageDTO statusPageDTO) {
        LOG.debug("Request to partially update StatusPage : {}", statusPageDTO);

        return statusPageRepository
            .findById(statusPageDTO.getId())
            .map(existingStatusPage -> {
                statusPageMapper.partialUpdate(existingStatusPage, statusPageDTO);

                return existingStatusPage;
            })
            .map(statusPageRepository::save)
            .map(savedStatusPage -> {
                statusPageSearchRepository.index(savedStatusPage);
                return savedStatusPage;
            })
            .map(statusPageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StatusPageDTO> findOne(Long id) {
        LOG.debug("Request to get StatusPage : {}", id);
        return statusPageRepository.findById(id).map(statusPageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StatusPage : {}", id);
        statusPageRepository.deleteById(id);
        statusPageSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatusPageDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of StatusPages for query {}", query);
        return statusPageSearchRepository.search(query, pageable).map(statusPageMapper::toDto);
    }
}
