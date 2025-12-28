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
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.HttpMonitorService;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorSearchResultDTO;
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

    public HttpMonitorServiceImpl(HttpMonitorRepository httpMonitorRepository, HttpMonitorMapper httpMonitorMapper) {
        this.httpMonitorRepository = httpMonitorRepository;
        this.httpMonitorMapper = httpMonitorMapper;
    }

    @Override
    public HttpMonitorDTO save(HttpMonitorDTO httpMonitorDTO) {
        LOG.debug("Request to save HttpMonitor : {}", httpMonitorDTO);
        HttpMonitor httpMonitor = httpMonitorMapper.toEntity(httpMonitorDTO);
        httpMonitor = httpMonitorRepository.save(httpMonitor);
        return httpMonitorMapper.toDto(httpMonitor);
    }

    @Override
    public HttpMonitorDTO update(HttpMonitorDTO httpMonitorDTO) {
        LOG.debug("Request to update HttpMonitor : {}", httpMonitorDTO);
        HttpMonitor httpMonitor = httpMonitorMapper.toEntity(httpMonitorDTO);
        httpMonitor = httpMonitorRepository.save(httpMonitor);
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
            .map(httpMonitorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HttpMonitorDTO> findOne(Long id) {
        LOG.debug("Request to get HttpMonitor : {}", id);
        return httpMonitorRepository.findById(id).map(httpMonitorMapper::toDto);
    }

    @Override
    public Optional<HttpMonitorDTO> findByName(String name) {
        LOG.debug("Request to get HttpMonitor by name : {}", name);
        return httpMonitorRepository.findByName(name).map(httpMonitorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete HttpMonitor : {}", id);
        httpMonitorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HttpMonitorDTO> search(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to search for a page of HttpMonitors for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return httpMonitorRepository.findAll(pageable).map(httpMonitorMapper::toDto);
        }

        String searchTerm = FullTextSearchUtil.sanitizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return httpMonitorRepository.searchFullText(searchTerm, limitedPageable).map(httpMonitorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HttpMonitorDTO> searchPrefix(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to prefix search HttpMonitors for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return httpMonitorRepository.searchPrefix(normalizedQuery, limitedPageable).map(httpMonitorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HttpMonitorDTO> searchFuzzy(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to fuzzy search HttpMonitors for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return httpMonitorRepository.searchFuzzy(normalizedQuery, limitedPageable).map(httpMonitorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HttpMonitorSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to search HttpMonitors with highlight for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return httpMonitorRepository.searchWithHighlight(normalizedQuery, limitedPageable).map(row -> {
            Long id = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String url = (String) row[2];
            String description = (String) row[3];
            Float rank = ((Number) row[4]).floatValue();
            String highlight = (String) row[5];
            return new HttpMonitorSearchResultDTO(id, name, url, description, rank, highlight);
        });
    }
}
