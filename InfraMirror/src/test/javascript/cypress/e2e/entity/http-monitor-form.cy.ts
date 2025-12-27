import { entityCreateButtonSelector } from '../../support/entity';

describe('HttpMonitor Form E2E Test', () => {
  const httpMonitorPageUrl = '/http-monitor';
  const httpMonitorCreateUrl = '/http-monitor/new';

  beforeEach(() => {
    cy.visit('/');
    cy.login('admin', 'admin');
    cy.intercept('GET', '/api/http-monitors*').as('entitiesRequest');
    cy.visit(httpMonitorPageUrl);
    cy.wait('@entitiesRequest');
  });

  afterEach(() => {
    cy.authenticatedRequest({
      method: 'GET',
      url: '/api/http-monitors',
    }).then(({ body }) => {
      if (body && body.length > 0) {
        body.forEach(monitor => {
          if (monitor.name?.includes('E2E Test')) {
            cy.authenticatedRequest({
              method: 'DELETE',
              url: `/api/http-monitors/${monitor.id}`,
            });
          }
        });
      }
    });
  });

  it('should create HTTP Monitor with only mandatory fields', () => {
    cy.get(entityCreateButtonSelector).click();
    cy.url().should('include', httpMonitorCreateUrl);

    // Fill mandatory fields
    cy.get('[data-cy="name"]').type('E2E Test Monitor Mandatory');
    cy.get('[data-cy="method"]').select('GET');
    cy.get('[data-cy="type"]').select('HTTPS');
    cy.get('[data-cy="intervalSeconds"]').should('have.value', '60'); // Check default
    cy.get('[data-cy="timeoutSeconds"]').should('have.value', '30'); // Check default
    cy.get('[data-cy="retryCount"]').should('have.value', '2'); // Check default
    cy.get('[data-cy="retryDelaySeconds"]').should('have.value', '5'); // Check default

    // Save
    cy.get('[data-cy="entityCreateSaveButton"]').click();
    cy.wait('@entitiesRequest');
    cy.url().should('include', httpMonitorPageUrl);
  });

  it('should expand and fill optional Request Configuration section', () => {
    cy.get(entityCreateButtonSelector).click();

    // Fill mandatory fields
    cy.get('[data-cy="name"]').type('E2E Test Monitor Request');
    cy.get('[data-cy="method"]').select('POST');
    cy.get('[data-cy="type"]').select('HTTPS');

    // Expand Request Configuration
    cy.contains('Request Configuration').click();
    cy.get('[data-cy="headers"]').should('be.visible');
    cy.get('[data-cy="headers"]').type('{"Content-Type": "application/json"}');
    cy.get('[data-cy="body"]').type('{"test": "data"}');
    cy.get('[data-cy="expectedStatusCodes"]').type('200,201');

    // Save
    cy.get('[data-cy="entityCreateSaveButton"]').click();
    cy.wait('@entitiesRequest');
  });

  it('should expand and fill optional Alert Thresholds section', () => {
    cy.get(entityCreateButtonSelector).click();

    // Fill mandatory fields
    cy.get('[data-cy="name"]').type('E2E Test Monitor Alerts');
    cy.get('[data-cy="method"]').select('GET');
    cy.get('[data-cy="type"]').select('HTTPS');

    // Expand Alert Thresholds
    cy.contains('Alert Thresholds').click();
    cy.get('[data-cy="responseTimeWarningMs"]').should('be.visible');
    cy.get('[data-cy="responseTimeWarningMs"]').type('500');
    cy.get('[data-cy="responseTimeCriticalMs"]').type('1000');
    cy.get('[data-cy="uptimeWarningPercent"]').type('95');
    cy.get('[data-cy="uptimeCriticalPercent"]').type('90');

    // Save
    cy.get('[data-cy="entityCreateSaveButton"]').click();
    cy.wait('@entitiesRequest');
  });

  it('should expand and fill optional Advanced Options section', () => {
    cy.get(entityCreateButtonSelector).click();

    // Fill mandatory fields
    cy.get('[data-cy="name"]').type('E2E Test Monitor Advanced');
    cy.get('[data-cy="method"]').select('GET');
    cy.get('[data-cy="type"]').select('HTTPS');

    // Expand Advanced Options
    cy.contains('Advanced Options').click();
    cy.get('[data-cy="checkSslCertificate"]').should('be.visible');
    cy.get('[data-cy="checkSslCertificate"]').check();
    cy.get('[data-cy="certificateExpiryDays"]').type('30');
    cy.get('[data-cy="tags"]').type('production, critical');

    // Save
    cy.get('[data-cy="entityCreateSaveButton"]').click();
    cy.wait('@entitiesRequest');
  });

  it('should validate mandatory fields', () => {
    cy.get(entityCreateButtonSelector).click();

    // Try to save without filling mandatory fields
    cy.get('[data-cy="entityCreateSaveButton"]').click();

    // Should show validation errors
    cy.get('[data-cy="name"]').parent().should('contain', 'required');
  });

  it('should collapse sections by default', () => {
    cy.get(entityCreateButtonSelector).click();

    // Optional sections should be collapsed
    cy.get('[data-cy="headers"]').should('not.be.visible');
    cy.get('[data-cy="responseTimeWarningMs"]').should('not.be.visible');
    cy.get('[data-cy="checkSslCertificate"]').should('not.be.visible');
  });
});
