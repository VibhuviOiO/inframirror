package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class AuditTrailDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditTrailDTO.class);
        AuditTrailDTO auditTrailDTO1 = new AuditTrailDTO();
        auditTrailDTO1.setId(1L);
        AuditTrailDTO auditTrailDTO2 = new AuditTrailDTO();
        assertThat(auditTrailDTO1).isNotEqualTo(auditTrailDTO2);
        auditTrailDTO2.setId(auditTrailDTO1.getId());
        assertThat(auditTrailDTO1).isEqualTo(auditTrailDTO2);
        auditTrailDTO2.setId(2L);
        assertThat(auditTrailDTO1).isNotEqualTo(auditTrailDTO2);
        auditTrailDTO1.setId(null);
        assertThat(auditTrailDTO1).isNotEqualTo(auditTrailDTO2);
    }
}
