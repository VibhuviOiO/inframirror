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

describe('ApiKey e2e test', () => {
  const apiKeyPageUrl = '/api-key';
  const apiKeyPageUrlPattern = new RegExp('/api-key(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const apiKeySample = { name: 'openly along frenetically', keyHash: 'fold gah handover', active: true, createdBy: 'culminate' };

  let apiKey;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/api-keys+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/api-keys').as('postEntityRequest');
    cy.intercept('DELETE', '/api/api-keys/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (apiKey) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/api-keys/${apiKey.id}`,
      }).then(() => {
        apiKey = undefined;
      });
    }
  });

  it('ApiKeys menu should load ApiKeys page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('api-key');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ApiKey').should('exist');
    cy.url().should('match', apiKeyPageUrlPattern);
  });

  describe('ApiKey page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(apiKeyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ApiKey page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/api-key/new$'));
        cy.getEntityCreateUpdateHeading('ApiKey');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiKeyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/api-keys',
          body: apiKeySample,
        }).then(({ body }) => {
          apiKey = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/api-keys+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/api-keys?page=0&size=20>; rel="last",<http://localhost/api/api-keys?page=0&size=20>; rel="first"',
              },
              body: [apiKey],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(apiKeyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ApiKey page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('apiKey');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiKeyPageUrlPattern);
      });

      it('edit button click should load edit ApiKey page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ApiKey');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiKeyPageUrlPattern);
      });

      it('edit button click should load edit ApiKey page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ApiKey');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiKeyPageUrlPattern);
      });

      it('last delete button click should delete instance of ApiKey', () => {
        cy.intercept('GET', '/api/api-keys/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('apiKey').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', apiKeyPageUrlPattern);

        apiKey = undefined;
      });
    });
  });

  describe('new ApiKey page', () => {
    beforeEach(() => {
      cy.visit(`${apiKeyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ApiKey');
    });

    it('should create an instance of ApiKey', () => {
      cy.get(`[data-cy="name"]`).type('colorfully');
      cy.get(`[data-cy="name"]`).should('have.value', 'colorfully');

      cy.get(`[data-cy="description"]`).type('aw doodle');
      cy.get(`[data-cy="description"]`).should('have.value', 'aw doodle');

      cy.get(`[data-cy="keyHash"]`).type('yuck wring');
      cy.get(`[data-cy="keyHash"]`).should('have.value', 'yuck wring');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(`[data-cy="lastUsedDate"]`).type('2025-12-04T23:29');
      cy.get(`[data-cy="lastUsedDate"]`).blur();
      cy.get(`[data-cy="lastUsedDate"]`).should('have.value', '2025-12-04T23:29');

      cy.get(`[data-cy="expiresAt"]`).type('2025-12-04T11:18');
      cy.get(`[data-cy="expiresAt"]`).blur();
      cy.get(`[data-cy="expiresAt"]`).should('have.value', '2025-12-04T11:18');

      cy.get(`[data-cy="createdBy"]`).type('absentmindedly per');
      cy.get(`[data-cy="createdBy"]`).should('have.value', 'absentmindedly per');

      cy.get(`[data-cy="createdDate"]`).type('2025-12-04T21:28');
      cy.get(`[data-cy="createdDate"]`).blur();
      cy.get(`[data-cy="createdDate"]`).should('have.value', '2025-12-04T21:28');

      cy.get(`[data-cy="lastModifiedBy"]`).type('nor');
      cy.get(`[data-cy="lastModifiedBy"]`).should('have.value', 'nor');

      cy.get(`[data-cy="lastModifiedDate"]`).type('2025-12-05T02:17');
      cy.get(`[data-cy="lastModifiedDate"]`).blur();
      cy.get(`[data-cy="lastModifiedDate"]`).should('have.value', '2025-12-05T02:17');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        apiKey = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', apiKeyPageUrlPattern);
    });
  });
});
