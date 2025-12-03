package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.DatacenterAsserts.*;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatacenterMapperTest {

    private DatacenterMapper datacenterMapper;

    @BeforeEach
    void setUp() {
        datacenterMapper = new DatacenterMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDatacenterSample1();
        var actual = datacenterMapper.toEntity(datacenterMapper.toDto(expected));
        assertDatacenterAllPropertiesEquals(expected, actual);
    }
}
