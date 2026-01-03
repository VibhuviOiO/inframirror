package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.service.InstanceService;
import vibhuvi.oio.inframirror.service.base.AbstractCrudService;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceMapper;

/**
 * Service for managing instances.
 * Provides CRUD operations following clean architecture principles.
 */
@Service
@Transactional
public class InstanceServiceImpl extends AbstractCrudService<InstanceDTO, Instance> implements InstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceServiceImpl.class);
    private final InstanceRepository instanceRepository;
    private final InstanceMapper instanceMapper;

    public InstanceServiceImpl(InstanceRepository instanceRepository, InstanceMapper instanceMapper) {
        this.instanceRepository = instanceRepository;
        this.instanceMapper = instanceMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected JpaRepository<Instance, Long> getRepository() {
        return instanceRepository;
    }

    @Override
    protected InstanceMapper getMapper() {
        return instanceMapper;
    }

    @Override
    protected String getEntityName() {
        return "instance";
    }

    @Override
    public Optional<InstanceDTO> partialUpdate(InstanceDTO instanceDTO) {
        return super.partialUpdate(instanceDTO, instanceDTO.getId());
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
