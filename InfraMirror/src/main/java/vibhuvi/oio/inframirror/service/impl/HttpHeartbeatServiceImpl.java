package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;
import vibhuvi.oio.inframirror.repository.HttpHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.HttpHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.HttpHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.HttpHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.HttpHeartbeatMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.HttpHeartbeat}.
 */
@Service
@Transactional
public class HttpHeartbeatServiceImpl implements HttpHeartbeatService {

    private static final Logger LOG = LoggerFactory.getLogger(HttpHeartbeatServiceImpl.class);

    private final HttpHeartbeatRepository httpHeartbeatRepository;

    private final HttpHeartbeatMapper httpHeartbeatMapper;

    private final HttpHeartbeatSearchRepository httpHeartbeatSearchRepository;

    public HttpHeartbeatServiceImpl(
        HttpHeartbeatRepository httpHeartbeatRepository,
        HttpHeartbeatMapper httpHeartbeatMapper,
        HttpHeartbeatSearchRepository httpHeartbeatSearchRepository
    ) {
        this.httpHeartbeatRepository = httpHeartbeatRepository;
        this.httpHeartbeatMapper = httpHeartbeatMapper;
        this.httpHeartbeatSearchRepository = httpHeartbeatSearchRepository;
    }

    @Override
    public HttpHeartbeatDTO save(HttpHeartbeatDTO httpHeartbeatDTO) {
        LOG.debug("Request to save HttpHeartbeat : {}", httpHeartbeatDTO);
        HttpHeartbeat httpHeartbeat = httpHeartbeatMapper.toEntity(httpHeartbeatDTO);
        httpHeartbeat = httpHeartbeatRepository.save(httpHeartbeat);
        httpHeartbeatSearchRepository.index(httpHeartbeat);
        return httpHeartbeatMapper.toDto(httpHeartbeat);
    }

    @Override
    public HttpHeartbeatDTO update(HttpHeartbeatDTO httpHeartbeatDTO) {
        LOG.debug("Request to update HttpHeartbeat : {}", httpHeartbeatDTO);
        HttpHeartbeat httpHeartbeat = httpHeartbeatMapper.toEntity(httpHeartbeatDTO);
        httpHeartbeat = httpHeartbeatRepository.save(httpHeartbeat);
        httpHeartbeatSearchRepository.index(httpHeartbeat);
        return httpHeartbeatMapper.toDto(httpHeartbeat);
    }

    @Override
    public Optional<HttpHeartbeatDTO> partialUpdate(HttpHeartbeatDTO httpHeartbeatDTO) {
        LOG.debug("Request to partially update HttpHeartbeat : {}", httpHeartbeatDTO);

        return httpHeartbeatRepository
            .findById(httpHeartbeatDTO.getId())
            .map(existingHttpHeartbeat -> {
                httpHeartbeatMapper.partialUpdate(existingHttpHeartbeat, httpHeartbeatDTO);

                return existingHttpHeartbeat;
            })
            .map(httpHeartbeatRepository::save)
            .map(savedHttpHeartbeat -> {
                httpHeartbeatSearchRepository.index(savedHttpHeartbeat);
                return savedHttpHeartbeat;
            })
            .map(httpHeartbeatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HttpHeartbeatDTO> findOne(Long id) {
        LOG.debug("Request to get HttpHeartbeat : {}", id);
        return httpHeartbeatRepository.findById(id).map(httpHeartbeatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete HttpHeartbeat : {}", id);
        httpHeartbeatRepository.deleteById(id);
        httpHeartbeatSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HttpHeartbeatDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of HttpHeartbeats for query {}", query);
        return httpHeartbeatSearchRepository.search(query, pageable).map(httpHeartbeatMapper::toDto);
    }
}
