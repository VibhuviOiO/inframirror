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
  // const serviceInstanceSample = {"port":31842};

  let serviceInstance;
  // let instance;
  // let monitoredService;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/instances',
      body: {"name":"steel","hostname":"straight mmm consequently","description":"diligently greatly","instanceType":"interchange","monitoringType":"finally obnoxiously inasmuch","operatingSystem":"convalesce what","platform":"considering scale","privateIpAddress":"reclassify fortunately","publicIpAddress":"ew","tags":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","pingEnabled":false,"pingInterval":26600,"pingTimeoutMs":32224,"pingRetryCount":24950,"hardwareMonitoringEnabled":false,"hardwareMonitoringInterval":20918,"cpuWarningThreshold":16180,"cpuDangerThreshold":24527,"memoryWarningThreshold":20838,"memoryDangerThreshold":14803,"diskWarningThreshold":27792,"diskDangerThreshold":2777,"createdAt":"2025-12-05T04:47:21.730Z","updatedAt":"2025-12-04T17:49:59.667Z","lastPingAt":"2025-12-04T15:56:31.858Z","lastHardwareCheckAt":"2025-12-04T06:00:49.313Z"},
    }).then(({ body }) => {
      instance = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/monitored-services',
      body: {"name":"as","description":"who equally","serviceType":"ick","environment":"including","monitoringEnabled":true,"clusterMonitoringEnabled":false,"intervalSeconds":16178,"timeoutMs":884,"retryCount":15938,"latencyWarningMs":15567,"latencyCriticalMs":19936,"advancedConfig":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","isActive":false,"createdAt":"2025-12-04T08:39:20.770Z","updatedAt":"2025-12-04T20:14:45.584Z"},
    }).then(({ body }) => {
      monitoredService = body;
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

    cy.intercept('GET', '/api/monitored-services', {
      statusCode: 200,
      body: [monitoredService],
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
    if (monitoredService) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/monitored-services/${monitoredService.id}`,
      }).then(() => {
        monitoredService = undefined;
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
            monitoredService: monitoredService,
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
      cy.get(`[data-cy="port"]`).type('19704');
      cy.get(`[data-cy="port"]`).should('have.value', '19704');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T08:10');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T08:10');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-04T09:33');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-04T09:33');

      cy.get(`[data-cy="instance"]`).select(1);
      cy.get(`[data-cy="monitoredService"]`).select(1);

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
