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

describe('ServiceHeartbeat e2e test', () => {
  const serviceHeartbeatPageUrl = '/service-heartbeat';
  const serviceHeartbeatPageUrlPattern = new RegExp('/service-heartbeat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const serviceHeartbeatSample = { executedAt: '2025-12-04T13:20:57.088Z', success: false, status: 'like finally pressur' };

  let serviceHeartbeat;
  let service;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/services',
      body: {
        name: 'lamp safeguard',
        description: 'indelible ew deceivingly',
        serviceType: 'untimely',
        environment: 'moment splash',
        monitoringEnabled: true,
        clusterMonitoringEnabled: false,
        intervalSeconds: 1858,
        timeoutMs: 1621,
        retryCount: 15056,
        latencyWarningMs: 19586,
        latencyCriticalMs: 21398,
        advancedConfig: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        isActive: false,
        createdAt: '2025-12-04T18:23:17.662Z',
        updatedAt: '2025-12-05T03:54:14.508Z',
      },
    }).then(({ body }) => {
      service = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/service-heartbeats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/service-heartbeats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/service-heartbeats/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/agents', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/services', {
      statusCode: 200,
      body: [service],
    });

    cy.intercept('GET', '/api/service-instances', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (serviceHeartbeat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/service-heartbeats/${serviceHeartbeat.id}`,
      }).then(() => {
        serviceHeartbeat = undefined;
      });
    }
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

  it('ServiceHeartbeats menu should load ServiceHeartbeats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('service-heartbeat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ServiceHeartbeat').should('exist');
    cy.url().should('match', serviceHeartbeatPageUrlPattern);
  });

  describe('ServiceHeartbeat page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(serviceHeartbeatPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ServiceHeartbeat page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/service-heartbeat/new$'));
        cy.getEntityCreateUpdateHeading('ServiceHeartbeat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHeartbeatPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/service-heartbeats',
          body: {
            ...serviceHeartbeatSample,
            service,
          },
        }).then(({ body }) => {
          serviceHeartbeat = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/service-heartbeats+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/service-heartbeats?page=0&size=20>; rel="last",<http://localhost/api/service-heartbeats?page=0&size=20>; rel="first"',
              },
              body: [serviceHeartbeat],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(serviceHeartbeatPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ServiceHeartbeat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('serviceHeartbeat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHeartbeatPageUrlPattern);
      });

      it('edit button click should load edit ServiceHeartbeat page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ServiceHeartbeat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHeartbeatPageUrlPattern);
      });

      it('edit button click should load edit ServiceHeartbeat page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ServiceHeartbeat');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHeartbeatPageUrlPattern);
      });

      it('last delete button click should delete instance of ServiceHeartbeat', () => {
        cy.intercept('GET', '/api/service-heartbeats/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('serviceHeartbeat').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceHeartbeatPageUrlPattern);

        serviceHeartbeat = undefined;
      });
    });
  });

  describe('new ServiceHeartbeat page', () => {
    beforeEach(() => {
      cy.visit(`${serviceHeartbeatPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ServiceHeartbeat');
    });

    it('should create an instance of ServiceHeartbeat', () => {
      cy.get(`[data-cy="executedAt"]`).type('2025-12-05T00:37');
      cy.get(`[data-cy="executedAt"]`).blur();
      cy.get(`[data-cy="executedAt"]`).should('have.value', '2025-12-05T00:37');

      cy.get(`[data-cy="success"]`).should('not.be.checked');
      cy.get(`[data-cy="success"]`).click();
      cy.get(`[data-cy="success"]`).should('be.checked');

      cy.get(`[data-cy="status"]`).type('mammoth creative');
      cy.get(`[data-cy="status"]`).should('have.value', 'mammoth creative');

      cy.get(`[data-cy="responseTimeMs"]`).type('5716');
      cy.get(`[data-cy="responseTimeMs"]`).should('have.value', '5716');

      cy.get(`[data-cy="errorMessage"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="errorMessage"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="metadata"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="metadata"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="service"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        serviceHeartbeat = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', serviceHeartbeatPageUrlPattern);
    });
  });
});
