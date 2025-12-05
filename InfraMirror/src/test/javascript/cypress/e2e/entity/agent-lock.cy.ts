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

describe('AgentLock e2e test', () => {
  const agentLockPageUrl = '/agent-lock';
  const agentLockPageUrlPattern = new RegExp('/agent-lock(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const agentLockSample = { agentId: 28629, acquiredAt: '2025-12-04T23:35:36.232Z', expiresAt: '2025-12-05T04:14:13.979Z' };

  let agentLock;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/agent-locks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/agent-locks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/agent-locks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (agentLock) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/agent-locks/${agentLock.id}`,
      }).then(() => {
        agentLock = undefined;
      });
    }
  });

  it('AgentLocks menu should load AgentLocks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('agent-lock');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AgentLock').should('exist');
    cy.url().should('match', agentLockPageUrlPattern);
  });

  describe('AgentLock page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(agentLockPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AgentLock page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/agent-lock/new$'));
        cy.getEntityCreateUpdateHeading('AgentLock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentLockPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/agent-locks',
          body: agentLockSample,
        }).then(({ body }) => {
          agentLock = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/agent-locks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/agent-locks?page=0&size=20>; rel="last",<http://localhost/api/agent-locks?page=0&size=20>; rel="first"',
              },
              body: [agentLock],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(agentLockPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AgentLock page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('agentLock');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentLockPageUrlPattern);
      });

      it('edit button click should load edit AgentLock page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentLock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentLockPageUrlPattern);
      });

      it('edit button click should load edit AgentLock page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentLock');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentLockPageUrlPattern);
      });

      it('last delete button click should delete instance of AgentLock', () => {
        cy.intercept('GET', '/api/agent-locks/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('agentLock').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentLockPageUrlPattern);

        agentLock = undefined;
      });
    });
  });

  describe('new AgentLock page', () => {
    beforeEach(() => {
      cy.visit(`${agentLockPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AgentLock');
    });

    it('should create an instance of AgentLock', () => {
      cy.get(`[data-cy="agentId"]`).type('18424');
      cy.get(`[data-cy="agentId"]`).should('have.value', '18424');

      cy.get(`[data-cy="acquiredAt"]`).type('2025-12-05T02:11');
      cy.get(`[data-cy="acquiredAt"]`).blur();
      cy.get(`[data-cy="acquiredAt"]`).should('have.value', '2025-12-05T02:11');

      cy.get(`[data-cy="expiresAt"]`).type('2025-12-04T22:25');
      cy.get(`[data-cy="expiresAt"]`).blur();
      cy.get(`[data-cy="expiresAt"]`).should('have.value', '2025-12-04T22:25');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        agentLock = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', agentLockPageUrlPattern);
    });
  });
});
