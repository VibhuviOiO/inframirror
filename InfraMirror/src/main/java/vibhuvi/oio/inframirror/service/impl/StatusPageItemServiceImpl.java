package vibhuvi.oio.inframirror.service.impl;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.StatusPageItem;
import vibhuvi.oio.inframirror.repository.StatusPageItemRepository;
import vibhuvi.oio.inframirror.service.StatusPageItemService;
import vibhuvi.oio.inframirror.service.dto.StatusPageItemDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusPageItemMapper;
/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.StatusPageItem}.
 */
@Service
@Transactional
public class StatusPageItemServiceImpl implements StatusPageItemService {
    private static final Logger LOG = LoggerFactory.getLogger(StatusPageItemServiceImpl.class);
    private final StatusPageItemRepository statusPageItemRepository;
    private final StatusPageItemMapper statusPageItemMapper;
    public StatusPageItemServiceImpl(
        StatusPageItemRepository statusPageItemRepository,
        StatusPageItemMapper statusPageItemMapper
    ) {
        this.statusPageItemRepository = statusPageItemRepository;
        this.statusPageItemMapper = statusPageItemMapper;
    }
    @Override
    public StatusPageItemDTO save(StatusPageItemDTO statusPageItemDTO) {
        LOG.debug("Request to save StatusPageItem : {}", statusPageItemDTO);
        StatusPageItem statusPageItem = statusPageItemMapper.toEntity(statusPageItemDTO);
        statusPageItem = statusPageItemRepository.save(statusPageItem);
        return statusPageItemMapper.toDto(statusPageItem);
    }
    @Override
    public StatusPageItemDTO update(StatusPageItemDTO statusPageItemDTO) {
        LOG.debug("Request to update StatusPageItem : {}", statusPageItemDTO);
        StatusPageItem statusPageItem = statusPageItemMapper.toEntity(statusPageItemDTO);
        statusPageItem = statusPageItemRepository.save(statusPageItem);
        return statusPageItemMapper.toDto(statusPageItem);
    }
    @Override
    public Optional<StatusPageItemDTO> partialUpdate(StatusPageItemDTO statusPageItemDTO) {
        LOG.debug("Request to partially update StatusPageItem : {}", statusPageItemDTO);
        return statusPageItemRepository

            .findById(statusPageItemDTO.getId())
            .map(existingStatusPageItem -> {
                statusPageItemMapper.partialUpdate(existingStatusPageItem, statusPageItemDTO);
                return existingStatusPageItem;
            })
            .map(statusPageItemRepository::save)
            .map(savedStatusPageItem -> {
                return savedStatusPageItem;
            })
            .map(statusPageItemMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<StatusPageItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all StatusPageItems");
        return statusPageItemRepository.findAll(pageable).map(statusPageItemMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<StatusPageItemDTO> findOne(Long id) {
        LOG.debug("Request to get StatusPageItem : {}", id);
        return statusPageItemRepository.findById(id).map(statusPageItemMapper::toDto);
    }
    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StatusPageItem : {}", id);
        statusPageItemRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<StatusPageItemDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of StatusPageItems for query {}", query);
        return statusPageItemRepository.findAll(pageable).map(statusPageItemMapper::toDto);
    }
}
