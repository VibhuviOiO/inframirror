package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.AuditTrailAsserts.*;
import static vibhuvi.oio.inframirror.domain.AuditTrailTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditTrailMapperTest {

    private AuditTrailMapper auditTrailMapper;

    @BeforeEach
    void setUp() {
        auditTrailMapper = new AuditTrailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditTrailSample1();
        var actual = auditTrailMapper.toEntity(auditTrailMapper.toDto(expected));
        assertAuditTrailAllPropertiesEquals(expected, actual);
    }
}
