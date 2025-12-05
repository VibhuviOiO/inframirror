package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.BrandingAsserts.*;
import static vibhuvi.oio.inframirror.domain.BrandingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BrandingMapperTest {

    private BrandingMapper brandingMapper;

    @BeforeEach
    void setUp() {
        brandingMapper = new BrandingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBrandingSample1();
        var actual = brandingMapper.toEntity(brandingMapper.toDto(expected));
        assertBrandingAllPropertiesEquals(expected, actual);
    }
}
