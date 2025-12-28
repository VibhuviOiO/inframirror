package vibhuvi.oio.inframirror.service.impl;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Branding;
import vibhuvi.oio.inframirror.repository.BrandingRepository;
import vibhuvi.oio.inframirror.service.BrandingService;
import vibhuvi.oio.inframirror.service.dto.BrandingDTO;
import vibhuvi.oio.inframirror.service.mapper.BrandingMapper;
/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Branding}.
 */
@Service
@Transactional
public class BrandingServiceImpl implements BrandingService {
    private static final Logger LOG = LoggerFactory.getLogger(BrandingServiceImpl.class);
    private final BrandingRepository brandingRepository;
    private final BrandingMapper brandingMapper;
    public BrandingServiceImpl(
        BrandingRepository brandingRepository,
        BrandingMapper brandingMapper
    ) {
        this.brandingRepository = brandingRepository;
        this.brandingMapper = brandingMapper;
    }
    @Override
    public BrandingDTO save(BrandingDTO brandingDTO) {
        LOG.debug("Request to save Branding : {}", brandingDTO);
        Branding branding = brandingMapper.toEntity(brandingDTO);
        branding = brandingRepository.save(branding);
        return brandingMapper.toDto(branding);
    }
    @Override
    public BrandingDTO update(BrandingDTO brandingDTO) {
        LOG.debug("Request to update Branding : {}", brandingDTO);
        Branding branding = brandingMapper.toEntity(brandingDTO);
        branding = brandingRepository.save(branding);
        return brandingMapper.toDto(branding);
    }
    @Override
    public Optional<BrandingDTO> partialUpdate(BrandingDTO brandingDTO) {
        LOG.debug("Request to partially update Branding : {}", brandingDTO);
        return brandingRepository

            .findById(brandingDTO.getId())
            .map(existingBranding -> {
                brandingMapper.partialUpdate(existingBranding, brandingDTO);
                return existingBranding;
            })
            .map(brandingRepository::save)
            .map(savedBranding -> {
                return savedBranding;
            })
            .map(brandingMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<BrandingDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Brandings");
        return brandingRepository.findAll(pageable).map(brandingMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<BrandingDTO> findOne(Long id) {
        LOG.debug("Request to get Branding : {}", id);
        return brandingRepository.findById(id).map(brandingMapper::toDto);
    }
    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Branding : {}", id);
        brandingRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<BrandingDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Brandings for query {}", query);
        return brandingRepository.findAll(pageable).map(brandingMapper::toDto);
    }
}
