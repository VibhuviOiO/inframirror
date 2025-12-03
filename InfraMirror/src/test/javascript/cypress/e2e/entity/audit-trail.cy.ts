import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('AuditTrail e2e test', () => {
  const auditTrailPageUrl = '/audit-trail';
  const auditTrailPageUrlPattern = new RegExp('/audit-trail(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';

  let auditTrail;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/audit-trails+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/audit-trails').as('postEntityRequest');
    cy.intercept('DELETE', '/api/audit-trails/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (auditTrail) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/audit-trails/${auditTrail.id}`,
      }).then(() => {
        auditTrail = undefined;
      });
    }
  });

  it('AuditTrails menu should load AuditTrails page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-trail');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AuditTrail').should('exist');
    cy.url().should('match', auditTrailPageUrlPattern);
  });

  describe('AuditTrail page', () => {
    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(auditTrailPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details AuditTrail page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('auditTrail');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditTrailPageUrlPattern);
      });
    });
  });
});
