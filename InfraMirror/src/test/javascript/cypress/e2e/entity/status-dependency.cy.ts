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

describe('StatusDependency e2e test', () => {
  const statusDependencyPageUrl = '/status-dependency';
  const statusDependencyPageUrlPattern = new RegExp('/status-dependency(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const statusDependencySample = {
    parentType: 'who buck geez',
    parentId: 7754,
    childType: 'stiffen',
    childId: 1809,
    createdAt: '2025-12-04T12:17:52.709Z',
  };

  let statusDependency;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/status-dependencies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/status-dependencies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/status-dependencies/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (statusDependency) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/status-dependencies/${statusDependency.id}`,
      }).then(() => {
        statusDependency = undefined;
      });
    }
  });

  it('StatusDependencies menu should load StatusDependencies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('status-dependency');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StatusDependency').should('exist');
    cy.url().should('match', statusDependencyPageUrlPattern);
  });

  describe('StatusDependency page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(statusDependencyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StatusDependency page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/status-dependency/new$'));
        cy.getEntityCreateUpdateHeading('StatusDependency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusDependencyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/status-dependencies',
          body: statusDependencySample,
        }).then(({ body }) => {
          statusDependency = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/status-dependencies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/status-dependencies?page=0&size=20>; rel="last",<http://localhost/api/status-dependencies?page=0&size=20>; rel="first"',
              },
              body: [statusDependency],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(statusDependencyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StatusDependency page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('statusDependency');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusDependencyPageUrlPattern);
      });

      it('edit button click should load edit StatusDependency page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StatusDependency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusDependencyPageUrlPattern);
      });

      it('edit button click should load edit StatusDependency page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StatusDependency');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusDependencyPageUrlPattern);
      });

      it('last delete button click should delete instance of StatusDependency', () => {
        cy.intercept('GET', '/api/status-dependencies/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('statusDependency').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusDependencyPageUrlPattern);

        statusDependency = undefined;
      });
    });
  });

  describe('new StatusDependency page', () => {
    beforeEach(() => {
      cy.visit(`${statusDependencyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StatusDependency');
    });

    it('should create an instance of StatusDependency', () => {
      cy.get(`[data-cy="parentType"]`).type('possession');
      cy.get(`[data-cy="parentType"]`).should('have.value', 'possession');

      cy.get(`[data-cy="parentId"]`).type('21814');
      cy.get(`[data-cy="parentId"]`).should('have.value', '21814');

      cy.get(`[data-cy="childType"]`).type('pigsty honestly although');
      cy.get(`[data-cy="childType"]`).should('have.value', 'pigsty honestly although');

      cy.get(`[data-cy="childId"]`).type('30736');
      cy.get(`[data-cy="childId"]`).should('have.value', '30736');

      cy.get(`[data-cy="metadata"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="metadata"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T13:03');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T13:03');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        statusDependency = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', statusDependencyPageUrlPattern);
    });
  });
});
