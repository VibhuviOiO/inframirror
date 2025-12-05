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
    name: 'pro greedily',
    hostname: 'finally replicate',
    instanceType: 'oh',
    monitoringType: 'until',
    pingEnabled: true,
    pingInterval: 2499,
    pingTimeoutMs: 11829,
    pingRetryCount: 11779,
    hardwareMonitoringEnabled: true,
    hardwareMonitoringInterval: 23338,
    cpuWarningThreshold: 20941,
    cpuDangerThreshold: 24387,
    memoryWarningThreshold: 16940,
    memoryDangerThreshold: 10999,
    diskWarningThreshold: 1472,
    diskDangerThreshold: 6343,
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
      body: { code: 'bar despit', name: 'acquire' },
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
      cy.get(`[data-cy="name"]`).type('produce');
      cy.get(`[data-cy="name"]`).should('have.value', 'produce');

      cy.get(`[data-cy="hostname"]`).type('besides');
      cy.get(`[data-cy="hostname"]`).should('have.value', 'besides');

      cy.get(`[data-cy="description"]`).type('cannibalise');
      cy.get(`[data-cy="description"]`).should('have.value', 'cannibalise');

      cy.get(`[data-cy="instanceType"]`).type('revitalise unimpressively upright');
      cy.get(`[data-cy="instanceType"]`).should('have.value', 'revitalise unimpressively upright');

      cy.get(`[data-cy="monitoringType"]`).type('well petty');
      cy.get(`[data-cy="monitoringType"]`).should('have.value', 'well petty');

      cy.get(`[data-cy="operatingSystem"]`).type('furthermore concrete');
      cy.get(`[data-cy="operatingSystem"]`).should('have.value', 'furthermore concrete');

      cy.get(`[data-cy="platform"]`).type('unique analogy');
      cy.get(`[data-cy="platform"]`).should('have.value', 'unique analogy');

      cy.get(`[data-cy="privateIpAddress"]`).type('circular');
      cy.get(`[data-cy="privateIpAddress"]`).should('have.value', 'circular');

      cy.get(`[data-cy="publicIpAddress"]`).type('litter');
      cy.get(`[data-cy="publicIpAddress"]`).should('have.value', 'litter');

      cy.get(`[data-cy="tags"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="tags"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="pingEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="pingEnabled"]`).click();
      cy.get(`[data-cy="pingEnabled"]`).should('be.checked');

      cy.get(`[data-cy="pingInterval"]`).type('2541');
      cy.get(`[data-cy="pingInterval"]`).should('have.value', '2541');

      cy.get(`[data-cy="pingTimeoutMs"]`).type('1678');
      cy.get(`[data-cy="pingTimeoutMs"]`).should('have.value', '1678');

      cy.get(`[data-cy="pingRetryCount"]`).type('27265');
      cy.get(`[data-cy="pingRetryCount"]`).should('have.value', '27265');

      cy.get(`[data-cy="hardwareMonitoringEnabled"]`).should('not.be.checked');
      cy.get(`[data-cy="hardwareMonitoringEnabled"]`).click();
      cy.get(`[data-cy="hardwareMonitoringEnabled"]`).should('be.checked');

      cy.get(`[data-cy="hardwareMonitoringInterval"]`).type('8246');
      cy.get(`[data-cy="hardwareMonitoringInterval"]`).should('have.value', '8246');

      cy.get(`[data-cy="cpuWarningThreshold"]`).type('21313');
      cy.get(`[data-cy="cpuWarningThreshold"]`).should('have.value', '21313');

      cy.get(`[data-cy="cpuDangerThreshold"]`).type('23840');
      cy.get(`[data-cy="cpuDangerThreshold"]`).should('have.value', '23840');

      cy.get(`[data-cy="memoryWarningThreshold"]`).type('5148');
      cy.get(`[data-cy="memoryWarningThreshold"]`).should('have.value', '5148');

      cy.get(`[data-cy="memoryDangerThreshold"]`).type('14840');
      cy.get(`[data-cy="memoryDangerThreshold"]`).should('have.value', '14840');

      cy.get(`[data-cy="diskWarningThreshold"]`).type('12174');
      cy.get(`[data-cy="diskWarningThreshold"]`).should('have.value', '12174');

      cy.get(`[data-cy="diskDangerThreshold"]`).type('28488');
      cy.get(`[data-cy="diskDangerThreshold"]`).should('have.value', '28488');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T10:41');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T10:41');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-04T21:25');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-04T21:25');

      cy.get(`[data-cy="lastPingAt"]`).type('2025-12-04T23:01');
      cy.get(`[data-cy="lastPingAt"]`).blur();
      cy.get(`[data-cy="lastPingAt"]`).should('have.value', '2025-12-04T23:01');

      cy.get(`[data-cy="lastHardwareCheckAt"]`).type('2025-12-05T00:16');
      cy.get(`[data-cy="lastHardwareCheckAt"]`).blur();
      cy.get(`[data-cy="lastHardwareCheckAt"]`).should('have.value', '2025-12-05T00:16');

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
