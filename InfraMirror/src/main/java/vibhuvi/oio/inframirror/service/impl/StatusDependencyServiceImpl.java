package vibhuvi.oio.inframirror.service.impl;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.StatusDependency;
import vibhuvi.oio.inframirror.domain.StatusPageItem;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.StatusDependencyRepository;
import vibhuvi.oio.inframirror.repository.StatusPageItemRepository;
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;
import vibhuvi.oio.inframirror.repository.HttpHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.service.StatusDependencyService;
import vibhuvi.oio.inframirror.service.dto.StatusDependencyDTO;
import vibhuvi.oio.inframirror.service.dto.DependencyTreeDTO;
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
    private final StatusPageItemRepository statusPageItemRepository;
    private final HttpMonitorRepository httpMonitorRepository;
    private final HttpHeartbeatRepository httpHeartbeatRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final InstanceRepository instanceRepository;

    public StatusDependencyServiceImpl(
        StatusDependencyRepository statusDependencyRepository,
        StatusDependencyMapper statusDependencyMapper,
        StatusPageItemRepository statusPageItemRepository,
        HttpMonitorRepository httpMonitorRepository,
        HttpHeartbeatRepository httpHeartbeatRepository,
        MonitoredServiceRepository monitoredServiceRepository,
        InstanceRepository instanceRepository
    ) {
        this.statusDependencyRepository = statusDependencyRepository;
        this.statusDependencyMapper = statusDependencyMapper;
        this.statusPageItemRepository = statusPageItemRepository;
        this.httpMonitorRepository = httpMonitorRepository;
        this.httpHeartbeatRepository = httpHeartbeatRepository;
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.instanceRepository = instanceRepository;
    }
    @Override
    public StatusDependencyDTO save(StatusDependencyDTO statusDependencyDTO) {
        LOG.debug("Request to save StatusDependency : {}", statusDependencyDTO);
        StatusDependency statusDependency = statusDependencyMapper.toEntity(statusDependencyDTO);
        statusDependency = statusDependencyRepository.save(statusDependency);
        return statusDependencyMapper.toDto(statusDependency);
    }
    @Override
    public StatusDependencyDTO update(StatusDependencyDTO statusDependencyDTO) {
        LOG.debug("Request to update StatusDependency : {}", statusDependencyDTO);
        StatusDependency statusDependency = statusDependencyMapper.toEntity(statusDependencyDTO);
        statusDependency = statusDependencyRepository.save(statusDependency);
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
    }
    @Override
    @Transactional(readOnly = true)
    public Page<StatusDependencyDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of StatusDependencies for query {}", query);
        return statusDependencyRepository.findAll(pageable).map(statusDependencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DependencyTreeDTO> getDependencyTree(Long statusPageId) {
        LOG.debug("Request to get dependency tree for status page : {}", statusPageId);
        List<StatusPageItem> items = statusPageItemRepository.findByStatusPageIdOrderByDisplayOrderAsc(statusPageId);
        LOG.debug("Found {} status page items", items.size());
        List<DependencyTreeDTO> tree = new ArrayList<>();

        for (StatusPageItem item : items) {
            LOG.debug("Processing item type: {}, id: {}", item.getItemType(), item.getItemId());
            DependencyTreeDTO node = buildTreeNode(item.getItemType(), item.getItemId());
            if (node != null) {
                LOG.debug("Built node: {}", node.getName());
                tree.add(node);
            } else {
                LOG.warn("Failed to build node for type: {}, id: {}", item.getItemType(), item.getItemId());
            }
        }

        LOG.debug("Returning tree with {} root nodes", tree.size());
        return tree;
    }

    private DependencyTreeDTO buildTreeNode(String type, Long itemId) {
        LOG.debug("Building tree node for type: {}, id: {}", type, itemId);
        DependencyTreeDTO node = new DependencyTreeDTO();
        node.setId(type + "-" + itemId);
        node.setType(type);
        node.setItemId(itemId);

        if ("HTTP_MONITOR".equals(type) || "HTTP".equals(type)) {
            HttpMonitor monitor = httpMonitorRepository.findById(itemId).orElse(null);
            if (monitor == null) {
                LOG.warn("HTTP Monitor not found: {}", itemId);
                return null;
            }

            node.setName(monitor.getName());
            LOG.debug("Found HTTP monitor: {}", monitor.getName());

            HttpHeartbeat heartbeat = httpHeartbeatRepository
                .findFirstByMonitorIdOrderByExecutedAtDesc(monitor.getId())
                .orElse(null);

            if (heartbeat == null || !Boolean.TRUE.equals(heartbeat.getSuccess())) {
                node.setStatus("DOWN");
                if (heartbeat != null) {
                    node.setErrorMessage(heartbeat.getErrorMessage());
                    node.setLastChecked(heartbeat.getExecutedAt() != null ? heartbeat.getExecutedAt().toString() : null);
                }
            } else if (heartbeat.getResponseTimeMs() != null &&
                       monitor.getPerformanceBudgetMs() != null &&
                       heartbeat.getResponseTimeMs() > monitor.getPerformanceBudgetMs()) {
                node.setStatus("DEGRADED");
                node.setResponseTimeMs(heartbeat.getResponseTimeMs());
                node.setLastChecked(heartbeat.getExecutedAt() != null ? heartbeat.getExecutedAt().toString() : null);
            } else {
                node.setStatus("UP");
                node.setResponseTimeMs(heartbeat.getResponseTimeMs());
                node.setLastChecked(heartbeat.getExecutedAt() != null ? heartbeat.getExecutedAt().toString() : null);
            }
        } else if ("SERVICE".equals(type)) {
            MonitoredService service = monitoredServiceRepository.findById(itemId).orElse(null);
            if (service == null) {
                LOG.warn("Service not found: {}", itemId);
                return null;
            }
            node.setName(service.getName());
            node.setStatus("UNKNOWN");
            LOG.debug("Found service: {}", service.getName());
        } else if ("INSTANCE".equals(type)) {
            Instance instance = instanceRepository.findById(itemId).orElse(null);
            if (instance == null) {
                LOG.warn("Instance not found: {}", itemId);
                return null;
            }
            node.setName(instance.getName());
            node.setStatus("UNKNOWN");
            LOG.debug("Found instance: {}", instance.getName());
        }

        List<StatusDependency> dependencies = statusDependencyRepository
            .findByParentTypeAndParentId(type, itemId);
        LOG.debug("Found {} dependencies for {} {}", dependencies.size(), type, itemId);

        List<DependencyTreeDTO> children = new ArrayList<>();
        for (StatusDependency dep : dependencies) {
            DependencyTreeDTO child = buildTreeNode(dep.getChildType(), dep.getChildId());
            if (child != null) {
                children.add(child);
            }
        }
        node.setChildren(children);

        return node;
    }
}
