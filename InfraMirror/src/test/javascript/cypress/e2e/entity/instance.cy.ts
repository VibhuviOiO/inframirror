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

describe('Instance e2e test', () => {
  const instancePageUrl = '/instance';
  const instancePageUrlPattern = new RegExp('/instance(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const instanceSample = {
    name: 'spirit',
    hostname: 'vice',
    instanceType: 'morbidity kindly',
    monitoringType: 'aha wetly',
    pingEnabled: false,
    pingInterval: 22570,
    pingTimeoutMs: 2986,
    pingRetryCount: 19446,
    hardwareMonitoringEnabled: false,
    hardwareMonitoringInterval: 8392,
    cpuWarningThreshold: 31597,
    cpuDangerThreshold: 12441,
    memoryWarningThreshold: 32202,
    memoryDangerThreshold: 2659,
    diskWarningThreshold: 356,
    diskDangerThreshold: 20659,
  };

  let instance;
  let datacenter;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/datacenters',
      body: { code: 'bookcase b', name: 'oof gah crocodile' },
    }).then(({ body }) => {
      datacenter = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/instances+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/instances').as('postEntityRequest');
    cy.intercept('DELETE', '/api/instances/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/instance-heartbeats', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/service-instances', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/datacenters', {
      statusCode: 200,
      body: [datacenter],
    });

    cy.intercept('GET', '/api/agents', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (instance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/instances/${instance.id}`,
      }).then(() => {
        instance = undefined;
      });
    }
  });

  afterEach(() => {
    if (datacenter) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/datacenters/${datacenter.id}`,
      }).then(() => {
        datacenter = undefined;
      });
    }
  });

  it('Instances menu should load Instances page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('instance');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Instance').should('exist');
    cy.url().should('match', instancePageUrlPattern);
  });

  describe('Instance page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(instancePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Instance page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/instance/new$'));
        cy.getEntityCreateUpdateHeading('Instance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instancePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/instances',
          body: {
            ...instanceSample,
            datacenter,
          },
        }).then(({ body }) => {
          instance = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/instances+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/instances?page=0&size=20>; rel="last",<http://localhost/api/instances?page=0&size=20>; rel="first"',
              },
              body: [instance],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(instancePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Instance page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('instance');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instancePageUrlPattern);
      });

      it('edit button click should load edit Instance page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Instance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instancePageUrlPattern);
      });

      it('edit button click should load edit Instance page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Instance');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instancePageUrlPattern);
      });

      it('last delete button click should delete instance of Instance', () => {
        cy.intercept('GET', '/api/instances/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('instance').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instancePageUrlPattern);

        instance = undefined;
      });
    });
  });

  describe('new Instance page', () => {
    beforeEach(() => {
      cy.visit(`${instancePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Instance');
    });

    it('should create an instance of Instance', () => {
      cy.get(`[data-cy="name"]`).type('think unlined state');
      cy.get(`[data-cy="name"]`).should('have.value', 'think unlined state');

      cy.get(`[data-cy="hostname"]`).type('question');
      cy.get(`[data-cy="hostname"]`).should('have.value', 'question');

      cy.get(`[data-cy="description"]`).type('colorize but when');
      cy.get(`[data-cy="description"]`).should('have.value', 'colorize but when');

      cy.get(`[data-cy="instanceType"]`).type('grubby');
      cy.get(`[data-cy="instanceType"]`).should('have.value', 'grubby');

      cy.get(`[data-cy="monitoringType"]`).type('heartfelt');
      cy.get(`[data-cy="monitoringType"]`).should('have.value', 'heartfelt');

      cy.get(`[data-cy="operatingSystem"]`).type('violently reassemble');
      cy.get(`[data-cy="operatingSystem"]`).should('have.value', 'violently reassemble');

      cy.get(`[data-cy="platform"]`).type('but towards indeed');
      cy.get(`[data-cy="platform"]`).should('have.value', 'but towards indeed');

      cy.get(`[data-cy="privateIpAddress"]`).type('yahoo');
      cy.get(`[data-cy="privateIpAddress"]`).should('have.value', 'yahoo');

      cy.get(`[data-cy="publicIpAddress"]`).type('pfft bitterly');
      cy.get(`[data-cy="publicIpAddress"]`).should('have.value', 'pfft bitterly');

      cy.get(`[data-cy="tags"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="tags"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="pingEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="pingEnabled"]`).click();
      cy.get(`[data-cy="pingEnabled"]`).should('be.checked');

      cy.get(`[data-cy="pingInterval"]`).type('7877');
      cy.get(`[data-cy="pingInterval"]`).should('have.value', '7877');

      cy.get(`[data-cy="pingTimeoutMs"]`).type('29071');
      cy.get(`[data-cy="pingTimeoutMs"]`).should('have.value', '29071');

      cy.get(`[data-cy="pingRetryCount"]`).type('7224');
      cy.get(`[data-cy="pingRetryCount"]`).should('have.value', '7224');

      cy.get(`[data-cy="hardwareMonitoringEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="hardwareMonitoringEnabled"]`).click();
      cy.get(`[data-cy="hardwareMonitoringEnabled"]`).should('be.checked');

      cy.get(`[data-cy="hardwareMonitoringInterval"]`).type('20320');
      cy.get(`[data-cy="hardwareMonitoringInterval"]`).should('have.value', '20320');

      cy.get(`[data-cy="cpuWarningThreshold"]`).type('31916');
      cy.get(`[data-cy="cpuWarningThreshold"]`).should('have.value', '31916');

      cy.get(`[data-cy="cpuDangerThreshold"]`).type('2785');
      cy.get(`[data-cy="cpuDangerThreshold"]`).should('have.value', '2785');

      cy.get(`[data-cy="memoryWarningThreshold"]`).type('4869');
      cy.get(`[data-cy="memoryWarningThreshold"]`).should('have.value', '4869');

      cy.get(`[data-cy="memoryDangerThreshold"]`).type('3053');
      cy.get(`[data-cy="memoryDangerThreshold"]`).should('have.value', '3053');

      cy.get(`[data-cy="diskWarningThreshold"]`).type('21224');
      cy.get(`[data-cy="diskWarningThreshold"]`).should('have.value', '21224');

      cy.get(`[data-cy="diskDangerThreshold"]`).type('17677');
      cy.get(`[data-cy="diskDangerThreshold"]`).should('have.value', '17677');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T09:20');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T09:20');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-05T04:54');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-05T04:54');

      cy.get(`[data-cy="lastPingAt"]`).type('2025-12-04T11:51');
      cy.get(`[data-cy="lastPingAt"]`).blur();
      cy.get(`[data-cy="lastPingAt"]`).should('have.value', '2025-12-04T11:51');

      cy.get(`[data-cy="lastHardwareCheckAt"]`).type('2025-12-04T07:38');
      cy.get(`[data-cy="lastHardwareCheckAt"]`).blur();
      cy.get(`[data-cy="lastHardwareCheckAt"]`).should('have.value', '2025-12-04T07:38');

      cy.get(`[data-cy="datacenter"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        instance = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', instancePageUrlPattern);
    });
  });
});
