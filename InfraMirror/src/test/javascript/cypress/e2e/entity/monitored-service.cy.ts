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

describe('MonitoredService e2e test', () => {
  const monitoredServicePageUrl = '/monitored-service';
  const monitoredServicePageUrlPattern = new RegExp('/monitored-service(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const monitoredServiceSample = {
    name: 'trustworthy ew heating',
    serviceType: 'probe pleasant',
    environment: 'state',
    intervalSeconds: 19864,
    timeoutMs: 1155,
    retryCount: 20356,
  };

  let monitoredService;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/monitored-services+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/monitored-services').as('postEntityRequest');
    cy.intercept('DELETE', '/api/monitored-services/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (monitoredService) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/monitored-services/${monitoredService.id}`,
      }).then(() => {
        monitoredService = undefined;
      });
    }
  });

  it('MonitoredServices menu should load MonitoredServices page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('monitored-service');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MonitoredService').should('exist');
    cy.url().should('match', monitoredServicePageUrlPattern);
  });

  describe('MonitoredService page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(monitoredServicePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MonitoredService page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/monitored-service/new$'));
        cy.getEntityCreateUpdateHeading('MonitoredService');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monitoredServicePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/monitored-services',
          body: monitoredServiceSample,
        }).then(({ body }) => {
          monitoredService = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/monitored-services+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/monitored-services?page=0&size=20>; rel="last",<http://localhost/api/monitored-services?page=0&size=20>; rel="first"',
              },
              body: [monitoredService],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(monitoredServicePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MonitoredService page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('monitoredService');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monitoredServicePageUrlPattern);
      });

      it('edit button click should load edit MonitoredService page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MonitoredService');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monitoredServicePageUrlPattern);
      });

      it('edit button click should load edit MonitoredService page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MonitoredService');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monitoredServicePageUrlPattern);
      });

      it('last delete button click should delete instance of MonitoredService', () => {
        cy.intercept('GET', '/api/monitored-services/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('monitoredService').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monitoredServicePageUrlPattern);

        monitoredService = undefined;
      });
    });
  });

  describe('new MonitoredService page', () => {
    beforeEach(() => {
      cy.visit(`${monitoredServicePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MonitoredService');
    });

    it('should create an instance of MonitoredService', () => {
      cy.get(`[data-cy="name"]`).type('gaseous but bludgeon');
      cy.get(`[data-cy="name"]`).should('have.value', 'gaseous but bludgeon');

      cy.get(`[data-cy="description"]`).type('yuck attest');
      cy.get(`[data-cy="description"]`).should('have.value', 'yuck attest');

      cy.get(`[data-cy="serviceType"]`).type('fooey');
      cy.get(`[data-cy="serviceType"]`).should('have.value', 'fooey');

      cy.get(`[data-cy="environment"]`).type('deed upwardly');
      cy.get(`[data-cy="environment"]`).should('have.value', 'deed upwardly');

      cy.get(`[data-cy="monitoringEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="monitoringEnabled"]`).click();
      cy.get(`[data-cy="monitoringEnabled"]`).should('be.checked');

      cy.get(`[data-cy="clusterMonitoringEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="clusterMonitoringEnabled"]`).click();
      cy.get(`[data-cy="clusterMonitoringEnabled"]`).should('be.checked');

      cy.get(`[data-cy="intervalSeconds"]`).type('31182');
      cy.get(`[data-cy="intervalSeconds"]`).should('have.value', '31182');

      cy.get(`[data-cy="timeoutMs"]`).type('16766');
      cy.get(`[data-cy="timeoutMs"]`).should('have.value', '16766');

      cy.get(`[data-cy="retryCount"]`).type('16639');
      cy.get(`[data-cy="retryCount"]`).should('have.value', '16639');

      cy.get(`[data-cy="latencyWarningMs"]`).type('30349');
      cy.get(`[data-cy="latencyWarningMs"]`).should('have.value', '30349');

      cy.get(`[data-cy="latencyCriticalMs"]`).type('32392');
      cy.get(`[data-cy="latencyCriticalMs"]`).should('have.value', '32392');

      cy.get(`[data-cy="advancedConfig"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="advancedConfig"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-05T04:05');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-05T04:05');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-04T07:16');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-04T07:16');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        monitoredService = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', monitoredServicePageUrlPattern);
    });
  });
});
