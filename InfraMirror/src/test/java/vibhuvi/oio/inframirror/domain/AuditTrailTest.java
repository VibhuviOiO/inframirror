package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AuditTrailTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class AuditTrailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditTrail.class);
        AuditTrail auditTrail1 = getAuditTrailSample1();
        AuditTrail auditTrail2 = new AuditTrail();
        assertThat(auditTrail1).isNotEqualTo(auditTrail2);

        auditTrail2.setId(auditTrail1.getId());
        assertThat(auditTrail1).isEqualTo(auditTrail2);

        auditTrail2 = getAuditTrailSample2();
        assertThat(auditTrail1).isNotEqualTo(auditTrail2);
    }
}
