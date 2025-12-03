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
import vibhuvi.oio.inframirror.repository.search.InstanceSearchRepository;
import vibhuvi.oio.inframirror.service.InstanceService;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
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

    private final InstanceSearchRepository instanceSearchRepository;

    public InstanceServiceImpl(
        InstanceRepository instanceRepository,
        InstanceMapper instanceMapper,
        InstanceSearchRepository instanceSearchRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceMapper = instanceMapper;
        this.instanceSearchRepository = instanceSearchRepository;
    }

    @Override
    public InstanceDTO save(InstanceDTO instanceDTO) {
        LOG.debug("Request to save Instance : {}", instanceDTO);
        Instance instance = instanceMapper.toEntity(instanceDTO);
        instance = instanceRepository.save(instance);
        instanceSearchRepository.index(instance);
        return instanceMapper.toDto(instance);
    }

    @Override
    public InstanceDTO update(InstanceDTO instanceDTO) {
        LOG.debug("Request to update Instance : {}", instanceDTO);
        Instance instance = instanceMapper.toEntity(instanceDTO);
        instance = instanceRepository.save(instance);
        instanceSearchRepository.index(instance);
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
            .map(savedInstance -> {
                instanceSearchRepository.index(savedInstance);
                return savedInstance;
            })
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
        instanceSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstanceDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Instances for query {}", query);
        return instanceSearchRepository.search(query, pageable).map(instanceMapper::toDto);
    }
}
