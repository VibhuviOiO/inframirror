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

describe('InstanceHeartbeat e2e test', () => {
  const instanceHeartbeatPageUrl = '/instance-heartbeat';
  const instanceHeartbeatPageUrlPattern = new RegExp('/instance-heartbeat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const instanceHeartbeatSample = {"executedAt":"2025-12-04T13:10:13.769Z","heartbeatType":"until","success":false,"status":"beneath far"};

  let instanceHeartbeat;
  // let instance;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/instances',
      body: {"name":"rowdy consistency arrogantly","hostname":"switch","description":"toward when","instanceType":"blaspheme knuckle naughty","monitoringType":"valuable finding","operatingSystem":"relieve yum","platform":"giggle whenever procurement","privateIpAddress":"design powerful","publicIpAddress":"liquid even","tags":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","pingEnabled":false,"pingInterval":6602,"pingTimeoutMs":14893,"pingRetryCount":4633,"hardwareMonitoringEnabled":true,"hardwareMonitoringInterval":21252,"cpuWarningThreshold":32701,"cpuDangerThreshold":8309,"memoryWarningThreshold":22225,"memoryDangerThreshold":8250,"diskWarningThreshold":5423,"diskDangerThreshold":1505,"createdAt":"2025-12-05T00:32:16.518Z","updatedAt":"2025-12-04T11:40:52.633Z","lastPingAt":"2025-12-04T08:17:40.060Z","lastHardwareCheckAt":"2025-12-05T03:51:14.144Z"},
    }).then(({ body }) => {
      instance = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/instance-heartbeats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/instance-heartbeats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/instance-heartbeats/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/agents', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/instances', {
      statusCode: 200,
      body: [instance],
    });

  });
   */

  afterEach(() => {
    if (instanceHeartbeat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/instance-heartbeats/${instanceHeartbeat.id}`,
      }).then(() => {
        instanceHeartbeat = undefined;
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
  });
   */

  it('InstanceHeartbeats menu should load InstanceHeartbeats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('instance-heartbeat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InstanceHeartbeat').should('exist');
    cy.url().should('match', instanceHeartbeatPageUrlPattern);
  });

  describe('InstanceHeartbeat page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(instanceHeartbeatPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InstanceHeartbeat page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/instance-heartbeat/new$'));
        cy.getEntityCreateUpdateHeading('InstanceHeartbeat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instanceHeartbeatPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/instance-heartbeats',
          body: {
            ...instanceHeartbeatSample,
            instance: instance,
          },
        }).then(({ body }) => {
          instanceHeartbeat = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/instance-heartbeats+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/instance-heartbeats?page=0&size=20>; rel="last",<http://localhost/api/instance-heartbeats?page=0&size=20>; rel="first"',
              },
              body: [instanceHeartbeat],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(instanceHeartbeatPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(instanceHeartbeatPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details InstanceHeartbeat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('instanceHeartbeat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instanceHeartbeatPageUrlPattern);
      });

      it('edit button click should load edit InstanceHeartbeat page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InstanceHeartbeat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instanceHeartbeatPageUrlPattern);
      });

      it('edit button click should load edit InstanceHeartbeat page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InstanceHeartbeat');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instanceHeartbeatPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of InstanceHeartbeat', () => {
        cy.intercept('GET', '/api/instance-heartbeats/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('instanceHeartbeat').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', instanceHeartbeatPageUrlPattern);

        instanceHeartbeat = undefined;
      });
    });
  });

  describe('new InstanceHeartbeat page', () => {
    beforeEach(() => {
      cy.visit(`${instanceHeartbeatPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InstanceHeartbeat');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of InstanceHeartbeat', () => {
      cy.get(`[data-cy="executedAt"]`).type('2025-12-04T20:41');
      cy.get(`[data-cy="executedAt"]`).blur();
      cy.get(`[data-cy="executedAt"]`).should('have.value', '2025-12-04T20:41');

      cy.get(`[data-cy="heartbeatType"]`).type('while revoke');
      cy.get(`[data-cy="heartbeatType"]`).should('have.value', 'while revoke');

      cy.get(`[data-cy="success"]`).should('not.be.checked');
      cy.get(`[data-cy="success"]`).click();
      cy.get(`[data-cy="success"]`).should('be.checked');

      cy.get(`[data-cy="responseTimeMs"]`).type('16806');
      cy.get(`[data-cy="responseTimeMs"]`).should('have.value', '16806');

      cy.get(`[data-cy="packetLoss"]`).type('29041.1');
      cy.get(`[data-cy="packetLoss"]`).should('have.value', '29041.1');

      cy.get(`[data-cy="jitterMs"]`).type('19769');
      cy.get(`[data-cy="jitterMs"]`).should('have.value', '19769');

      cy.get(`[data-cy="cpuUsage"]`).type('32686.1');
      cy.get(`[data-cy="cpuUsage"]`).should('have.value', '32686.1');

      cy.get(`[data-cy="memoryUsage"]`).type('23786.52');
      cy.get(`[data-cy="memoryUsage"]`).should('have.value', '23786.52');

      cy.get(`[data-cy="diskUsage"]`).type('10879.25');
      cy.get(`[data-cy="diskUsage"]`).should('have.value', '10879.25');

      cy.get(`[data-cy="loadAverage"]`).type('6544.92');
      cy.get(`[data-cy="loadAverage"]`).should('have.value', '6544.92');

      cy.get(`[data-cy="processCount"]`).type('472');
      cy.get(`[data-cy="processCount"]`).should('have.value', '472');

      cy.get(`[data-cy="networkRxBytes"]`).type('12130');
      cy.get(`[data-cy="networkRxBytes"]`).should('have.value', '12130');

      cy.get(`[data-cy="networkTxBytes"]`).type('11713');
      cy.get(`[data-cy="networkTxBytes"]`).should('have.value', '11713');

      cy.get(`[data-cy="uptimeSeconds"]`).type('10076');
      cy.get(`[data-cy="uptimeSeconds"]`).should('have.value', '10076');

      cy.get(`[data-cy="status"]`).type('delightfully enchant');
      cy.get(`[data-cy="status"]`).should('have.value', 'delightfully enchant');

      cy.get(`[data-cy="errorMessage"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="errorMessage"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="errorType"]`).type('wombat lest');
      cy.get(`[data-cy="errorType"]`).should('have.value', 'wombat lest');

      cy.get(`[data-cy="metadata"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="metadata"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="instance"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        instanceHeartbeat = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', instanceHeartbeatPageUrlPattern);
    });
  });
});
