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

describe('Service e2e test', () => {
  const servicePageUrl = '/service';
  const servicePageUrlPattern = new RegExp('/service(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const serviceSample = {
    name: 'furthermore aha',
    serviceType: 'amidst reasoning catch',
    environment: 'despite',
    intervalSeconds: 1932,
    timeoutMs: 30187,
    retryCount: 15046,
  };

  let service;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/services+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/services').as('postEntityRequest');
    cy.intercept('DELETE', '/api/services/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (service) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/services/${service.id}`,
      }).then(() => {
        service = undefined;
      });
    }
  });

  it('Services menu should load Services page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('service');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Service').should('exist');
    cy.url().should('match', servicePageUrlPattern);
  });

  describe('Service page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(servicePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Service page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/service/new$'));
        cy.getEntityCreateUpdateHeading('Service');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', servicePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/services',
          body: serviceSample,
        }).then(({ body }) => {
          service = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/services+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/services?page=0&size=20>; rel="last",<http://localhost/api/services?page=0&size=20>; rel="first"',
              },
              body: [service],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(servicePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Service page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('service');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', servicePageUrlPattern);
      });

      it('edit button click should load edit Service page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Service');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', servicePageUrlPattern);
      });

      it('edit button click should load edit Service page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Service');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', servicePageUrlPattern);
      });

      it('last delete button click should delete instance of Service', () => {
        cy.intercept('GET', '/api/services/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('service').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', servicePageUrlPattern);

        service = undefined;
      });
    });
  });

  describe('new Service page', () => {
    beforeEach(() => {
      cy.visit(`${servicePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Service');
    });

    it('should create an instance of Service', () => {
      cy.get(`[data-cy="name"]`).type('for ew');
      cy.get(`[data-cy="name"]`).should('have.value', 'for ew');

      cy.get(`[data-cy="description"]`).type('boohoo direct yippee');
      cy.get(`[data-cy="description"]`).should('have.value', 'boohoo direct yippee');

      cy.get(`[data-cy="serviceType"]`).type('speedily unnecessarily into');
      cy.get(`[data-cy="serviceType"]`).should('have.value', 'speedily unnecessarily into');

      cy.get(`[data-cy="environment"]`).type('modulo');
      cy.get(`[data-cy="environment"]`).should('have.value', 'modulo');

      cy.get(`[data-cy="monitoringEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="monitoringEnabled"]`).click();
      cy.get(`[data-cy="monitoringEnabled"]`).should('be.checked');

      cy.get(`[data-cy="clusterMonitoringEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="clusterMonitoringEnabled"]`).click();
      cy.get(`[data-cy="clusterMonitoringEnabled"]`).should('be.checked');

      cy.get(`[data-cy="intervalSeconds"]`).type('235');
      cy.get(`[data-cy="intervalSeconds"]`).should('have.value', '235');

      cy.get(`[data-cy="timeoutMs"]`).type('384');
      cy.get(`[data-cy="timeoutMs"]`).should('have.value', '384');

      cy.get(`[data-cy="retryCount"]`).type('21991');
      cy.get(`[data-cy="retryCount"]`).should('have.value', '21991');

      cy.get(`[data-cy="latencyWarningMs"]`).type('4832');
      cy.get(`[data-cy="latencyWarningMs"]`).should('have.value', '4832');

      cy.get(`[data-cy="latencyCriticalMs"]`).type('20448');
      cy.get(`[data-cy="latencyCriticalMs"]`).should('have.value', '20448');

      cy.get(`[data-cy="advancedConfig"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="advancedConfig"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T21:08');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T21:08');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-04T22:26');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-04T22:26');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        service = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', servicePageUrlPattern);
    });
  });
});
