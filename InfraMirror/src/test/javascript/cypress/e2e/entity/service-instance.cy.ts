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

describe('ServiceInstance e2e test', () => {
  const serviceInstancePageUrl = '/service-instance';
  const serviceInstancePageUrlPattern = new RegExp('/service-instance(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const serviceInstanceSample = {"port":31215};

  let serviceInstance;
  // let instance;
  // let service;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/instances',
      body: {"name":"though scamper yowza","hostname":"yearly neatly a","description":"energetic","instanceType":"cleverly","monitoringType":"to fervently","operatingSystem":"safely vacantly","platform":"taxicab enhance","privateIpAddress":"the shyly","publicIpAddress":"where yippee","tags":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","pingEnabled":false,"pingInterval":27237,"pingTimeoutMs":6490,"pingRetryCount":25105,"hardwareMonitoringEnabled":true,"hardwareMonitoringInterval":14987,"cpuWarningThreshold":20234,"cpuDangerThreshold":28625,"memoryWarningThreshold":32755,"memoryDangerThreshold":5646,"diskWarningThreshold":25506,"diskDangerThreshold":1811,"createdAt":"2025-12-04T13:26:20.737Z","updatedAt":"2025-12-04T19:49:47.494Z","lastPingAt":"2025-12-04T16:00:14.394Z","lastHardwareCheckAt":"2025-12-04T20:01:07.846Z"},
    }).then(({ body }) => {
      instance = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/services',
      body: {"name":"behold hmph","description":"nor hearten","serviceType":"foolishly","environment":"bliss misreport","monitoringEnabled":true,"clusterMonitoringEnabled":false,"intervalSeconds":20294,"timeoutMs":6879,"retryCount":17531,"latencyWarningMs":25513,"latencyCriticalMs":8559,"advancedConfig":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","isActive":false,"createdAt":"2025-12-04T19:52:19.859Z","updatedAt":"2025-12-04T19:26:45.561Z"},
    }).then(({ body }) => {
      service = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/service-instances+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/service-instances').as('postEntityRequest');
    cy.intercept('DELETE', '/api/service-instances/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/service-heartbeats', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/instances', {
      statusCode: 200,
      body: [instance],
    });

    cy.intercept('GET', '/api/services', {
      statusCode: 200,
      body: [service],
    });

  });
   */

  afterEach(() => {
    if (serviceInstance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/service-instances/${serviceInstance.id}`,
      }).then(() => {
        serviceInstance = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (instance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/instances/${instance.id}`,
      }).then(() => {
        instance = undefined;
      });
    }
    if (service) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/services/${service.id}`,
      }).then(() => {
        service = undefined;
      });
    }
  });
   */

  it('ServiceInstances menu should load ServiceInstances page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('service-instance');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ServiceInstance').should('exist');
    cy.url().should('match', serviceInstancePageUrlPattern);
  });

  describe('ServiceInstance page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(serviceInstancePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ServiceInstance page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/service-instance/new$'));
        cy.getEntityCreateUpdateHeading('ServiceInstance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceInstancePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/service-instances',
          body: {
            ...serviceInstanceSample,
            instance: instance,
            service: service,
          },
        }).then(({ body }) => {
          serviceInstance = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/service-instances+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/service-instances?page=0&size=20>; rel="last",<http://localhost/api/service-instances?page=0&size=20>; rel="first"',
              },
              body: [serviceInstance],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(serviceInstancePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(serviceInstancePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ServiceInstance page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('serviceInstance');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceInstancePageUrlPattern);
      });

      it('edit button click should load edit ServiceInstance page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ServiceInstance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceInstancePageUrlPattern);
      });

      it('edit button click should load edit ServiceInstance page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ServiceInstance');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceInstancePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ServiceInstance', () => {
        cy.intercept('GET', '/api/service-instances/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('serviceInstance').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', serviceInstancePageUrlPattern);

        serviceInstance = undefined;
      });
    });
  });

  describe('new ServiceInstance page', () => {
    beforeEach(() => {
      cy.visit(`${serviceInstancePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ServiceInstance');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ServiceInstance', () => {
      cy.get(`[data-cy="port"]`).type('6162');
      cy.get(`[data-cy="port"]`).should('have.value', '6162');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T18:44');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T18:44');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-04T14:04');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-04T14:04');

      cy.get(`[data-cy="instance"]`).select(1);
      cy.get(`[data-cy="service"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        serviceInstance = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', serviceInstancePageUrlPattern);
    });
  });
});
