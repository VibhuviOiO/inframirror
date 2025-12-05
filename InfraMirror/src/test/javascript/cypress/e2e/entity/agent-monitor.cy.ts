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

describe('AgentMonitor e2e test', () => {
  const agentMonitorPageUrl = '/agent-monitor';
  const agentMonitorPageUrlPattern = new RegExp('/agent-monitor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const agentMonitorSample = { active: true, createdBy: 'up swiftly' };

  let agentMonitor;
  let agent;
  let httpMonitor;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/agents',
      body: { name: 'hospitalization' },
    }).then(({ body }) => {
      agent = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/http-monitors',
      body: {
        name: 'sedately joyfully instead',
        method: 'than accel',
        type: 'whenever',
        url: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        headers: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        body: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        intervalSeconds: 28338,
        timeoutSeconds: 17814,
        retryCount: 16021,
        retryDelaySeconds: 6387,
        responseTimeWarningMs: 19162,
        responseTimeCriticalMs: 9878,
        uptimeWarningPercent: 31510.47,
        uptimeCriticalPercent: 12669.3,
        includeResponseBody: true,
        resendNotificationCount: 7615,
        certificateExpiryDays: 19613,
        ignoreTlsError: false,
        checkSslCertificate: false,
        checkDnsResolution: false,
        upsideDownMode: true,
        maxRedirects: 24177,
        description: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        tags: 'drat',
        enabled: true,
        expectedStatusCodes: 'modulo',
        performanceBudgetMs: 17616,
        sizeBudgetKb: 18983,
      },
    }).then(({ body }) => {
      httpMonitor = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/agent-monitors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/agent-monitors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/agent-monitors/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/agents', {
      statusCode: 200,
      body: [agent],
    });

    cy.intercept('GET', '/api/http-monitors', {
      statusCode: 200,
      body: [httpMonitor],
    });
  });

  afterEach(() => {
    if (agentMonitor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/agent-monitors/${agentMonitor.id}`,
      }).then(() => {
        agentMonitor = undefined;
      });
    }
  });

  afterEach(() => {
    if (agent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/agents/${agent.id}`,
      }).then(() => {
        agent = undefined;
      });
    }
    if (httpMonitor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/http-monitors/${httpMonitor.id}`,
      }).then(() => {
        httpMonitor = undefined;
      });
    }
  });

  it('AgentMonitors menu should load AgentMonitors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('agent-monitor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AgentMonitor').should('exist');
    cy.url().should('match', agentMonitorPageUrlPattern);
  });

  describe('AgentMonitor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(agentMonitorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AgentMonitor page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/agent-monitor/new$'));
        cy.getEntityCreateUpdateHeading('AgentMonitor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentMonitorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/agent-monitors',
          body: {
            ...agentMonitorSample,
            agent,
            monitor: httpMonitor,
          },
        }).then(({ body }) => {
          agentMonitor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/agent-monitors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [agentMonitor],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(agentMonitorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AgentMonitor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('agentMonitor');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentMonitorPageUrlPattern);
      });

      it('edit button click should load edit AgentMonitor page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentMonitor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentMonitorPageUrlPattern);
      });

      it('edit button click should load edit AgentMonitor page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentMonitor');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentMonitorPageUrlPattern);
      });

      it('last delete button click should delete instance of AgentMonitor', () => {
        cy.intercept('GET', '/api/agent-monitors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('agentMonitor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentMonitorPageUrlPattern);

        agentMonitor = undefined;
      });
    });
  });

  describe('new AgentMonitor page', () => {
    beforeEach(() => {
      cy.visit(`${agentMonitorPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AgentMonitor');
    });

    it('should create an instance of AgentMonitor', () => {
      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(`[data-cy="createdBy"]`).type('besides');
      cy.get(`[data-cy="createdBy"]`).should('have.value', 'besides');

      cy.get(`[data-cy="createdDate"]`).type('2025-12-04T21:16');
      cy.get(`[data-cy="createdDate"]`).blur();
      cy.get(`[data-cy="createdDate"]`).should('have.value', '2025-12-04T21:16');

      cy.get(`[data-cy="lastModifiedBy"]`).type('save upon');
      cy.get(`[data-cy="lastModifiedBy"]`).should('have.value', 'save upon');

      cy.get(`[data-cy="lastModifiedDate"]`).type('2025-12-04T19:00');
      cy.get(`[data-cy="lastModifiedDate"]`).blur();
      cy.get(`[data-cy="lastModifiedDate"]`).should('have.value', '2025-12-04T19:00');

      cy.get(`[data-cy="agent"]`).select(1);
      cy.get(`[data-cy="monitor"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        agentMonitor = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', agentMonitorPageUrlPattern);
    });
  });
});
