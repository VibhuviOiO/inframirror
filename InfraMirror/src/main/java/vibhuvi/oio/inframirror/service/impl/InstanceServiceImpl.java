package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.InstanceService;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Instance}.
 */
@Service
@Transactional
public class InstanceServiceImpl implements InstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceMapper instanceMapper;

    public InstanceServiceImpl(InstanceRepository instanceRepository, InstanceMapper instanceMapper) {
        this.instanceRepository = instanceRepository;
        this.instanceMapper = instanceMapper;
    }

    @Override
    public InstanceDTO save(InstanceDTO instanceDTO) {
        LOG.debug("Request to save Instance : {}", instanceDTO);
        Instance instance = instanceMapper.toEntity(instanceDTO);
        instance = instanceRepository.save(instance);
        return instanceMapper.toDto(instance);
    }

    @Override
    public InstanceDTO update(InstanceDTO instanceDTO) {
        LOG.debug("Request to update Instance : {}", instanceDTO);
        Instance instance = instanceMapper.toEntity(instanceDTO);
        instance = instanceRepository.save(instance);
        return instanceMapper.toDto(instance);
    }

    @Override
    public Optional<InstanceDTO> partialUpdate(InstanceDTO instanceDTO) {
        LOG.debug("Request to partially update Instance : {}", instanceDTO);

        return instanceRepository
            .findById(instanceDTO.getId())
            .map(existingInstance -> {
                instanceMapper.partialUpdate(existingInstance, instanceDTO);

                return existingInstance;
            })
            .map(instanceRepository::save)
            .map(instanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InstanceDTO> findOne(Long id) {
        LOG.debug("Request to get Instance : {}", id);
        return instanceRepository.findById(id).map(instanceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Instance : {}", id);
        instanceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstanceDTO> search(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to search Instances for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return instanceRepository.findAll(pageable).map(instanceMapper::toDto);
        }

        String searchTerm = FullTextSearchUtil.sanitizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return instanceRepository.searchFullText(searchTerm, limitedPageable).map(instanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstanceDTO> searchPrefix(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to prefix search Instances for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return instanceRepository.searchPrefix(normalizedQuery, limitedPageable).map(instanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstanceDTO> searchFuzzy(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to fuzzy search Instances for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return instanceRepository.searchFuzzy(normalizedQuery, limitedPageable).map(instanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstanceSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to search Instances with highlight for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return instanceRepository
            .searchWithHighlight(normalizedQuery, limitedPageable)
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String name = (String) row[1];
                String hostname = (String) row[2];
                String description = (String) row[3];
                String privateIpAddress = (String) row[4];
                String publicIpAddress = (String) row[5];
                Float rank = ((Number) row[6]).floatValue();
                String highlight = (String) row[7];
                return new InstanceSearchResultDTO(id, name, hostname, description, privateIpAddress, publicIpAddress, rank, highlight);
            });
    }

    @Override
    public InstanceDTO findOrCreate(InstanceDTO instanceDTO) {
        LOG.debug("Request to find or create Instance by hostname: {}", instanceDTO.getHostname());
        
        Optional<Instance> existing = instanceRepository.findByHostname(instanceDTO.getHostname());
        
        if (existing.isPresent()) {
            Instance instance = existing.get();
            instanceDTO.setAgent(null);
            instanceDTO.setDatacenter(null);
            instanceMapper.partialUpdate(instance, instanceDTO);
            instance = instanceRepository.save(instance);
            LOG.debug("Updated existing instance: {}", instance.getId());
            return instanceMapper.toDto(instance);
        } else {
            Instance instance = instanceMapper.toEntity(instanceDTO);
            instance = instanceRepository.save(instance);
            LOG.debug("Created new instance: {}", instance.getId());
            return instanceMapper.toDto(instance);
        }
    }
}
