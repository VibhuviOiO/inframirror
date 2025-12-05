package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.repository.AuditTrailRepository;
import vibhuvi.oio.inframirror.repository.search.AuditTrailSearchRepository;
import vibhuvi.oio.inframirror.service.AuditTrailService;
import vibhuvi.oio.inframirror.service.dto.AuditTrailDTO;
import vibhuvi.oio.inframirror.service.mapper.AuditTrailMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.AuditTrail}.
 */
@Service
@Transactional
public class AuditTrailServiceImpl implements AuditTrailService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTrailServiceImpl.class);

    private final AuditTrailRepository auditTrailRepository;

    private final AuditTrailMapper auditTrailMapper;

    private final AuditTrailSearchRepository auditTrailSearchRepository;

    public AuditTrailServiceImpl(
        AuditTrailRepository auditTrailRepository,
        AuditTrailMapper auditTrailMapper,
        AuditTrailSearchRepository auditTrailSearchRepository
    ) {
        this.auditTrailRepository = auditTrailRepository;
        this.auditTrailMapper = auditTrailMapper;
        this.auditTrailSearchRepository = auditTrailSearchRepository;
    }

    @Override
    public AuditTrailDTO save(AuditTrailDTO auditTrailDTO) {
        LOG.debug("Request to save AuditTrail : {}", auditTrailDTO);
        AuditTrail auditTrail = auditTrailMapper.toEntity(auditTrailDTO);
        auditTrail = auditTrailRepository.save(auditTrail);
        auditTrailSearchRepository.index(auditTrail);
        return auditTrailMapper.toDto(auditTrail);
    }

    @Override
    public AuditTrailDTO update(AuditTrailDTO auditTrailDTO) {
        LOG.debug("Request to update AuditTrail : {}", auditTrailDTO);
        AuditTrail auditTrail = auditTrailMapper.toEntity(auditTrailDTO);
        auditTrail = auditTrailRepository.save(auditTrail);
        auditTrailSearchRepository.index(auditTrail);
        return auditTrailMapper.toDto(auditTrail);
    }

    @Override
    public Optional<AuditTrailDTO> partialUpdate(AuditTrailDTO auditTrailDTO) {
        LOG.debug("Request to partially update AuditTrail : {}", auditTrailDTO);

        return auditTrailRepository
            .findById(auditTrailDTO.getId())
            .map(existingAuditTrail -> {
                auditTrailMapper.partialUpdate(existingAuditTrail, auditTrailDTO);

                return existingAuditTrail;
            })
            .map(auditTrailRepository::save)
            .map(savedAuditTrail -> {
                auditTrailSearchRepository.index(savedAuditTrail);
                return savedAuditTrail;
            })
            .map(auditTrailMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditTrailDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AuditTrails");
        return auditTrailRepository.findAll(pageable).map(auditTrailMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditTrailDTO> findOne(Long id) {
        LOG.debug("Request to get AuditTrail : {}", id);
        return auditTrailRepository.findById(id).map(auditTrailMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AuditTrail : {}", id);
        auditTrailRepository.deleteById(id);
        auditTrailSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditTrailDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of AuditTrails for query {}", query);
        return auditTrailSearchRepository.search(query, pageable).map(auditTrailMapper::toDto);
    }
}
