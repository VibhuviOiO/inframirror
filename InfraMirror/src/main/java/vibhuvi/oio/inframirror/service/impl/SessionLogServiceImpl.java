package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.SessionLog;
import vibhuvi.oio.inframirror.repository.SessionLogRepository;
import vibhuvi.oio.inframirror.repository.search.SessionLogSearchRepository;
import vibhuvi.oio.inframirror.service.SessionLogService;
import vibhuvi.oio.inframirror.service.dto.SessionLogDTO;
import vibhuvi.oio.inframirror.service.mapper.SessionLogMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.SessionLog}.
 */
@Service
@Transactional
public class SessionLogServiceImpl implements SessionLogService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionLogServiceImpl.class);

    private final SessionLogRepository sessionLogRepository;

    private final SessionLogMapper sessionLogMapper;

    private final SessionLogSearchRepository sessionLogSearchRepository;

    public SessionLogServiceImpl(
        SessionLogRepository sessionLogRepository,
        SessionLogMapper sessionLogMapper,
        SessionLogSearchRepository sessionLogSearchRepository
    ) {
        this.sessionLogRepository = sessionLogRepository;
        this.sessionLogMapper = sessionLogMapper;
        this.sessionLogSearchRepository = sessionLogSearchRepository;
    }

    @Override
    public SessionLogDTO save(SessionLogDTO sessionLogDTO) {
        LOG.debug("Request to save SessionLog : {}", sessionLogDTO);
        SessionLog sessionLog = sessionLogMapper.toEntity(sessionLogDTO);
        sessionLog = sessionLogRepository.save(sessionLog);
        sessionLogSearchRepository.index(sessionLog);
        return sessionLogMapper.toDto(sessionLog);
    }

    @Override
    public SessionLogDTO update(SessionLogDTO sessionLogDTO) {
        LOG.debug("Request to update SessionLog : {}", sessionLogDTO);
        SessionLog sessionLog = sessionLogMapper.toEntity(sessionLogDTO);
        sessionLog = sessionLogRepository.save(sessionLog);
        sessionLogSearchRepository.index(sessionLog);
        return sessionLogMapper.toDto(sessionLog);
    }

    @Override
    public Optional<SessionLogDTO> partialUpdate(SessionLogDTO sessionLogDTO) {
        LOG.debug("Request to partially update SessionLog : {}", sessionLogDTO);

        return sessionLogRepository
            .findById(sessionLogDTO.getId())
            .map(existingSessionLog -> {
                sessionLogMapper.partialUpdate(existingSessionLog, sessionLogDTO);

                return existingSessionLog;
            })
            .map(sessionLogRepository::save)
            .map(savedSessionLog -> {
                sessionLogSearchRepository.index(savedSessionLog);
                return savedSessionLog;
            })
            .map(sessionLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionLogDTO> findOne(Long id) {
        LOG.debug("Request to get SessionLog : {}", id);
        return sessionLogRepository.findById(id).map(sessionLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SessionLog : {}", id);
        sessionLogRepository.deleteById(id);
        sessionLogSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionLogDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of SessionLogs for query {}", query);
        return sessionLogSearchRepository.search(query, pageable).map(sessionLogMapper::toDto);
    }
}
