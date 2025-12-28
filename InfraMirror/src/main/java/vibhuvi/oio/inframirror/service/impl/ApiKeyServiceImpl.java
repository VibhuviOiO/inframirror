package vibhuvi.oio.inframirror.service.impl;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import vibhuvi.oio.inframirror.domain.ApiKey;
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.repository.ApiKeyRepository;
import vibhuvi.oio.inframirror.repository.AuditTrailRepository;
import vibhuvi.oio.inframirror.service.ApiKeyService;
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;
import vibhuvi.oio.inframirror.service.dto.ApiKeySearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.ApiKeyMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.ApiKey}.
 */
@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {
    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyServiceImpl.class);
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;
    private final AuditTrailRepository auditTrailRepository;
    public ApiKeyServiceImpl(
        ApiKeyRepository apiKeyRepository,
        ApiKeyMapper apiKeyMapper,
        AuditTrailRepository auditTrailRepository
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyMapper = apiKeyMapper;
        this.auditTrailRepository = auditTrailRepository;
    }
    private void logAudit(String action, Long entityId, String oldValue, String newValue) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HttpServletRequest request = getCurrentRequest();
        AuditTrail trail = new AuditTrail();
        trail.setAction(action);
        trail.setEntityName("ApiKey");
        trail.setEntityId(entityId);
        trail.setOldValue(oldValue);
        trail.setNewValue(newValue);
        trail.setTimestamp(Instant.now());
        trail.setPerformedBy(username);
        if (request != null) {
            trail.setIpAddress(getClientIp(request));
            trail.setUserAgent(request.getHeader("User-Agent"));
        }
        auditTrailRepository.save(trail);
    }
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
    private String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return "inframirror_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private String hashApiKey(String plainTextKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainTextKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    @Override
    public ApiKeyDTO save(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to save ApiKey : {}", apiKeyDTO);
        String plainTextKey = generateApiKey();
        String lookupHash = hashApiKey(plainTextKey);
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiKey apiKey = apiKeyMapper.toEntity(apiKeyDTO);
        apiKey.setKeyHash(lookupHash);
        apiKey.setActive(true);
        apiKey.setCreatedBy(currentUser);
        apiKey.setCreatedDate(Instant.now());
        apiKey = apiKeyRepository.save(apiKey);
        logAudit("CREATE", apiKey.getId(), null, apiKey.getName());
        ApiKeyDTO result = apiKeyMapper.toDto(apiKey);
        result.setPlainTextKey(plainTextKey);
        return result;
    }
    @Override
    public ApiKeyDTO update(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to update ApiKey : {}", apiKeyDTO);
        ApiKey apiKey = apiKeyMapper.toEntity(apiKeyDTO);
        apiKey = apiKeyRepository.save(apiKey);
        return apiKeyMapper.toDto(apiKey);
    }
    @Override
    public Optional<ApiKeyDTO> partialUpdate(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to partially update ApiKey : {}", apiKeyDTO);
        return apiKeyRepository
            .findById(apiKeyDTO.getId())
            .map(existingApiKey -> {
                apiKeyMapper.partialUpdate(existingApiKey, apiKeyDTO);
                return existingApiKey;
            })
            .map(apiKeyRepository::save)
            .map(savedApiKey -> {
                return savedApiKey;
            })
            .map(apiKeyMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ApiKeys");
        return apiKeyRepository.findAll(pageable).map(apiKeyMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<ApiKeyDTO> findOne(Long id) {
        LOG.debug("Request to get ApiKey : {}", id);
        return apiKeyRepository.findById(id).map(apiKeyMapper::toDto);
    }
    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ApiKey : {}", id);
        apiKeyRepository.findById(id).ifPresent(apiKey -> {
            logAudit("DELETE", id, apiKey.getName(), null);
        });
        apiKeyRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeyDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ApiKeys for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return apiKeyRepository.findAll(pageable).map(apiKeyMapper::toDto);
        }
        String searchTerm = FullTextSearchUtil.sanitizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return apiKeyRepository.searchFullText(searchTerm, limitedPageable).map(apiKeyMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeyDTO> searchPrefix(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }
        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return apiKeyRepository.searchPrefix(normalizedQuery, limitedPageable).map(apiKeyMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeyDTO> searchFuzzy(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }
        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return apiKeyRepository.searchFuzzy(normalizedQuery, limitedPageable).map(apiKeyMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeySearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }
        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);
        return apiKeyRepository.searchWithHighlight(normalizedQuery, limitedPageable)
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String name = (String) row[1];
                String description = (String) row[2];
                Float rank = ((Number) row[3]).floatValue();
                String highlight = (String) row[4];
                return new ApiKeySearchResultDTO(id, name, description, rank, highlight);
            });
    }
    public void deactivate(Long id) {
        LOG.debug("Request to deactivate ApiKey : {}", id);
        apiKeyRepository
            .findById(id)
            .ifPresent(apiKey -> {
                apiKey.setActive(false);
                apiKeyRepository.save(apiKey);
                logAudit("DEACTIVATE", id, "active", "inactive");
            });
    }
}
