import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('AuditTrail e2e test', () => {
  const auditTrailPageUrl = '/audit-trail';
  const auditTrailPageUrlPattern = new RegExp('/audit-trail(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const auditTrailSample = { action: 'freely', entityName: 'noisily at edge', entityId: 31923, timestamp: '2025-12-04T12:39:07.378Z' };

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
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(auditTrailPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AuditTrail page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/audit-trail/new$'));
        cy.getEntityCreateUpdateHeading('AuditTrail');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditTrailPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/audit-trails',
          body: auditTrailSample,
        }).then(({ body }) => {
          auditTrail = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/audit-trails+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/audit-trails?page=0&size=20>; rel="last",<http://localhost/api/audit-trails?page=0&size=20>; rel="first"',
              },
              body: [auditTrail],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(auditTrailPageUrl);

        cy.wait('@entitiesRequestInternal');
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

      it('edit button click should load edit AuditTrail page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AuditTrail');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditTrailPageUrlPattern);
      });

      it('edit button click should load edit AuditTrail page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AuditTrail');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditTrailPageUrlPattern);
      });

      it('last delete button click should delete instance of AuditTrail', () => {
        cy.intercept('GET', '/api/audit-trails/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('auditTrail').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', auditTrailPageUrlPattern);

        auditTrail = undefined;
      });
    });
  });

  describe('new AuditTrail page', () => {
    beforeEach(() => {
      cy.visit(`${auditTrailPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AuditTrail');
    });

    it('should create an instance of AuditTrail', () => {
      cy.get(`[data-cy="action"]`).type('moralise');
      cy.get(`[data-cy="action"]`).should('have.value', 'moralise');

      cy.get(`[data-cy="entityName"]`).type('as boldly');
      cy.get(`[data-cy="entityName"]`).should('have.value', 'as boldly');

      cy.get(`[data-cy="entityId"]`).type('6394');
      cy.get(`[data-cy="entityId"]`).should('have.value', '6394');

      cy.get(`[data-cy="oldValue"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="oldValue"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="newValue"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="newValue"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="timestamp"]`).type('2025-12-04T19:25');
      cy.get(`[data-cy="timestamp"]`).blur();
      cy.get(`[data-cy="timestamp"]`).should('have.value', '2025-12-04T19:25');

      cy.get(`[data-cy="ipAddress"]`).type('bank outside underplay');
      cy.get(`[data-cy="ipAddress"]`).should('have.value', 'bank outside underplay');

      cy.get(`[data-cy="userAgent"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="userAgent"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        auditTrail = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', auditTrailPageUrlPattern);
    });
  });
});
