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

describe('Datacenter e2e test', () => {
  const datacenterPageUrl = '/datacenter';
  const datacenterPageUrlPattern = new RegExp('/datacenter(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const datacenterSample = { code: 'sorrowful', name: 'yum' };

  let datacenter;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/datacenters+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/datacenters').as('postEntityRequest');
    cy.intercept('DELETE', '/api/datacenters/*').as('deleteEntityRequest');
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

  it('Datacenters menu should load Datacenters page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('datacenter');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Datacenter').should('exist');
    cy.url().should('match', datacenterPageUrlPattern);
  });

  describe('Datacenter page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(datacenterPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Datacenter page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/datacenter/new$'));
        cy.getEntityCreateUpdateHeading('Datacenter');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', datacenterPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/datacenters',
          body: datacenterSample,
        }).then(({ body }) => {
          datacenter = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/datacenters+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/datacenters?page=0&size=20>; rel="last",<http://localhost/api/datacenters?page=0&size=20>; rel="first"',
              },
              body: [datacenter],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(datacenterPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Datacenter page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('datacenter');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', datacenterPageUrlPattern);
      });

      it('edit button click should load edit Datacenter page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Datacenter');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', datacenterPageUrlPattern);
      });

      it('edit button click should load edit Datacenter page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Datacenter');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', datacenterPageUrlPattern);
      });

      it('last delete button click should delete instance of Datacenter', () => {
        cy.intercept('GET', '/api/datacenters/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('datacenter').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', datacenterPageUrlPattern);

        datacenter = undefined;
      });
    });
  });

  describe('new Datacenter page', () => {
    beforeEach(() => {
      cy.visit(`${datacenterPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Datacenter');
    });

    it('should create an instance of Datacenter', () => {
      cy.get(`[data-cy="code"]`).type('lightly by');
      cy.get(`[data-cy="code"]`).should('have.value', 'lightly by');

      cy.get(`[data-cy="name"]`).type('usable');
      cy.get(`[data-cy="name"]`).should('have.value', 'usable');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        datacenter = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', datacenterPageUrlPattern);
    });
  });
});
