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
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.StatusPageService;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;
import vibhuvi.oio.inframirror.service.dto.StatusPageSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusPageMapper;

@Service
@Transactional
public class StatusPageServiceImpl implements StatusPageService {
    private static final Logger LOG = LoggerFactory.getLogger(StatusPageServiceImpl.class);
    private final StatusPageRepository statusPageRepository;
    private final StatusPageMapper statusPageMapper;
    
    public StatusPageServiceImpl(StatusPageRepository statusPageRepository, StatusPageMapper statusPageMapper) {
        this.statusPageRepository = statusPageRepository;
        this.statusPageMapper = statusPageMapper;
    }
    
    @Override
    public StatusPageDTO save(StatusPageDTO statusPageDTO) {
        LOG.debug("Request to save StatusPage : {}", statusPageDTO);
        StatusPage statusPage = statusPageMapper.toEntity(statusPageDTO);
        statusPage = statusPageRepository.save(statusPage);
        return statusPageMapper.toDto(statusPage);
    }
    
    @Override
    public StatusPageDTO update(StatusPageDTO statusPageDTO) {
        LOG.debug("Request to update StatusPage : {}", statusPageDTO);
        StatusPage statusPage = statusPageMapper.toEntity(statusPageDTO);
        statusPage = statusPageRepository.save(statusPage);
        return statusPageMapper.toDto(statusPage);
    }
    
    @Override
    public Optional<StatusPageDTO> partialUpdate(StatusPageDTO statusPageDTO) {
        LOG.debug("Request to partially update StatusPage : {}", statusPageDTO);
        return statusPageRepository.findById(statusPageDTO.getId())
            .map(existingStatusPage -> {
                statusPageMapper.partialUpdate(existingStatusPage, statusPageDTO);
                return existingStatusPage;
            })
            .map(statusPageRepository::save)
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
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StatusPageDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of StatusPages for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return statusPageRepository.findAll(pageable).map(statusPageMapper::toDto);
        }
        String searchTerm = FullTextSearchUtil.sanitizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return statusPageRepository.searchFullText(searchTerm, limitedPageable).map(statusPageMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StatusPageDTO> searchPrefix(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }
        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return statusPageRepository.searchPrefix(normalizedQuery, limitedPageable).map(statusPageMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StatusPageDTO> searchFuzzy(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }
        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return statusPageRepository.searchFuzzy(normalizedQuery, limitedPageable).map(statusPageMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StatusPageSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }
        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return statusPageRepository.searchWithHighlight(normalizedQuery, limitedPageable)
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String name = (String) row[1];
                String slug = (String) row[2];
                String description = (String) row[3];
                Float rank = ((Number) row[4]).floatValue();
                String highlight = (String) row[5];
                return new StatusPageSearchResultDTO(id, name, slug, description, rank, highlight);
            });
    }
}
