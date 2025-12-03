package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;
import vibhuvi.oio.inframirror.repository.search.HttpMonitorSearchRepository;
import vibhuvi.oio.inframirror.service.HttpMonitorService;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;
import vibhuvi.oio.inframirror.service.mapper.HttpMonitorMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.HttpMonitor}.
 */
@Service
@Transactional
public class HttpMonitorServiceImpl implements HttpMonitorService {

    private static final Logger LOG = LoggerFactory.getLogger(HttpMonitorServiceImpl.class);

    private final HttpMonitorRepository httpMonitorRepository;

    private final HttpMonitorMapper httpMonitorMapper;

    private final HttpMonitorSearchRepository httpMonitorSearchRepository;

    public HttpMonitorServiceImpl(
        HttpMonitorRepository httpMonitorRepository,
        HttpMonitorMapper httpMonitorMapper,
        HttpMonitorSearchRepository httpMonitorSearchRepository
    ) {
        this.httpMonitorRepository = httpMonitorRepository;
        this.httpMonitorMapper = httpMonitorMapper;
        this.httpMonitorSearchRepository = httpMonitorSearchRepository;
    }

    @Override
    public HttpMonitorDTO save(HttpMonitorDTO httpMonitorDTO) {
        LOG.debug("Request to save HttpMonitor : {}", httpMonitorDTO);
        HttpMonitor httpMonitor = httpMonitorMapper.toEntity(httpMonitorDTO);
        httpMonitor = httpMonitorRepository.save(httpMonitor);
        httpMonitorSearchRepository.index(httpMonitor);
        return httpMonitorMapper.toDto(httpMonitor);
    }

    @Override
    public HttpMonitorDTO update(HttpMonitorDTO httpMonitorDTO) {
        LOG.debug("Request to update HttpMonitor : {}", httpMonitorDTO);
        HttpMonitor httpMonitor = httpMonitorMapper.toEntity(httpMonitorDTO);
        httpMonitor = httpMonitorRepository.save(httpMonitor);
        httpMonitorSearchRepository.index(httpMonitor);
        return httpMonitorMapper.toDto(httpMonitor);
    }

    @Override
    public Optional<HttpMonitorDTO> partialUpdate(HttpMonitorDTO httpMonitorDTO) {
        LOG.debug("Request to partially update HttpMonitor : {}", httpMonitorDTO);

        return httpMonitorRepository
            .findById(httpMonitorDTO.getId())
            .map(existingHttpMonitor -> {
                httpMonitorMapper.partialUpdate(existingHttpMonitor, httpMonitorDTO);

                return existingHttpMonitor;
            })
            .map(httpMonitorRepository::save)
            .map(savedHttpMonitor -> {
                httpMonitorSearchRepository.index(savedHttpMonitor);
                return savedHttpMonitor;
            })
            .map(httpMonitorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HttpMonitorDTO> findOne(Long id) {
        LOG.debug("Request to get HttpMonitor : {}", id);
        return httpMonitorRepository.findById(id).map(httpMonitorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete HttpMonitor : {}", id);
        httpMonitorRepository.deleteById(id);
        httpMonitorSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HttpMonitorDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of HttpMonitors for query {}", query);
        return httpMonitorSearchRepository.search(query, pageable).map(httpMonitorMapper::toDto);
    }
}
